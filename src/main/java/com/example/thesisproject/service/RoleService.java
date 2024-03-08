package com.example.thesisproject.service;

import com.example.thesisproject.datamodel.entity.Role;

public interface RoleService {

    Role loadRoleByName(String roleName);

    Role createRole(String roleName);
}
