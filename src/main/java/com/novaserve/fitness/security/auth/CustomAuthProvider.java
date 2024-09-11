/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.security.auth;

import com.novaserve.fitness.exception.ExceptionMessage;
import com.novaserve.fitness.users.model.User;
import com.novaserve.fitness.users.repository.UserRepository;
import java.util.Set;
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
    private final UserRepository userRepository;

    private final SecurityUtil securityUtil;

    private final PasswordEncoder passwordEncoder;

    public CustomAuthProvider(
            UserRepository userRepository, SecurityUtil securityUtil, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.securityUtil = securityUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        String principal = auth.getPrincipal().toString();
        String credentials = auth.getCredentials().toString();

        User user = userRepository
                .findByUsernameOrEmailOrPhone(principal, principal, principal)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with username, email, or phone: " + principal));

        if (!passwordEncoder.matches(credentials, user.getPassword())) {
            throw new BadCredentialsException(ExceptionMessage.INVALID_CREDENTIALS.getName());
        }

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(
                        user, credentials, securityUtil.mapRolesToAuthorities(Set.of(user.getRole())));

        return usernamePasswordAuthenticationToken;
    }

    @Override
    public boolean supports(Class<?> auth) {
        return auth.equals(UsernamePasswordAuthenticationToken.class);
    }
}
