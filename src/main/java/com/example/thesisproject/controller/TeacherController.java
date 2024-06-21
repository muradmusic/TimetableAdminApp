package com.example.thesisproject.controller;


import com.example.thesisproject.datamodel.entity.Course;
import com.example.thesisproject.datamodel.entity.User;
import com.example.thesisproject.datamodel.entity.UserCourse;
import com.example.thesisproject.datamodel.enums.Decision;
import com.example.thesisproject.datamodel.enums.TeachingType;
import com.example.thesisproject.repository.CourseRepository;
import com.example.thesisproject.repository.UserRepository;
import com.example.thesisproject.repository.UserCourseRepository;
import com.example.thesisproject.service.CourseService;
import com.example.thesisproject.service.UserService;
import com.example.thesisproject.service.UserCourseService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/teacher")
@RequiredArgsConstructor
public class TeacherController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserCourseRepository userCourseRepository;
    @Autowired
    private CourseService courseService;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/{userId}")
    public String renderUserPage(@PathVariable Long userId, Model model) {


        List<Course> courses = courseService.fetchCourses();
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not Found with id " + userId));

        List<TeachingType> allTeachingTypes = Arrays.asList(TeachingType.values());
        List<UserCourse> userCourses = userCourseRepository.findUserCourseByUserId(userId);


        model.addAttribute("user", user);
        model.addAttribute("courses", courses);
        model.addAttribute("newRecord", new UserCourse());
        model.addAttribute("user_courses", userCourses);
        model.addAttribute("allTeachingTypes", allTeachingTypes);


        log.info("Fetched courses: {}", courses);
        log.info("Fetched user courses: {}", userCourses);
        return "teacher/teacher-courses";
    }


    @PostMapping("/{userId}/changeDecision")
    public String changeDecision(@PathVariable Long userId, HttpServletRequest request) {
        Map<String, String[]> parameters = request.getParameterMap();
        for (String key : parameters.keySet()) {
            if (key.startsWith("decision-")) {
                Long userCourseId = Long.parseLong(key.split("-")[1]);
                String decisionValue = parameters.get(key)[0];
                UserCourse userCourse = userCourseRepository.findById(userCourseId).orElseThrow();
                userCourse.setDecision(Decision.valueOf(decisionValue));
                userCourseRepository.save(userCourse);
            }
        }
        return "redirect:/teacher/{userId}";
    }


    @PostMapping("/{userId}/labs")
    public String updateLabs(@PathVariable Long userId,
                             @RequestParam Map<String, String> allParams,
                             RedirectAttributes redirectAttributes) {
        allParams.forEach((key, value) -> {
            if (key.startsWith("minLab[") || key.startsWith("maxLab[")) {
                Long userCourseId = Long.parseLong(key.replaceAll("\\D+", "")); // Extract numeric ID
                UserCourse userCourse = userCourseRepository.findById(userCourseId).orElseThrow();

                if (key.startsWith("minLab")) {
                    userCourse.setMinLab(Integer.parseInt(value));
                } else if (key.startsWith("maxLab")) {
                    userCourse.setMaxLab(Integer.parseInt(value));
                }

                userCourseRepository.save(userCourse);
            }
        });

        redirectAttributes.addFlashAttribute("success", "Labs updated successfully.");
        return "redirect:/teacher/" + userId;
    }


}
