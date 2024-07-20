package com.novaserve.fitness.security.auth;

import com.novaserve.fitness.users.model.User;
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
public class CustomAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    SecurityUtil securityUtil;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String principal = authentication.getPrincipal().toString();
        String credentials = authentication.getCredentials().toString();
        User user = userRepository
                .findByUsernameOrEmailOrPhone(principal, principal, principal)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with username, email, or phone: " + principal));
        if (!passwordEncoder.matches(credentials, user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }
        return new UsernamePasswordAuthenticationToken(
                user, credentials, securityUtil.mapRolesToAuthorities(Set.of(user.getRole())));
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
