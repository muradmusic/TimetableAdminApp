package com.example.thesisproject.service.impl;

import com.example.thesisproject.datamodel.entity.Role;
import com.example.thesisproject.repository.RoleRepository;
import com.example.thesisproject.service.RoleService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Role> getRolesByUserId(Long userId) {
        return roleRepository.findRolesByUserId(userId);
    }


}
