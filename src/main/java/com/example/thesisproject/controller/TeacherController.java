package com.example.thesisproject.controller;

import com.example.thesisproject.datamodel.entity.Course;
import com.example.thesisproject.datamodel.entity.User;
import com.example.thesisproject.datamodel.entity.UserCourse;
import com.example.thesisproject.datamodel.enums.TeachingType;
import com.example.thesisproject.repository.UserCourseRepository;
import com.example.thesisproject.service.CourseService;
import com.example.thesisproject.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
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
    private UserCourseRepository userCourseRepository;
    @Autowired
    private CourseService courseService;

    @GetMapping("/{userId}")
    public String renderUserPage(@PathVariable Long userId, Model model) {


        List<Course> courses = courseService.fetchCourses();
        User user = userService.getUserById(userId);
        List<TeachingType> allTeachingTypes = Arrays.asList(TeachingType.values());
        List<UserCourse> userCourses = userCourseRepository.findUserCourseByUserId(userId);


        model.addAttribute("user", user);
        model.addAttribute("courses", courses);
        model.addAttribute("newRecord", new UserCourse());
        model.addAttribute("user_courses", userCourses);
        model.addAttribute("allTeachingTypes", allTeachingTypes);

        return "teacher/teacher-courses";
    }


    @PostMapping("/{userId}/changeDecision")
    public String changeDecision(@PathVariable Long userId, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        Map<String, String[]> parameters = request.getParameterMap();
        userService.changeDecision(userId, parameters);
        redirectAttributes.addAttribute("userId", userId);
        return "redirect:/teacher/{userId}";
    }


    @PostMapping("/{userId}/labs")
    public String updateLabs(@PathVariable Long userId,
                             @RequestParam Map<String, String> allParams,
                             RedirectAttributes redirectAttributes) {

        userService.updateLabs(userId, allParams, redirectAttributes);
        return "redirect:/teacher/" + userId;
    }


}
