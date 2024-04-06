package com.example.thesisproject.service;

import com.example.thesisproject.datamodel.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {


    List<User> fetchUsers();

    void createUser(User user);
//    void createTeacher(User user);

    void assignRoleToUser(String username, String roleName);

    boolean doesCurrentUserHasRole(String roleName);

    void deleteUser(Long userId);

}
