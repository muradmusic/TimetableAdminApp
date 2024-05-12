package com.example.thesisproject.service;

import com.example.thesisproject.datamodel.entity.Role;
import com.example.thesisproject.datamodel.entity.User;
//import com.example.thesisproject.repository.RoleRepository;
import com.example.thesisproject.repository.RoleRepository;
import com.example.thesisproject.repository.UserRepository;
import com.example.thesisproject.repository.UserCourseRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService{
    @Override
    public void deleteRolesByUserId(Long userId) {
        userRepository.deleteRolesByUserId(userId);
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
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

    @Autowired
    private  UserRepository userRepository;

    @Autowired
    private UserCourseRepository userCourseRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleService roleService;
    @Autowired
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
        userRepository.save(user);
    }



    @Override
    public void createUser(User user) {
    User existingUser = userRepository.findUserByUsername(user.getUsername());


    if (existingUser == null) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        User user1 = new User(user.getUsername(), encodedPassword);

//        assignRoleToUser(user.getUsername(), "User");
//        assignRoleToUser(user.getUsername(), "ROLE_TEACHER");
//        roleService.createRole("ROLE_TEACHER");
        userRepository.save(user1);
    } else {
        System.out.println("User with username " + user.getUsername() + " already exists.");
    }
}
    @Override
    public boolean doesCurrentUserHasRole(String roleName) {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(roleName));
    }
    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;

    }

    @Override
    public List<User> fetchUsers() {
//        return userRepository.findAll();
        return userRepository.findAll(Sort.by(Sort.Direction.ASC, "username"));

    }


}
