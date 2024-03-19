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
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.HashMap;
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
    private UserSubjectRepository userSubjectRepository;
    @Autowired
    private SubjectService subjectService;

    @Autowired
    private SubjectRepository subjectRepository;

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserSubjectService userSubjectService;




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
        return "teacher/teacher-subjects";
    }


    @PostMapping("/{userId}/changeDecision")
    public String changeDecision(@PathVariable Long userId, HttpServletRequest request) {
        Map<String, String[]> parameters = request.getParameterMap();
        for (String key : parameters.keySet()) {
            if (key.startsWith("decision-")) {
                Long userSubjectId = Long.parseLong(key.split("-")[1]);
                String decisionValue = parameters.get(key)[0];
                UserSubject userSubject = userSubjectRepository.findById(userSubjectId).orElseThrow();
                userSubject.setDecision(Decision.valueOf(decisionValue));
                userSubjectRepository.save(userSubject);
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
                Long userSubjectId = Long.parseLong(key.replaceAll("\\D+", "")); // Extract numeric ID
                UserSubject userSubject = userSubjectRepository.findById(userSubjectId).orElseThrow();

                if (key.startsWith("minLab")) {
                    userSubject.setMinLab(Integer.parseInt(value));
                } else if (key.startsWith("maxLab")) {
                    userSubject.setMaxLab(Integer.parseInt(value));
                }

                userSubjectRepository.save(userSubject);
            }
        });

        redirectAttributes.addFlashAttribute("success", "Labs updated successfully.");
        return "redirect:/teacher/" + userId;
    }


}
