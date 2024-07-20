package com.novaserve.fitness.security.auth;

import com.novaserve.fitness.users.model.User;
import com.novaserve.fitness.users.service.UserUtil;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    UserUtil userUtil;

    @Autowired
    SecurityUtil securityUtil;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) {
        User user = userUtil.getUserByUsername(username).orElseThrow(() -> {
            String message = String.format("User not found with provided username, email, or phone: %s", username);
            return new UsernameNotFoundException(message);
        });
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), securityUtil.mapRolesToAuthorities(Set.of(user.getRole())));
    }
}
