package com.example.thesisproject.service;

import com.example.thesisproject.datamodel.entity.User;
import com.example.thesisproject.repository.UserRepository;
import com.example.thesisproject.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService{

    private  UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> fetchUsers() {
        return userRepository.findAll();
    }
}
