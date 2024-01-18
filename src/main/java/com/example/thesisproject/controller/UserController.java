package com.example.thesisproject.controller;

import com.example.thesisproject.datamodel.entity.Subject;
import com.example.thesisproject.datamodel.entity.User;
import com.example.thesisproject.datamodel.entity.UserSubject;
import com.example.thesisproject.datamodel.enums.TeachingType;
import com.example.thesisproject.repository.SubjectRepository;
import com.example.thesisproject.repository.UserRepository;
import com.example.thesisproject.repository.UserSubjectRepository;
import com.example.thesisproject.service.SubjectService;
import com.example.thesisproject.service.UserService;
import com.example.thesisproject.service.UserSubjectService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;


@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSubjectRepository userSubjectRepository;
    @Autowired
    private SubjectService subjectService;

    @Autowired
    private SubjectRepository subjectRepository;




    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserSubjectService userSubjectService;


    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public String renderSubjectsPage(@PathVariable Long userId, Model model) {

        List<Subject> subjects = subjectService.fetchSubjects();


        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not Found with id " + userId));

        List<TeachingType> allTeachingTypes = Arrays.asList(TeachingType.values());
        model.addAttribute("allTeachingTypes", allTeachingTypes);

        List<UserSubject> userSubjects = userSubjectRepository.findUserSubjectByUserId(userId);

        model.addAttribute("user", user);
        model.addAttribute("subjects", subjects);
        model.addAttribute("newRecord" , new UserSubject());
        model.addAttribute("user_subjects", userSubjects );




        log.info("Fetched subjects: {}", subjects);
        log.info("Fetched user subjects: {}", userSubjects);
        return "users/user";
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

        return "redirect:/users/all";
    }
    @GetMapping("{userId}/assign")
    public String showAssignSubjectForm(@PathVariable Long userId, Model model){


        User user = userRepository.findById(userId).orElseThrow();

//        List<TeachingType> teachingTypes = teachingTypeRepository.findAll();


        List<Subject> subjects = subjectService.fetchSubjects();

        model.addAttribute("subjects", subjects);
        model.addAttribute("user" , user);
//        model.addAttribute("teachingTypes", teachingTypes);

        return "users/assignSubject";
    }


    @PostMapping("/{userId}")
    public String addUserSubject(@PathVariable Long userId , @RequestParam Long subjectId, @RequestParam TeachingType teachingType, @ModelAttribute UserSubject userSubject, BindingResult result, Model model) {

        if (result.hasErrors()) {
            System.out.println("error occurred");
        }
        User user = userRepository.findById(userId).orElseThrow();
        Subject subject = subjectRepository.findById(subjectId).orElseThrow();

        boolean alreadyHasSubject = userSubjectRepository.existsByUserAndSubjectAndTeachingType(user, subject, teachingType);


        if (alreadyHasSubject) {
            System.out.println("User already has this subject");
            return "redirect:/users/{userId}";
        }


        userSubject.setUser(user);
        userSubject.setSubject(subject);

        userRepository.save(user);
        subjectRepository.save(subject);
        userSubjectRepository.save(userSubject);



        return "redirect:/users/{userId}";    }



}
