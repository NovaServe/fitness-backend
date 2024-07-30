/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.users.service;

import com.novaserve.fitness.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    public UserRepository userRepository;

    @Override
    public boolean hasRoleSuperAdmin(long id) {
        return userRepository
                .findById(id)
                .map(user -> user.getRole().getName().equals("ROLE_SUPERADMIN"))
                .orElse(false);
    }

    @Override
    public boolean hasRoleAdmin(long id) {
        return userRepository
                .findById(id)
                .map(user -> user.getRole().getName().equals("ROLE_ADMIN"))
                .orElse(false);
    }

    @Override
    public boolean hasRoleInstructor(long id) {
        return userRepository
                .findById(id)
                .map(user -> user.getRole().getName().equals("ROLE_INSTRUCTOR"))
                .orElse(false);
    }

    @Override
    public boolean hasRoleCustomer(long id) {
        return userRepository
                .findById(id)
                .map(user -> user.getRole().getName().equals("ROLE_CUSTOMER"))
                .orElse(false);
    }
}
