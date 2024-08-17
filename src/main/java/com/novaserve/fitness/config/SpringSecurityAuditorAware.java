/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.config;

import com.novaserve.fitness.auth.service.AuthUtil;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;

public class SpringSecurityAuditorAware implements AuditorAware<Long> {
    @Autowired
    AuthUtil authUtil;

    @Override
    public Optional<Long> getCurrentAuditor() {
        Long userId =
                authUtil.getUserIdFromAuth(SecurityContextHolder.getContext().getAuthentication());
        return Optional.of(userId);
    }
}
