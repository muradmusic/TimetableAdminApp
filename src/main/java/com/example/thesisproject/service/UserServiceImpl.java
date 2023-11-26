package com.example.thesisproject.service;

import com.example.thesisproject.datamodel.entity.User;
import com.example.thesisproject.repository.UserRepository;
import com.example.thesisproject.service.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService{

    private  UserRepository userRepository;

    @Override
    public void createUser(User user) {
        userRepository.save(user);
    }

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> fetchUsers() {
        return userRepository.findAll();
    }

//    @Override
//    @PostConstruct
//    public void initializeTestData() {
//
//        User user = new User( "guthondr", "password");
//        User user1 = new User( "mammamur", "password");
//
//        userRepository.save(user);
//        userRepository.save(user1);
//
//    }
}
