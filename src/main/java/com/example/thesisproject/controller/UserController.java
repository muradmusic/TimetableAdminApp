package com.example.thesisproject.controller;

import com.example.thesisproject.datamodel.entity.Subject;
import com.example.thesisproject.datamodel.entity.User;
import com.example.thesisproject.datamodel.entity.UserSubject;
import com.example.thesisproject.datamodel.enums.Decision;
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
    public String renderUserPage(@PathVariable Long userId, Model model) {


        List<Subject> subjects = subjectService.fetchSubjects();
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not Found with id " + userId));

        List<TeachingType> allTeachingTypes = Arrays.asList(TeachingType.values());
        List<UserSubject> userSubjects = userSubjectRepository.findUserSubjectByUserId(userId);

        model.addAttribute("user", user);
        model.addAttribute("subjects", subjects);
        model.addAttribute("newRecord" , new UserSubject());
        model.addAttribute("user_subjects", userSubjects );
        model.addAttribute("allTeachingTypes", allTeachingTypes);

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

@PostMapping("/{userId}/delete")
public String deleteUser(@PathVariable Long userId) {


    if (userRepository.existsById(userId)) {

        userRepository.deleteById(userId);
        return "redirect:/users/all";

    } else {
        throw new EntityNotFoundException("User with ID " + userId + " not found");
    }
}
    @PostMapping("/{userId}/deleteUserSubject")
    public String deleteUserSubject(@PathVariable Long userId, @RequestParam Long userSubjectId) {


        if (userSubjectRepository.existsById(userSubjectId)) {

            userSubjectRepository.deleteById(userSubjectId);
            return "redirect:/users/{userId}";

        } else {
            throw new EntityNotFoundException("UserSubject with ID "  + userSubjectId + " not found");
        }
    }



    @PostMapping("/{userId}")
    public String addUserSubject(@PathVariable Long userId , @RequestParam Long subjectId, @RequestParam TeachingType teachingType, @ModelAttribute UserSubject userSubject, BindingResult result) {

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
        userSubject.setDecision(Decision.PENDING);

        userRepository.save(user);
        subjectRepository.save(subject);
        userSubjectRepository.save(userSubject);

        return "redirect:/users/{userId}";
    }
    @PostMapping("/{userId}/labs")
    public String saveLabs(@PathVariable Long userId, @RequestParam Long subjectId, BindingResult result,Model model){

        if (result.hasErrors()) {
            System.out.println("error occurred");
        }
        User user = userRepository.findById(userId).orElseThrow();

        List<UserSubject> userSubjectList = userSubjectRepository.findUserSubjectByUserId(userId);

        userSubjectService.updateMinMaxValues(userId, userSubjectList);

        return "redirect:/users/{userId}";
    }

//    @PostMapping("/{userId}/changeDecision")
//    public String changeDecision(@PathVariable Long userId , @RequestParam Long subjectId, @ModelAttribute UserSubject userSubject, BindingResult result) {
//
//        if (result.hasErrors()) {
//            System.out.println("error occurred");
//        }
//        User user = userRepository.findById(userId).orElseThrow();
//        Subject subject = subjectRepository.findById(subjectId).orElseThrow();
//
////        boolean alreadyHasSubject = userSubjectRepository.existsByUserAndSubjectAndTeachingType(user, subject, teachingType);
////
////
////        if (alreadyHasSubject) {
////            System.out.println("User already has this subject");
////            return "redirect:/users/{userId}";
////        }
//
//        userSubject.setUser(user);
//        userSubject.setSubject(subject);
//        userSubject.setDecision(Decision.PENDING);
//
//        userRepository.save(user);
//        subjectRepository.save(subject);
//        userSubjectRepository.save(userSubject);
//
//        return "redirect:/users/{userId}";
//    }





}
