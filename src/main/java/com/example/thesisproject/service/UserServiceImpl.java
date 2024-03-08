package com.example.thesisproject.service;

import com.example.thesisproject.datamodel.entity.Role;
import com.example.thesisproject.datamodel.entity.User;
//import com.example.thesisproject.repository.RoleRepository;
import com.example.thesisproject.repository.RoleRepository;
import com.example.thesisproject.repository.UserRepository;
import com.example.thesisproject.service.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class UserServiceImpl implements UserService{

    private  UserRepository userRepository;


    private RoleRepository roleRepository;

    private PasswordEncoder passwordEncoder;

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Override
    public void assignRoleToUser(String username, String roleName) {
        User user = userRepository.findUserByUsername(username);
        Role role = roleRepository.findByName(roleName);
        user.getRoles().add(role);
    }



    @Override
    public void createUser(User user) {
    User existingUser = userRepository.findUserByUsername(user.getUsername());

//        Role userRole = roleRepository.findByName("ROLE_USER");
//        // Assign the "USER" role to the new user
//        List<Role> roles = new ArrayList<>();
//        roles.add(userRole);
//        existingUser.setRoles(roles);

    if (existingUser == null) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        User user1 = new User(user.getUsername(), encodedPassword);

//        assignRoleToUser(user.getUsername(), "User");
        userRepository.save(user1);
    } else {
        System.out.println("User with username " + user.getUsername() + " already exists.");
    }
}
    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;

    }

    @Override
    public List<User> fetchUsers() {
        return userRepository.findAll();
    }


}
