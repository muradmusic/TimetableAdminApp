package com.example.thesisproject.service;

import com.example.thesisproject.datamodel.entity.Role;
import com.example.thesisproject.datamodel.entity.User;
import com.example.thesisproject.repository.RoleRepository;
import com.example.thesisproject.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class RoleServiceImpl implements RoleService{

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    public RoleServiceImpl(RoleRepository roleRepository){
        this.roleRepository = roleRepository;
    }

    @Override
    public Role loadRoleByName(String roleName) {
        return roleRepository.findByName(roleName);
    }

    @Override
    public Role createRole(String roleName) {
        return roleRepository.save(new Role(roleName));
    }

//    @Override
//    public List<Role> retrieveRoleByUsername(String username) {
//        User user = userRepository.findUserByUsername(username);
//
//
//        return null;
//    }

    @Override
    public List<Role> retrieveRoleByUsername(String username) {
        User user = userRepository.findUserByUsername(username);
        if (user != null) {
            // If you are using FetchType.LAZY for roles, ensure this is called within a transactional context
            // This ensures the roles are lazily loaded without issues
            return new ArrayList<>(user.getRoles());
        } else {
            // Handle the case where the user does not exist or return an empty list
            return Collections.emptyList();
        }
    }
    public List<Role> getRolesByUserId(Long userId) {
        return roleRepository.findRolesByUserId(userId);
    }


}
