package com.example.thesisproject.controller;

import com.example.thesisproject.datamodel.entity.User;
import com.example.thesisproject.datamodel.entity.UserCourse;
import com.example.thesisproject.datamodel.enums.Decision;
import com.example.thesisproject.datamodel.enums.TeachingType;
import com.example.thesisproject.repository.CourseRepository;
import com.example.thesisproject.service.CourseService;
import com.example.thesisproject.service.UserService;
import com.example.thesisproject.service.UserCourseService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.thesisproject.datamodel.entity.Course;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CoursesController {



    @Autowired
    private UserCourseService userCourseService;
    @Autowired
    private CourseService courseService;

    @Autowired
    private UserService userService;

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    public CoursesController(UserService userService, CourseService courseService, UserCourseService userCourseService) {
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

        Map<Long, Map<String, Object>> coursesMap = new HashMap<>();

        for (UserCourse userCourse : userCourses) {
            Long userId = userCourse.getUser().getId();
            String teachingType = userCourse.getTeachingType().name();

            Map<String, Object> userDetails = coursesMap.computeIfAbsent(userId, k -> new HashMap<>());

            userDetails.put("minLab", userCourse.getMinLab());
            userDetails.put("maxLab", userCourse.getMaxLab());
            userDetails.put("username", userCourse.getUser().getUsername());


            Map<String, Boolean> teachingTypes = (Map<String, Boolean>) userDetails.computeIfAbsent("teachingTypes", k -> new HashMap<>());
            teachingTypes.put(teachingType, true);
        }

        model.addAttribute("users", users);
        model.addAttribute("courseId", courseId);
        model.addAttribute("course", course);
        model.addAttribute("allTeachingTypes", allTeachingTypes);
        model.addAttribute("coursesMap", coursesMap);
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
        courseService.createCourse(course);
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


    @PostMapping("/update")
    public String updateCourse(@ModelAttribute Course course, RedirectAttributes redirectAttributes) {

        courseService.saveCourse(course);
        redirectAttributes.addFlashAttribute("success", "Course updated successfully.");
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
    public String addUserCourse(@PathVariable Long courseId , @RequestParam Long userId, @RequestParam TeachingType teachingType, @ModelAttribute UserCourse userCourse, BindingResult result, Model model) {

        if (result.hasErrors()) {
            System.out.println("error occurred");
        }
        User user = userService.getUserById(userId);
        Course course = courseService.getCourseById(courseId);

        boolean alreadyHasCourse = userCourseService.existsByUserAndCourseAndTeachingType(user, course, teachingType);

        if (alreadyHasCourse) {
            System.out.println("User already has this course");
            return "redirect:/courses/{courseId}";
        }

        userCourse.setUser(user);
        userCourse.setCourse(course);
        userCourse.setDecision(Decision.PENDING);


        userService.saveUser(user);
        courseService.saveCourse(course);
        userCourseService.saveUserCourse(userCourse);

        return "redirect:/courses/{courseId}";
    }


}
