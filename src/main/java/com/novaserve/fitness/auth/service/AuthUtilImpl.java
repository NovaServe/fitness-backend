/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.auth.service;

import static java.util.Objects.isNull;

import com.novaserve.fitness.users.model.User;
import com.novaserve.fitness.users.repository.UserRepository;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthUtilImpl implements AuthUtil {
    @Autowired
    UserRepository userRepository;

    @Override
    public Long getUserIdFromAuth(Authentication auth) {
        return getUserFromAuth(auth).map(User::getId).orElse(null);
    }

    @Override
    public Optional<User> getUserFromAuth(Authentication auth) {
        if (isNull(auth) || !auth.isAuthenticated()) {
            return Optional.empty();
        }
        String username = null;
        var principalClassName = auth.getPrincipal().getClass().getName();
        if (principalClassName.equals("org.springframework.security.core.userdetails.User")) {
            username = ((org.springframework.security.core.userdetails.User) (auth.getPrincipal())).getUsername();
        } else if (principalClassName.equals("com.novaserve.fitness.users.model.User")) {
            username = ((User) (auth.getPrincipal())).getUsername();
        }
        return userRepository.findByUsername(username);
    }

    @Override
    public String formatCookieExpires(Date date) {
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.of("GMT"));
        return zonedDateTime.format(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss zzz"));
    }
}
