package com.example.thesisproject.controller;

import com.example.thesisproject.datamodel.dto.CourseDataDto;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    public CourseController(UserService userService, CourseService courseService, UserCourseService userCourseService) {
        this.userService = userService;
        this.courseService = courseService;
        this.userCourseService = userCourseService;
    }

    @GetMapping("/{courseId}")
    public String renderCoursesPage(@PathVariable Long courseId, Model model) {

        CourseDataDto courseData = courseService.prepareCoursePageData(courseId);

        model.addAttribute("users", courseData.users);
        model.addAttribute("course", courseData.course);
        model.addAttribute("allTeachingTypes", courseData.allTeachingTypes);
        model.addAttribute("usersMap", courseData.usersMap);
        model.addAttribute("newRecord", new UserCourse());
        model.addAttribute("user_courses", courseData.userCourses);
        model.addAttribute("currentLectures", courseData.currentLectures);
        model.addAttribute("currentSeminars", courseData.currentSeminars);
        model.addAttribute("currentLabs", courseData.currentLabs);
        model.addAttribute("currentLabSumsMax", courseData.currentLabSumsMax);
        model.addAttribute("currentLabSumsMin", courseData.currentLabSumsMin);

        return "courses/course";
    }

    @GetMapping("/all")
    public String renderUsersPage(Model model) {

        List<Course> courses = courseService.fetchCourses();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userService.findUserByUsername(username);

        model.addAttribute("userId", currentUser.getId());
        model.addAttribute("courses", courses);

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
    public String deleteUserCourse(@PathVariable Long courseId, @RequestParam Long userCourseId) {

        if (userCourseService.existsUserCourse(userCourseId)) {
            userCourseService.deleteUserCourseById(userCourseId);
            return "redirect:/courses/" + courseId;
        } else {
            throw new EntityNotFoundException("UserCourse with ID " + userCourseId + " not found");
        }
    }

    @GetMapping("/{courseId}/edit")
    public String showEditForm(@PathVariable Long courseId, Model model) {

        Course course = courseService.getCourseById(courseId);
        model.addAttribute("course", course);
        return "courses/editCourse";
    }


    @PostMapping("/edit")
    public String updateCourse(@ModelAttribute Course course, BindingResult result, Model model) {

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
    public String updateLabs(@PathVariable Long courseId, @RequestParam Map<String, String> allParams, RedirectAttributes redirectAttributes) {

        courseService.updateLabs(courseId, allParams);
        redirectAttributes.addFlashAttribute("success", "Labs updated successfully.");
        return "redirect:/courses/" + courseId;
    }

    @PostMapping("/{courseId}/changeDecision")
    public String changeDecision(@PathVariable Long courseId, HttpServletRequest request) {

        Map<String, String[]> parameters = request.getParameterMap();
        courseService.changeDecision(courseId, parameters);
        return "redirect:/courses/{courseId}";
    }


    @PostMapping("/{courseId}")
    public String addUserCourse(@PathVariable Long courseId, @RequestParam Long userId, @RequestParam TeachingType teachingType, @ModelAttribute UserCourse userCourse, BindingResult result, RedirectAttributes redirectAttributes) {

        boolean created = courseService.addUserCourse(courseId, userId, teachingType, userCourse, result, redirectAttributes);
        if (!created) {
            return "redirect:/courses/" + courseId;
        }
        return "redirect:/courses/{courseId}";
    }

    @PostMapping("/saveUserCourseChanges/{courseId}")
    public String updateCourses(@PathVariable Long courseId, HttpServletRequest request) throws Exception {

        Map<String, String[]> parameters = request.getParameterMap();
        courseService.updateCourses(courseId, parameters);
        return "redirect:/courses/" + courseId;
    }
}
