/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.security.auth;

import com.novaserve.fitness.profiles.model.Role;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

@Service
public class SecurityUtil {
    public Collection<? extends GrantedAuthority> mapRolesToAuthorities(Set<Role> roles) {
        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .toList();
        return authorities;
    }
}
