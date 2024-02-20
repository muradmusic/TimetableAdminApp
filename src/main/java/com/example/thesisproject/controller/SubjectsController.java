package com.example.thesisproject.controller;

import com.example.thesisproject.datamodel.entity.User;
import com.example.thesisproject.datamodel.entity.UserSubject;
import com.example.thesisproject.datamodel.enums.Decision;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        Map<Long, Integer> currentLabSumsMax = new HashMap<>();
        for (Subject subject : subjects) {
            int sumMaxLab = userSubjectRepository.sumMaxLabBySubjectId(subject.getId()).orElse(0);
            currentLabSumsMax.put(subject.getId(), sumMaxLab);
        }
        Map<Long, Integer> currentLabSumsMin = new HashMap<>();
        for (Subject subject : subjects) {
            int sumMinLab = userSubjectRepository.sumMinLabBySubjectId(subject.getId()).orElse(0);
            currentLabSumsMin.put(subject.getId(), sumMinLab);
        }

        model.addAttribute("subjects", subjects);
        model.addAttribute("currentLabSumsMax", currentLabSumsMax);
        model.addAttribute("currentLabSumsMin", currentLabSumsMin);

        log.info("Fetched subjects: {}", subjects);
        return "subjects/all-subjects";
    }

@GetMapping("/{subjectId}")
public String renderSubjectsPage(@PathVariable Long subjectId, Model model) {

    log.info("ActionLog.renderSubjectsPage.start with subjectId: {}", subjectId);

    List<User> users = userService.fetchUsers();
    Subject subject = subjectRepository.findById(subjectId).orElseThrow();

    List<UserSubject> userSubjects = userSubjectRepository.findUserSubjectsBySubjectId(subject.getId());
    List<TeachingType> allTeachingTypes = Arrays.asList(TeachingType.values());

    Map<Long, Map<String, Object>> subjectsMap = new HashMap<>();

    for (UserSubject userSubject : userSubjects) {
        Long userId = userSubject.getUser().getId();
        String teachingType = userSubject.getTeachingType().name();

        Map<String, Object> userDetails = subjectsMap.computeIfAbsent(userId, k -> new HashMap<>());

        userDetails.put("minLab", userSubject.getMinLab());
        userDetails.put("maxLab", userSubject.getMaxLab());
        userDetails.put("username", userSubject.getUser().getUsername());


        Map<String, Boolean> teachingTypes = (Map<String, Boolean>) userDetails.computeIfAbsent("teachingTypes", k -> new HashMap<>());
        teachingTypes.put(teachingType, true);
    }

    model.addAttribute("users", users);
    model.addAttribute("subject", subject);
    model.addAttribute("allTeachingTypes", allTeachingTypes);
    model.addAttribute("subjectsMap", subjectsMap);
    model.addAttribute("newRecord", new UserSubject());

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

            subjectService.deleteSubject(subjectId);
            return "redirect:/subjects/all";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {

        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid subject Id:" + id));
        model.addAttribute("subject", subject);
        return "subjects/editSubject";
    }

    @PostMapping("/update")
    public String updateSubject(@ModelAttribute Subject subject, RedirectAttributes redirectAttributes) {

        subjectRepository.save(subject);
        redirectAttributes.addFlashAttribute("success", "Subject updated successfully.");
        return "redirect:/subjects/all";
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
        userSubject.setDecision(Decision.PENDING);


        userRepository.save(user);
        subjectRepository.save(subject);
        userSubjectRepository.save(userSubject);

        return "redirect:/subjects/{subjectId}";
    }


}
