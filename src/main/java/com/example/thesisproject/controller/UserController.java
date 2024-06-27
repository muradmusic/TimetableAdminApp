package com.example.thesisproject.controller;

import com.example.thesisproject.datamodel.dto.UserDataDto;
import com.example.thesisproject.datamodel.entity.*;
import com.example.thesisproject.datamodel.enums.TeachingType;
import com.example.thesisproject.service.RoleService;
import com.example.thesisproject.service.UserService;
import com.example.thesisproject.service.UserCourseService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    private UserCourseService userCourseService;

    @Autowired

    private RoleService roleService;
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/{userId}")
    public String renderUserPage(@PathVariable Long userId, Model model) {

        UserDataDto userData = userService.prepareUserPageData(userId);

        model.addAttribute("user", userData.user);
        model.addAttribute("userId", userData.userId);
        model.addAttribute("courses", userData.courses);
        model.addAttribute("newRecord", new UserCourse());
        model.addAttribute("user_courses", userData.userCourses);
        model.addAttribute("allTeachingTypes", userData.allTeachingTypes);
        model.addAttribute("coursesMap", userData.coursesMap);

        return "users/user";
    }

    @GetMapping("/all")
    public String renderUsersPage(Model model) {

        List<User> users = userService.fetchUsers();
        List<UserCourse> userCourses = userCourseService.fetchUserCourses();
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

//        String username = authentication.getName();
//        User currentUser = userService.findUserByUsername(username);


        model.addAttribute("users", users);
        model.addAttribute("user_courses", userCourses);
//        model.addAttribute("userId", currentUser.getId());

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

        roleService.createRole("ROLE_TEACHER");
        userService.assignRoleToUser(user.getUsername(), "ROLE_TEACHER");
        return "redirect:/users/all";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {

        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "users/editUser";
    }

    @PostMapping("/edit")
    public String editUser(@ModelAttribute User user, BindingResult result, RedirectAttributes redirectAttributes, Model model) {

        boolean created = userService.editUser(user, result, redirectAttributes);
        if (!created) {
            model.addAttribute("usernameExists", "Username '" + user.getUsername() + "' already exists.");
            User existingUser = userService.getUserById(user.getId());
            model.addAttribute("user", existingUser);
            return "users/editUser";
        }

        redirectAttributes.addFlashAttribute("success", "User updated successfully.");
        return "redirect:/users/all";
    }

    @PostMapping("/{userId}/delete")
    public String deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return "redirect:/users/all";
    }

    @PostMapping("/{userId}/deleteUserCourse")
    public String deleteUserCourse(@PathVariable Long userId, @RequestParam Long userCourseId) {

        userService.deleteUserCourse(userId, userCourseId);
        return "redirect:/users/" + userId;
    }

    @PostMapping("/{userId}")
    public String addUserCourse(@PathVariable Long userId, @RequestParam Long courseId, @RequestParam TeachingType teachingType, @ModelAttribute UserCourse userCourse, RedirectAttributes redirectAttributes, BindingResult result) {

        boolean created = userService.addUserCourse(userId, courseId, teachingType, userCourse, redirectAttributes, result);
        if (!created) {
            return "redirect:/courses/" + courseId;
        }
        return "redirect:/users/" + userId;

    }

    @PostMapping("/{userId}/labs")
    public String updateLabs(@PathVariable Long userId, @RequestParam Map<String, String> allParams,
                             RedirectAttributes redirectAttributes) {

        userService.updateLabs(userId, allParams, redirectAttributes);
        return "redirect:/users/" + userId;
    }


    @PostMapping("/{userId}/changeDecision")
    public String changeDecision(@PathVariable Long userId, HttpServletRequest request, RedirectAttributes redirectAttributes) {

        Map<String, String[]> parameters = request.getParameterMap();
        userService.changeDecision(userId, parameters);
        redirectAttributes.addAttribute("userId", userId);
        return "redirect:/users/{userId}";
    }

    @PostMapping("/saveUserCourseChanges/{userId}")
    public String updateCourses(@PathVariable Long userId, HttpServletRequest request) {

        Map<String, String[]> parameters = request.getParameterMap();
        userService.updateUserCourses(userId, parameters);
        return "redirect:/users/{userId}";
    }


}
