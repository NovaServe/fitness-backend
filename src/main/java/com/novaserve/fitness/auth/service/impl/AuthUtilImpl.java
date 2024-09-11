/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.auth.service.impl;

import com.novaserve.fitness.auth.service.AuthUtil;
import com.novaserve.fitness.users.model.User;
import com.novaserve.fitness.users.repository.UserRepository;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthUtilImpl implements AuthUtil {
    private final UserRepository userRepository;

    public AuthUtilImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Long getUserIdFromAuth(Authentication auth) {
        return getUserFromAuth(auth).map(User::getId).orElse(null);
    }

    /**
     * @apiNote User principal =
     *          authUtil.getUserFromAuth(
     *              SecurityContextHolder.getContext().getAuthentication());
     */
    @Override
    public Optional<User> getUserFromAuth(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return Optional.empty();
        }
        String username = null;
        String principalClassName = auth.getPrincipal().getClass().getName();

        if ("org.springframework.security.core.userdetails.User".equals(principalClassName)) {
            username = ((org.springframework.security.core.userdetails.User) (auth.getPrincipal())).getUsername();
        } else if ("com.novaserve.fitness.users.model.User".equals(principalClassName)) {
            username = ((User) (auth.getPrincipal())).getUsername();
        }

        Optional<User> userOptional = userRepository.findByUsername(username);
        return userOptional;
    }

    @Override
    public String formatCookieExpires(Date date) {
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.of("GMT"));
        DateTimeFormatter dateTimeFormatter =
                DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
        String formattedDate = zonedDateTime.format(dateTimeFormatter);
        return formattedDate;
    }
}
