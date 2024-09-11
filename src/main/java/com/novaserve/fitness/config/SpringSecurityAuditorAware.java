/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.config;

import com.novaserve.fitness.auth.service.AuthUtil;
import com.novaserve.fitness.users.model.User;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SpringSecurityAuditorAware implements AuditorAware<User> {
    @Autowired
    AuthUtil authUtil;

    @Override
    public Optional<User> getCurrentAuditor() {
        return authUtil.getUserFromAuth(SecurityContextHolder.getContext().getAuthentication());
        // Long userId = authUtil.getUserIdFromAuth(SecurityContextHolder.getContext().getAuthentication());
        // return Optional.of(userId);
    }
}
