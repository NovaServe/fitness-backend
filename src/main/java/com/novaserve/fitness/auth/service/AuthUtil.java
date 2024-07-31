/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.auth.service;

import com.novaserve.fitness.users.model.User;
import java.util.Date;
import java.util.Optional;
import org.springframework.security.core.Authentication;

public interface AuthUtil {
    Long getPrincipalId(Authentication auth);

    Optional<User> getPrincipal(Authentication auth);

    String formatCookieExpires(Date date);
}
