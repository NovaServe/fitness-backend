/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.security.auth;

import com.novaserve.fitness.users.model.User;
import com.novaserve.fitness.users.service.UserUtil;
import java.util.Set;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetails implements UserDetailsService {
    private final UserUtil userUtil;

    private final SecurityUtil securityUtil;

    public CustomUserDetails(UserUtil userUtil, SecurityUtil securityUtil) {
        this.userUtil = userUtil;
        this.securityUtil = securityUtil;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) {
        User user = userUtil.getUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format("User not found with provided username, email, or phone: %s", username)));

        org.springframework.security.core.userdetails.User userDetailsImpl =
                new org.springframework.security.core.userdetails.User(
                        user.getUsername(),
                        user.getPassword(),
                        securityUtil.mapRolesToAuthorities(Set.of(user.getRole())));

        return userDetailsImpl;
    }
}
