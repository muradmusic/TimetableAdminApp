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
    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    @Override
public void createUser(User user) {
    User existingUser = userRepository.findUserByUsername(user.getUsername());
    if (existingUser == null) {
        userRepository.save(user);
    } else {
        System.out.println("User with username " + user.getUsername() + " already exists.");
    }
}
    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> fetchUsers() {
        return userRepository.findAll();
    }


}
