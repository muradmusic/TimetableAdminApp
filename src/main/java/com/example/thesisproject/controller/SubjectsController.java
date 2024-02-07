package com.example.thesisproject.controller;

import com.example.thesisproject.datamodel.entity.User;
import com.example.thesisproject.datamodel.entity.UserSubject;
import com.example.thesisproject.datamodel.enums.TeachingType;
import com.example.thesisproject.repository.SubjectRepository;
import com.example.thesisproject.repository.UserRepository;
import com.example.thesisproject.repository.UserSubjectRepository;
import com.example.thesisproject.service.SubjectService;
import com.example.thesisproject.service.UserService;
import jakarta.persistence.EntityNotFoundException;
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

import java.util.Arrays;
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
    private UserRepository userRepository;

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
        model.addAttribute("subjects", subjects);


        log.info("Fetched subjects: {}", subjects);
        return "subjects/all-subjects";
    }

    @GetMapping("/{subjectId}")
    public String renderSubjectsPage(@PathVariable Long subjectId,  Model model) {

        log.info("ActionLog.renderSubjectsPage.start with subjectId: {}", subjectId);

        List<User> users = userService.fetchUsers();
        Subject subject = subjectRepository.findById(subjectId).orElseThrow();

        List<UserSubject> userSubjects = userSubjectRepository.findUserSubjectsBySubjectId(subject.getId());
        List<TeachingType> allTeachingTypes = Arrays.asList(TeachingType.values());

        model.addAttribute("users", users);
        model.addAttribute("user_subjects", userSubjects );
        model.addAttribute("subject", subject);
        model.addAttribute("allTeachingTypes", allTeachingTypes);
        model.addAttribute("newRecord" , new UserSubject());



        log.info("Fetched users: {}", users);
        log.info("Fetched user subjects: {}", userSubjects);
        log.info("ActionLog.renderSubjectsPage.end with subjectId: {}", subjectId);
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

        return "redirect:/subjects/all";
    }


    @PostMapping("/{subjectId}/delete")
    public String deleteSubject(@PathVariable Long subjectId) {


        if (subjectRepository.existsById(subjectId)) {

            subjectRepository.deleteById(subjectId);
            return "redirect:/subjects/all";

        } else {
            throw new EntityNotFoundException("Subject with ID " + subjectId + " not found");
        }
    }

    @PostMapping("/{subjectId}")
    public String addUserSubject(@PathVariable Long subjectId , @RequestParam Long userId, @RequestParam TeachingType teachingType, @ModelAttribute UserSubject userSubject, BindingResult result, Model model) {

        if (result.hasErrors()) {
            System.out.println("error occurred");
        }
        User user = userRepository.findById(userId).orElseThrow();
        Subject subject = subjectRepository.findById(subjectId).orElseThrow();

        boolean alreadyHasSubject = userSubjectRepository.existsByUserAndSubjectAndTeachingType(user, subject, teachingType);


        if (alreadyHasSubject) {
            System.out.println("User already has this subject");
            return "redirect:/subjects/{subjectId}";
        }

        userSubject.setUser(user);
        userSubject.setSubject(subject);

        userRepository.save(user);
        subjectRepository.save(subject);
        userSubjectRepository.save(userSubject);

        return "redirect:/subjects/{subjectId}";
    }


}
