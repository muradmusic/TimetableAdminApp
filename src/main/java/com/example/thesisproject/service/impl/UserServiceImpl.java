package com.example.thesisproject.service.impl;

import com.example.thesisproject.datamodel.dto.UserDataDto;
import com.example.thesisproject.datamodel.entity.Course;
import com.example.thesisproject.datamodel.entity.Role;
import com.example.thesisproject.datamodel.entity.User;
//import com.example.thesisproject.repository.RoleRepository;
import com.example.thesisproject.datamodel.entity.UserCourse;
import com.example.thesisproject.datamodel.enums.Decision;
import com.example.thesisproject.datamodel.enums.TeachingType;
import com.example.thesisproject.repository.CourseRepository;
import com.example.thesisproject.repository.RoleRepository;
import com.example.thesisproject.repository.UserRepository;
import com.example.thesisproject.repository.UserCourseRepository;
import com.example.thesisproject.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.*;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private  UserRepository userRepository;
    @Autowired
    private UserCourseRepository userCourseRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, CourseRepository courseRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.courseRepository = courseRepository;
    }
    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id " + id));
    }

    @Override
    public void deleteUser(Long userId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not Found"));

        userRepository.deleteRolesByUserId(userId);

        userCourseRepository.deleteByUserId(userId);

        userRepository.delete(user);
    }


    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Override
    public void assignRoleToUser(String username, String roleName) {
        User user = userRepository.findUserByUsername(username);
        Role role = roleRepository.findByName(roleName);
        user.getRoles().add(role);
        userRepository.save(user);
    }

    @Override
    public boolean createUser(User user) {
        User existingUser = userRepository.findUserByUsername(user.getUsername());
        if (existingUser != null) {
            return false;
        } else {
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            User newUser = new User(user.getUsername(), encodedPassword);
            userRepository.save(newUser);
            return true;
        }
    }

    @Override
    public List<User> fetchUsers() {
        return userRepository.findAll(Sort.by(Sort.Direction.ASC, "username"));

    }

    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    @Override
    public UserDataDto prepareUserPageData(Long userId) {
        List<Course> courses = courseRepository.findAll();
        User user = userRepository.findById(userId).orElse(null);

        List<TeachingType> allTeachingTypes = Arrays.asList(TeachingType.values());
        List<UserCourse> userCourses = userCourseRepository.findUserCourseByUserId(userId);

        Map<String, Map<String, Boolean>> coursesMap = new HashMap<>();
        for (UserCourse userCourse : userCourses) {
            String courseCode = userCourse.getCourse().getCourseCode();
            String teachingType = userCourse.getTeachingType().name();

            Map<String, Boolean> teachingTypes = coursesMap.getOrDefault(courseCode, new HashMap<>());
            teachingTypes.put(teachingType, true);

            coursesMap.put(courseCode, teachingTypes);
        }

        return new UserDataDto(user, userId, courses, userCourses, allTeachingTypes, coursesMap);
    }

    @Override
    public boolean editUser(User user, BindingResult result, RedirectAttributes redirectAttributes) {
        if (!result.hasErrors()) {
            User existingUser = userRepository.findById(user.getId()).orElse(null);
            if (existingUser != null) {
                if (!existingUser.getUsername().equals(user.getUsername()) && userRepository.existsByUsername(user.getUsername())) {
                    return false;
                }
                existingUser.setUsername(user.getUsername());
                if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                    existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
                }
                userRepository.save(existingUser);
                return true;
            }
        }
        return false;
    }

    @Override
    public void deleteUserCourse(Long userId, Long userCourseId) {
        userRepository.deleteRolesByUserId(userId);
        if (userCourseRepository.existsById(userCourseId)) {
            userCourseRepository.deleteById(userCourseId);
        } else {
            throw new EntityNotFoundException("UserCourse with ID " + userCourseId + " not found");
        }
    }

    @Override
    public boolean addUserCourse(Long userId, Long courseId, TeachingType teachingType, UserCourse userCourse, RedirectAttributes redirectAttributes, BindingResult result) {
        if (result.hasErrors()) {
            return false;
        }
        User user = userRepository.findById(userId).orElse(null);
        Course course = courseRepository.findById(courseId).orElse(null);

        if (user == null || course == null) {
            return false;
        }

        boolean existUserWithLabs = userCourseRepository.existsByUserAndCourseAndTeachingType(user, course, TeachingType.LAB);
        if (existUserWithLabs && course.hasLabs() && teachingType != TeachingType.SEMINAR && teachingType != TeachingType.LECTURE) {
            redirectAttributes.addFlashAttribute("LabRecordExists", "Course already has this user with lab class");
            return false;
        }

        userCourse.setUser(user);
        userCourse.setCourse(course);
        userCourse.setDecision(Decision.PENDING);

        userRepository.save(user);
        courseRepository.save(course);
        userCourseRepository.save(userCourse);

        return true;
    }

    @Override
    public void updateLabs(Long userId, Map<String, String> allParams, RedirectAttributes redirectAttributes) {
        Map<UserCourse, List<Integer>> courseLabsMap = new HashMap<>();
        allParams.forEach((key, value) -> {
            if (key.startsWith("minLab[") || key.startsWith("maxLab[")) {
                Long userCourseId = Long.parseLong(key.replaceAll("\\D+", ""));
                UserCourse userCourse = userCourseRepository.findById(userCourseId).orElse(null);
                int labValue = Integer.parseInt(value);
                if (key.startsWith("minLab")) {
                    courseLabsMap.computeIfAbsent(userCourse, k -> new ArrayList<>()).add(0, labValue);
                } else {
                    courseLabsMap.computeIfAbsent(userCourse, k -> new ArrayList<>()).add(1, labValue);
                }
            }
        });

        courseLabsMap.forEach((userCourse, labs) -> {
            int min = labs.get(0);
            int max = labs.get(1);
            userCourse.setMinLab(min);
            userCourse.setMaxLab(Math.max(min, max));
            userCourseRepository.save(userCourse);
        });

        redirectAttributes.addFlashAttribute("success", "Labs updated successfully.");
    }
    @Override
    public void changeDecision(Long userId, Map<String, String[]> parameters) {
        for (String key : parameters.keySet()) {
            if (key.startsWith("decision-")) {
                Long userCourseId = Long.parseLong(key.split("-")[1]);
                String decisionValue = parameters.get(key)[0];
                UserCourse userCourse = userCourseRepository.findById(userCourseId).orElse(null);
                if (userCourse != null) {
                    userCourse.setDecision(Decision.valueOf(decisionValue));
                    userCourseRepository.save(userCourse);
                }
            }
        }
    }


    @Override
    public void updateUserCourses(Long userId, Map<String, String[]> parameters) {
        List<UserCourse> userCourses = userCourseRepository.findUserCourseByUserId(userId);

        for (UserCourse userCourse : userCourses) {
            String key = userCourse.getCourse().getCourseCode() + "-" + userCourse.getTeachingType().name();
            if (!parameters.containsKey(key)) {
                userCourseRepository.delete(userCourse);
            }
        }

        for (String key : parameters.keySet()) {
            String[] parts = key.split("-");
            String courseCode;
            String courseType;

            if (parts.length == 2) {
                courseCode = parts[0];
                courseType = parts[1];
            } else {
                courseCode = parts[0] + '-' + parts[1];
                courseType = parts[2];
            }

            Optional<UserCourse> userCourse = userCourses.stream().filter(
                    c -> c.getCourse().getCourseCode().equals(courseCode)
                            && c.getTeachingType().name().equals(courseType)
            ).findFirst();
            if (userCourse.isPresent()) {
                continue;
            }

            if (parameters.get(key)[0].equals("on")) {
                UserCourse newCourse = new UserCourse();
                newCourse.setCourse(courseRepository.findByCourseCode(courseCode));
                newCourse.setTeachingType(TeachingType.valueOf(courseType));
                newCourse.setUser(userRepository.findById(userId).orElse(null));
                newCourse.setDecision(Decision.PENDING);
                newCourse.setMinLab(0);
                newCourse.setMaxLab(0);
                userCourseRepository.save(newCourse);
            }
        }
    }



}
