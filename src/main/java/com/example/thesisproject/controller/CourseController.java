package com.example.thesisproject.controller;

import com.example.thesisproject.datamodel.entity.User;
import com.example.thesisproject.datamodel.entity.UserCourse;
import com.example.thesisproject.datamodel.enums.Decision;
import com.example.thesisproject.datamodel.enums.TeachingType;
import com.example.thesisproject.service.CourseService;
import com.example.thesisproject.service.UserService;
import com.example.thesisproject.service.UserCourseService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.thesisproject.datamodel.entity.Course;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {



    @Autowired
    private UserCourseService userCourseService;
    @Autowired
    private CourseService courseService;

    @Autowired
    private UserService userService;

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    public CourseController(UserService userService, CourseService courseService, UserCourseService userCourseService) {
        this.userService = userService;
        this.courseService = courseService;
        this.userCourseService = userCourseService;
    }

    @GetMapping("/{courseId}")
    public String renderCoursesPage(@PathVariable Long courseId, Model model) {

        log.info("ActionLog.renderCoursesPage.start with courseId: {}", courseId);

        List<User> users = userService.fetchUsers();
        Course course = courseService.getCourseById(courseId);

        List<UserCourse> userCourses = userCourseService.getUserCoursesByCourseId(courseId);
        List<TeachingType> allTeachingTypes = Arrays.asList(TeachingType.values());

        for (UserCourse userCourse : userCourses) {
            if (userCourse.getCourse() == null) {
                log.error("No course associated with UserCourse ID: {}", userCourse.getId());
                continue;
            }
            log.info("Course code: {}", userCourse.getCourse().getCourseCode());
        }
        Map<String, Map<String, Boolean>> usersMap = new HashMap<>();
        for (UserCourse userCourse : userCourses) {
            String username = userCourse.getUser().getUsername();
            String teachingType = userCourse.getTeachingType().name();

            Map<String, Boolean> teachingTypes = usersMap.getOrDefault(username, new HashMap<>());
            teachingTypes.put(teachingType, true); // Mark the teaching type as present

            usersMap.put(username, teachingTypes);
        }


        model.addAttribute("users", users);
        model.addAttribute("courseId", courseId);
        model.addAttribute("course", course);
        model.addAttribute("allTeachingTypes", allTeachingTypes);
        model.addAttribute("usersMap", usersMap);
        model.addAttribute("newRecord", new UserCourse());
        model.addAttribute("user_courses", userCourses);

        log.info("Fetched users: {}", users);
        log.info("Fetched user courses: {}", userCourses);
        log.info("ActionLog.renderCoursesPage.end with courseId: {}", courseId);
        return "courses/course";
    }

    @GetMapping("/all")
    public String renderUsersPage(Model model) {

        List<Course> courses = courseService.fetchCourses();

        Map<Long, Integer> currentLabSumsMax = new HashMap<>();
        for (Course course : courses) {
            int sumMaxLab = userCourseService.sumMaxLabByCourseId(course.getId());
            currentLabSumsMax.put(course.getId(), sumMaxLab);
        }
        Map<Long, Integer> currentLabSumsMin = new HashMap<>();
        for (Course course : courses) {
            int sumMinLab = userCourseService.sumMinLabByCourseId(course.getId());
            currentLabSumsMin.put(course.getId(), sumMinLab);
        }
        for (Course course : courses) {
            List<UserCourse> userCourses = userCourseService.getUserCoursesByCourseId(course.getId());
            boolean approvedAll = !userCourses.isEmpty();

            for (UserCourse userCourse : userCourses) {
                if (userCourse.getDecision() != Decision.YES) {
                    approvedAll = false;
                    break;
                }
            }
            course.setApprovedAll(approvedAll);
            courseService.saveCourse(course);
        }

        model.addAttribute("courses", courses);
        model.addAttribute("currentLabSumsMax", currentLabSumsMax);
        model.addAttribute("currentLabSumsMin", currentLabSumsMin);

        log.info("Fetched courses: {}", courses);
        return "courses/all-courses";
    }


    @GetMapping("/create")
    public String showCreateUserForm(Model model) {

        model.addAttribute("course", new Course());
        return "courses/createCourse";
    }

    @PostMapping("/create")
    public String createCourse(@ModelAttribute("course") @Valid Course course, BindingResult result, Model model) {

        if (result.hasErrors()) {
            return "courses/createCourse";
        }
        boolean created = courseService.createCourse(course);
        if (!created) {
            model.addAttribute("courseCodeExists", "Course with code " + course.getCourseCode() + " already exists.");
            return "courses/createCourse";
        }
        return "redirect:/courses/all";
    }


    @PostMapping("/{courseId}/delete")
    public String deleteCourse(@PathVariable Long courseId) {

            courseService.deleteCourse(courseId);
            return "redirect:/courses/all";
    }
    @PostMapping("/{courseId}/deleteUserCourse")
    public String deleteUserCourse(@PathVariable Long courseId,  @RequestParam Long userCourseId) {

        if (userCourseService.existsUserCourse(userCourseId)) {
            userCourseService.deleteUserCourseById(userCourseId);
            return "redirect:/courses/" + courseId;
        } else {
            throw new EntityNotFoundException("UserCourse with ID "  + userCourseId + " not found");
        }
    }
//check this functionality
    @GetMapping("/{courseId}/edit")
    public String showEditForm(@PathVariable Long courseId, Model model) {

        Course course = courseService.getCourseById(courseId);
        model.addAttribute("course", course);
        return "courses/editCourse";
    }


    @PostMapping("/edit")
    public String updateCourse(@ModelAttribute Course course, BindingResult result, RedirectAttributes redirectAttributes,Model model) {

        if (result.hasErrors()) {
            return "courses/editCourse";
        }

        boolean updated = courseService.updateCourse(course);
        if (!updated) {
            model.addAttribute("courseCodeExists", "Course code '" + course.getCourseCode() + "' already exists for another course.");
            return "courses/editCourse";
        }
        return "redirect:/courses/all";
    }

    @PostMapping("/{courseId}/labs")
    public String updateLabs(@PathVariable Long courseId,
                             @RequestParam Map<String, String> allParams,
                             RedirectAttributes redirectAttributes) {
        allParams.forEach((key, value) -> {
            if (key.startsWith("minLab[") || key.startsWith("maxLab[")) {
                Long userCourseId = Long.parseLong(key.replaceAll("\\D+", "")); // Extract numeric ID
                UserCourse userCourse = userCourseService.getUserCourseById(userCourseId);

                if (key.startsWith("minLab")) {
                    userCourse.setMinLab(Integer.parseInt(value));
                } else if (key.startsWith("maxLab")) {
                    userCourse.setMaxLab(Integer.parseInt(value));
                }
                userCourseService.saveUserCourse(userCourse);
            }
        });

        redirectAttributes.addFlashAttribute("success", "Labs updated successfully.");
        return "redirect:/courses/" + courseId;
    }

    @PostMapping("/{courseId}/changeDecision")
    public String changeDecision(@PathVariable Long courseId, HttpServletRequest request) {
        Map<String, String[]> parameters = request.getParameterMap();
        for (String key : parameters.keySet()) {
            if (key.startsWith("decision-")) {
                Long userCourseId = Long.parseLong(key.split("-")[1]);
                String decisionValue = parameters.get(key)[0];
                UserCourse userCourse = userCourseService.getUserCourseById(userCourseId);
                userCourse.setDecision(Decision.valueOf(decisionValue));
                userCourseService.saveUserCourse(userCourse);
            }
        }
        return "redirect:/courses/{courseId}";
    }


    @PostMapping("/{courseId}")
    public String addUserCourse(@PathVariable Long courseId , @RequestParam Long userId, @RequestParam TeachingType teachingType, @ModelAttribute UserCourse userCourse, BindingResult result,RedirectAttributes redirectAttributes , Model model) {

        if (result.hasErrors()) {
            System.out.println("error occurred");
        }
        User user = userService.getUserById(userId);
        Course course = courseService.getCourseById(courseId);

        boolean alreadyHasCourse = userCourseService.existsByUserAndCourseAndTeachingType(user, course, teachingType);


        if (alreadyHasCourse) {
            redirectAttributes.addFlashAttribute("recordExists", "User already has this course with selected teaching type.");
            System.out.println("User already has this course");
            return "redirect:/courses/" + courseId;
        }

        userCourse.setUser(user);
        userCourse.setCourse(course);
        userCourse.setDecision(Decision.PENDING);

        userService.saveUser(user);
        courseService.saveCourse(course);
        userCourseService.saveUserCourse(userCourse);

        return "redirect:/courses/{courseId}";
    }

    @PostMapping("/saveUserCourseChanges/{courseId}")
    public String updateCourses(@PathVariable Long courseId, HttpServletRequest request, RedirectAttributes redirectAttributes) throws Exception {
        Map<String, String[]> parameters = request.getParameterMap();

        List<UserCourse> userCourses = userCourseService.getUserCoursesByCourseId(courseId);

        for (UserCourse userCourse : userCourses) {
            String key = userCourse.getUser().getUsername() + "-" + userCourse.getTeachingType().name();
            if (!parameters.containsKey(key)) {
                System.out.println(key);
                userCourseService.deleteUserCourse(userCourse);
            }
        }

        for (String key : parameters.keySet()) {
            String[] parts = key.split("-");
            String username;
            String courseType;

            if (parts.length == 2) {
                username = parts[0];
                courseType = parts[1];
            } else {
                username = parts[0] + '-' + parts[1];
                courseType = parts[2];
                System.out.println(username);

            }
            Optional<UserCourse> userCourse = userCourses.stream().filter(
                    c -> c.getUser().getUsername().equals(username)
                            && c.getTeachingType().name().equals(courseType)
            ).findFirst();
            if (userCourse.isPresent()) {
                continue;
            }
            if (parameters.get(key)[0].equals("on")) {
                User user = userService.findUserByUsername(username);
                if (user == null) {
                    throw new Exception("User not found: " + username);
                }

                UserCourse newUserCourse = new UserCourse();
                newUserCourse.setUser(user);
                newUserCourse.setTeachingType(TeachingType.valueOf(courseType));
                newUserCourse.setCourse(courseService.getCourseById(courseId));
                newUserCourse.setDecision(Decision.PENDING);
                newUserCourse.setMinLab(0);
                newUserCourse.setMaxLab(0);
                userCourseService.saveUserCourse(newUserCourse);
            }
        }

        return "redirect:/courses/" + courseId;
    }

}
