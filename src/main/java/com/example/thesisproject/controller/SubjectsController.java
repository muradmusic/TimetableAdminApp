package com.example.thesisproject.controller;

import com.example.thesisproject.datamodel.entity.User;
import com.example.thesisproject.datamodel.entity.UserSubject;
import com.example.thesisproject.repository.SubjectRepository;
import com.example.thesisproject.repository.UserSubjectRepository;
import com.example.thesisproject.service.SubjectService;
import com.example.thesisproject.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.thesisproject.datamodel.entity.Subject;

import java.util.List;

@Controller
@RequestMapping("/subjects")
@RequiredArgsConstructor
public class SubjectsController {


    @Autowired
    private UserSubjectRepository userSubjectRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private UserService userService;

    private static final Logger log = LoggerFactory.getLogger(UserController.class);



    public SubjectsController(UserSubjectRepository userSubjectRepository, SubjectService subjectService, UserService userService) {
        this.userSubjectRepository = userSubjectRepository;
        this.subjectService = subjectService;
        this.userService = userService;
    }

    @GetMapping("/all")
    public String renderUsersPage(Model model) {

        List<Subject> subjects = subjectService.fetchSubjects();
//        List<UserSubject> userSubjects = userSubjectService.fetchUserSubjects();
        model.addAttribute("subjects", subjects);
//        model.addAttribute("user_subjects", userSubjects );


        log.info("Fetched subjects: {}", subjects);
//        log.info("Fetched user subjects: {}", userSubjects);
        return "subjects/all-subjects";
    }

    @GetMapping("/{subjectId}")
    public String renderSubjectsPage(@PathVariable Long subjectId,  Model model) {

        List<User> users = userService.fetchUsers();
        Subject subject = subjectRepository.findById(subjectId).orElseThrow();

//        List<UserSubject> userSubjects = userSubjectRepository.findAll();
        List<UserSubject> userSubjects = userSubjectRepository.findUserSubjectsBySubjectId(subject.getId());

        model.addAttribute("users", users);
        model.addAttribute("user_subjects", userSubjects );
        model.addAttribute("subject", subject);


        log.info("Fetched users: {}", users);
        log.info("Fetched user subjects: {}", userSubjects);
        return "subjects/subject";
    }


    @GetMapping("/create")
    public String showCreateUserForm(Model model) {
        model.addAttribute("subject", new Subject());
        return "subjects/createSubject";
    }

    @PostMapping("/create")
    public String createSubject(@ModelAttribute("subject") @Valid Subject subject, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "subjects/createSubject";
        }

        subjectService.createSubject(subject);

        return "redirect:/subjects/all"; // Redirect to the user list page or another appropriate page
    }





}
