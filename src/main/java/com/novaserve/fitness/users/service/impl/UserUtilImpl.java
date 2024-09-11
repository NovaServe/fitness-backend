/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.users.service.impl;

import com.novaserve.fitness.users.model.User;
import com.novaserve.fitness.users.repository.UserRepository;
import com.novaserve.fitness.users.service.UserUtil;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class UserUtilImpl implements UserUtil {
    private final UserRepository userRepository;

    public UserUtilImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> getUserByUsernameOrEmailOrPhone(String username, String email, String phone) {
        return userRepository.findByUsernameOrEmailOrPhone(username, email, phone);
    }
}
