package com.example.thesisproject.controller;

import com.example.thesisproject.datamodel.entity.*;
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
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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

        // Preprocessing to map each subject code to its teaching types
        Map<String, Map<String, Boolean>> subjectsMap = new HashMap<>();
        for (UserSubject userSubject : userSubjects) {
            String subjectCode = userSubject.getSubject().getSubjectCode();
            String teachingType = userSubject.getTeachingType().name();

            Map<String, Boolean> teachingTypes = subjectsMap.getOrDefault(subjectCode, new HashMap<>());
            teachingTypes.put(teachingType, true); // Mark the teaching type as present

            subjectsMap.put(subjectCode, teachingTypes);
        }

        model.addAttribute("user", user);
        model.addAttribute("userId", userId);
        model.addAttribute("subjects", subjects);
        model.addAttribute("newRecord" , new UserSubject());
        model.addAttribute("user_subjects", userSubjects );
        model.addAttribute("allTeachingTypes", allTeachingTypes);
        model.addAttribute("subjectsMap", subjectsMap);


        log.info("Fetched subjects: {}", subjects);
        log.info("Fetched user subjects: {}", userSubjects);
        return "users/user";
    }

    @GetMapping("/all")
//    @PreAuthorize("hasRole('ADMIN')")
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
        userService.assignRoleToUser(user.getUsername(), "ROLE_TEACHER");

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
    public String deleteUserSubject(@PathVariable Long userId,  @RequestParam Long userSubjectId) {


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
    return "redirect:/users/" + userId;
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
        return "redirect:/users/{userId}";
    }

    @PostMapping("/saveUserSubjectChanges/{userId}")
    @Transactional
    public String saveUserSubjectChanges(@PathVariable Long userId, HttpServletRequest request) {

        log.info("Starting processing of userSubject changes for user ID: {}", userId);
        Map<String, UserSubject> currentSubjectsMap = new HashMap<>();


        String[] presentTypes = request.getParameterValues("presentTypes");
        if (presentTypes == null) {
            log.warn("No presentTypes found in the request.");
            return "redirect:/users/" + userId;
        }

        for (String type : presentTypes) {
            String[] parts = type.split("-");
            if (parts.length == 2) {
                String subjectCode = parts[0];
                TeachingType teachingType = TeachingType.valueOf(parts[1]);
                String key = subjectCode + "-" + teachingType.name();

                boolean isChecked = "on".equals(request.getParameter(type));
                log.info("Processing type: {}, isChecked: {}", type, isChecked);

                UserSubject existingAssociation = currentSubjectsMap.get(key);

                if (isChecked) {
                    if (existingAssociation == null) {
                        log.info("Creating new UserSubject for type: {}", type);
                    } else {
                        log.info("Association already exists for type: {}, skipping creation.", type);
                    }
                } else {
                    if (existingAssociation != null) {
                        log.info("Removing UserSubject for type: {}", type);
                        userSubjectRepository.delete(existingAssociation);
                    } else {
                        log.info("No existing association to remove for type: {}", type);
                    }
                }
            }
        }

        log.info("Completed processing of userSubject changes for user ID: {}", userId);
        return "redirect:/users/" + userId;
    }

}
