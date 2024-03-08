package com.example.thesisproject.service;

import com.example.thesisproject.datamodel.entity.Role;
import com.example.thesisproject.repository.RoleRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class RoleServiceImpl implements RoleService{

    private RoleRepository roleRepository;

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

}
