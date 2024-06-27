package com.example.thesisproject.service;

import com.example.thesisproject.datamodel.entity.Role;
import java.util.List;

public interface RoleService {
    List<Role> getRolesByUserId(Long userId);
}
