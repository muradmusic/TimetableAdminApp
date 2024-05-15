package com.example.thesisproject.controller;

import com.example.thesisproject.datamodel.entity.*;
import com.example.thesisproject.datamodel.enums.Decision;
import com.example.thesisproject.datamodel.enums.TeachingType;
import com.example.thesisproject.repository.RoleRepository;
import com.example.thesisproject.repository.CourseRepository;
import com.example.thesisproject.repository.UserRepository;
import com.example.thesisproject.repository.UserCourseRepository;
import com.example.thesisproject.service.CourseService;
import com.example.thesisproject.service.UserService;
import com.example.thesisproject.service.UserCourseService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;


@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserCourseRepository userCourseRepository;
    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseRepository courseRepository;

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserCourseService userCourseService;


    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }



    @GetMapping("/{userId}")
    public String renderUserPage(@PathVariable Long userId, Model model) {



        List<Course> courses = courseService.fetchCourses();
        User user = userService.getUserById(userId);

        List<TeachingType> allTeachingTypes = Arrays.asList(TeachingType.values());
        List<UserCourse> userCourses = userCourseService.getUserCoursesByUserId(userId);

        for (UserCourse userCourse : userCourses) {
            if (userCourse.getCourse() == null) {
                log.error("No course associated with UserCourse ID: {}", userCourse.getId());
                continue;
            }
            log.info("Course code: {}", userCourse.getCourse().getCourseCode());
        }

        // Preprocessing to map each course code to its teaching types
        Map<String, Map<String, Boolean>> coursesMap = new HashMap<>();
        for (UserCourse userCourse : userCourses) {
            String courseCode = userCourse.getCourse().getCourseCode();
            String teachingType = userCourse.getTeachingType().name();

            Map<String, Boolean> teachingTypes = coursesMap.getOrDefault(courseCode, new HashMap<>());
            teachingTypes.put(teachingType, true); // Mark the teaching type as present

            coursesMap.put(courseCode, teachingTypes);
        }

        model.addAttribute("user", user);
        model.addAttribute("userId", userId);
        model.addAttribute("courses", courses);
        model.addAttribute("newRecord" , new UserCourse());
        model.addAttribute("user_courses", userCourses );
        model.addAttribute("allTeachingTypes", allTeachingTypes);
        model.addAttribute("coursesMap", coursesMap);


        log.info("Fetched courses: {}", courses);
        log.info("Fetched user courses: {}", userCourses);
        return "users/user";
    }

    @GetMapping("/all")
    public String renderUsersPage(Model model) {

        List<User> users = userService.fetchUsers();
        List<UserCourse> userCourses = userCourseService.fetchUserCourses();
        model.addAttribute("users", users);
        model.addAttribute("user_courses", userCourses );


        log.info("Fetched users: {}", users);
        log.info("Fetched user courses: {}", userCourses);
        return "users/all-users";
    }

    @GetMapping("/create")
    public String showCreateUserForm(Model model) {
        model.addAttribute("user", new User());
        return "users/createUser";
    }

    @PostMapping("/create")
    public String createUser(@ModelAttribute("user") @Valid User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "users/createUser";
        }

        boolean created = userService.createUser(user);
        if (!created) {
            model.addAttribute("usernameExists", "User with username " + user.getUsername() + " already exists.");
            return "users/createUser";
        }

        userService.assignRoleToUser(user.getUsername(), "ROLE_TEACHER");
        return "redirect:/users/all";
    }


//    @PostMapping("/create")
//    public String createUser(@ModelAttribute("user") @Valid User user, BindingResult result, Model model) {
//        if (result.hasErrors()) {
//            return "users/createUser";
//        }
//
//        userService.createUser(user);
//        userService.assignRoleToUser(user.getUsername(), "ROLE_TEACHER");
//
//        return "redirect:/users/all";
//    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {

        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "users/editUser";
    }
@PostMapping("/edit")
public String editUser(@ModelAttribute User user, BindingResult result, RedirectAttributes redirectAttributes,Model model) {

    if (!result.hasErrors()) {
        User existingUser = userService.getUserById(user.getId());
        if (!existingUser.getUsername().equals(user.getUsername()) && userService.usernameExists(user.getUsername())) {
            model.addAttribute("usernameExists", "Username '" + user.getUsername() + "' already exists.");
            model.addAttribute("user", existingUser);
            return "users/editUser";
        }
        if (existingUser != null) {
            existingUser.setUsername(user.getUsername());
            existingUser.setUsername(user.getUsername());
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                existingUser.setPassword(user.getPassword());
            }
            userService.saveUser(existingUser);
            redirectAttributes.addFlashAttribute("success", "User updated successfully.");
        } else {
            redirectAttributes.addFlashAttribute("error", "User not found.");
        }
    } else {
        redirectAttributes.addFlashAttribute("error", "Error updating user.");
    }
    return "redirect:/users/all";
}

    @PostMapping("/{userId}/delete")
public String deleteUser(@PathVariable Long userId) {
    userService.deleteUser(userId);
    return "redirect:/users/all";
}

    @PostMapping("/{userId}/deleteUserCourse")
    public String deleteUserCourse(@PathVariable Long userId,  @RequestParam Long userCourseId) {
        User user = userService.getUserById(userId);

        userRepository.deleteRolesByUserId(userId);
        if (userCourseService.existsUserCourse(userCourseId)) {

            userCourseService.deleteUserCourseById(userCourseId);
            return "redirect:/users/" + userId;
        } else {
            throw new EntityNotFoundException("UserCourse with ID "  + userCourseId + " not found");
        }
    }



    @PostMapping("/{userId}")
    public String addUserCourse(@PathVariable Long userId , @RequestParam Long courseId, @RequestParam TeachingType teachingType, @ModelAttribute UserCourse userCourse, BindingResult result) {

        if (result.hasErrors()) {
            System.out.println("error occurred");
        }
        User user = userService.getUserById(userId);
        Course course = courseService.getCourseById(courseId);

        boolean alreadyHasCourse = userCourseService.existsByUserAndCourseAndTeachingType(user, course, teachingType);


        if (alreadyHasCourse) {
            System.out.println("User already has this course");
            return "redirect:/users/{userId}";
        }

        userCourse.setUser(user);
        userCourse.setCourse(course);
        userCourse.setDecision(Decision.PENDING);


        userService.saveUser(user);
        courseService.saveCourse(course);
        userCourseService.saveUserCourse(userCourse);

        return "redirect:/users/{userId}";
    }

@PostMapping("/{userId}/labs")
public String updateLabs(@PathVariable Long userId,
                         @RequestParam Map<String, String> allParams,
                         RedirectAttributes redirectAttributes) {
    allParams.forEach((key, value) -> {
        if (key.startsWith("minLab[") || key.startsWith("maxLab[")) {
            Long userCourseId = Long.parseLong(key.replaceAll("\\D+", ""));
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
    return "redirect:/users/" + userId;
}

    @PostMapping("/{userId}/changeDecision")
    public String changeDecision(@PathVariable Long userId, HttpServletRequest request) {
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
        return "redirect:/users/{userId}";
    }


@PostMapping("/saveUserCourseChanges/{userId}")
public String updateCourses(@PathVariable Long userId, HttpServletRequest request, RedirectAttributes redirectAttributes) throws Exception {
    Map<String, String[]> parameters = request.getParameterMap();

    List<UserCourse> userCourses = userCourseService.getUserCoursesByUserId(userId);

    for (UserCourse userCourse1 : userCourses) {
        String key1 = userCourse1.getCourse().getCourseCode() + "-" + userCourse1.getTeachingType().name();
        if (!parameters.containsKey(key1)) {
            System.out.println(key1);
            userCourseService.deleteUserCourse(userCourse1);
        }
    }

    Map<String, List<TeachingType>> courseMap = new HashMap<>();
    for (String key : parameters.keySet()) {
        String[] parts = key.split("-");
        String courseCode;
        String courseType;

        if (parts.length == 2) {
            courseCode = parts[0];
            courseType = parts[1];
        }else {
            courseCode = parts[0] + '-' + parts[1];
            courseType = parts[2];
        }
        Optional<UserCourse> userCourse =  userCourses.stream().filter(
                c -> c.getCourse().getCourseCode().equals(courseCode)
                        && c.getTeachingType().name().equals(courseType)
        ).findFirst();
        if (userCourse.isPresent()) {
            continue;
        }

        if (parameters.get(key)[0].equals("on")) {

            UserCourse newCourse = new UserCourse();
            newCourse.setCourse(courseService.findCourseByCourseCode(courseCode));
            newCourse.setTeachingType(TeachingType.valueOf(courseType));
            newCourse.setUser(userService.getUserById(userId));
            newCourse.setDecision(Decision.PENDING);
            newCourse.setMinLab(0);
            newCourse.setMaxLab(0);
            userCourseService.saveUserCourse(newCourse);
        }
    }


    return "redirect:/users/" + userId;
}




}
