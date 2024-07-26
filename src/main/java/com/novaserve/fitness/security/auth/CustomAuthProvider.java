/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.security.auth;

import com.novaserve.fitness.exception.ExceptionMessage;
import com.novaserve.fitness.users.repository.UserRepository;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthProvider implements AuthenticationProvider {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    SecurityUtil securityUtil;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        var principal = auth.getPrincipal().toString();
        var credentials = auth.getCredentials().toString();
        var user = userRepository
                .findByUsernameOrEmailOrPhone(principal, principal, principal)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with username, email, or phone: " + principal));
        if (!passwordEncoder.matches(credentials, user.getPassword())) {
            throw new BadCredentialsException(ExceptionMessage.INVALID_CREDENTIALS.getName());
        }
        return new UsernamePasswordAuthenticationToken(
                user, credentials, securityUtil.mapRolesToAuthorities(Set.of(user.getRole())));
    }

    @Override
    public boolean supports(Class<?> auth) {
        return auth.equals(UsernamePasswordAuthenticationToken.class);
    }
}
