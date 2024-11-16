/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.auth.service.impl;

import com.novaserve.fitness.auth.service.AuthUtil;
import com.novaserve.fitness.profiles.model.UserBase;
import com.novaserve.fitness.profiles.repository.UserRepository;
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
    public Long getUserIdFromAuth(Authentication authentication) {
        return getUserFromAuth(authentication).map(UserBase::getId).orElse(null);
    }

    /**
     * @apiNote UserBase principal =
     *          authUtil.getUserFromAuth(
     *              SecurityContextHolder.getContext().getAuthentication());
     */
    @Override
    public Optional<UserBase> getUserFromAuth(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        String username = null;
        String principalClassName = authentication.getPrincipal().getClass().getName();

        if ("org.springframework.security.core.userdetails.User".equals(principalClassName)) {
            username = ((org.springframework.security.core.userdetails.User) (authentication.getPrincipal()))
                    .getUsername();
        } else if ("com.novaserve.fitness.users.model.UserBase".equals(principalClassName)) {
            username = ((UserBase) (authentication.getPrincipal())).getUsername();
        }

        Optional<UserBase> userOptional = userRepository.findByUsername(username);
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
