package com.example.thesisproject.service;

import com.example.thesisproject.datamodel.dto.UserDataDto;
import com.example.thesisproject.datamodel.entity.User;
import com.example.thesisproject.datamodel.entity.UserCourse;
import com.example.thesisproject.datamodel.enums.TeachingType;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Service
public interface UserService {
    User getUserById(Long id);
    List<User> fetchUsers();
    void assignRoleToUser(String username, String roleName);
    void deleteUser(Long userId);
    boolean createUser(User user);
    User findUserByUsername(String username);
    UserDataDto prepareUserPageData(Long userId);
    boolean editUser(User user, BindingResult result, RedirectAttributes redirectAttributes);
    void deleteUserCourse(Long userId, Long userCourseId);
    boolean addUserCourse(Long userId, Long courseId, TeachingType teachingType, UserCourse userCourse, RedirectAttributes redirectAttributes, BindingResult result);
    void updateLabs(Long userId, Map<String, String> allParams, RedirectAttributes redirectAttributes);
    void changeDecision(Long userId, Map<String, String[]> parameters);
    void updateUserCourses(Long userId, Map<String, String[]> parameters);

}