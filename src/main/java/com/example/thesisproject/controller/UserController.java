package com.example.thesisproject.controller;

import com.example.thesisproject.datamodel.entity.User;
import com.example.thesisproject.datamodel.entity.UserSubject;
import com.example.thesisproject.repository.UserSubjectRepository;
import com.example.thesisproject.service.UserService;
import com.example.thesisproject.service.UserSubjectService;
import jakarta.validation.Valid;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;


@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private UserService userService;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserSubjectService userSubjectService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/all")
    public String renderUsersPage(Model model) {

        List<User> users = userService.fetchUsers();
        List<UserSubject> userSubjects = userSubjectService.fetchUserSubjects();
        model.addAttribute("users", users);
        model.addAttribute("user_subjects", userSubjects );


        log.info("Fetched users: {}", users);
        log.info("Fetched user subjects: {}", userSubjects);
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

        userService.createUser(user);

        return "redirect:/users/all"; // Redirect to the user list page or another appropriate page
    }

}
