/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.auth.service;

import com.novaserve.fitness.profiles.model.UserBase;
import java.util.Date;
import java.util.Optional;
import org.springframework.security.core.Authentication;

public interface AuthUtil {
    Long getUserIdFromAuth(Authentication authentication);

    Optional<UserBase> getUserFromAuth(Authentication authentication);

    String formatCookieExpires(Date date);
}
