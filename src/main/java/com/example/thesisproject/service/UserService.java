package com.example.thesisproject.service;

import com.example.thesisproject.datamodel.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {


    List<User> fetchUsers();

//    void initializeTestData();
//
    void createUser(User user);

}

