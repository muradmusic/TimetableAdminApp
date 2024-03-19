package com.example.thesisproject.service;

import com.example.thesisproject.datamodel.entity.Role;

import java.util.List;

public interface RoleService {

    Role loadRoleByName(String roleName);

    Role createRole(String roleName);

    List<Role> retrieveRoleByUsername(String username);

    List<Role> getRolesByUserId(Long userId);
}
