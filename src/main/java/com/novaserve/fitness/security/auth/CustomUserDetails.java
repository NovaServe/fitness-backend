/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.security.auth;

import com.novaserve.fitness.profiles.model.UserBase;
import com.novaserve.fitness.profiles.service.ProfileUtil;
import java.util.Set;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetails implements UserDetailsService {
    private final ProfileUtil profileUtil;

    private final SecurityUtil securityUtil;

    public CustomUserDetails(ProfileUtil profileUtil, SecurityUtil securityUtil) {
        this.profileUtil = profileUtil;
        this.securityUtil = securityUtil;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) {
        UserBase user = profileUtil
                .getUserByUsername(username)
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
