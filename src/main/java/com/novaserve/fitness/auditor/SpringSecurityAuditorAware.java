/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.auditor;

import com.novaserve.fitness.auth.service.AuthUtil;
import com.novaserve.fitness.profiles.model.UserBase;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SpringSecurityAuditorAware implements AuditorAware<String> {
    @Autowired
    private AuthUtil authUtil;

    @Override
    public Optional<String> getCurrentAuditor() {
        Optional<UserBase> userOptional =
                authUtil.getUserFromAuth(SecurityContextHolder.getContext().getAuthentication());
        if (userOptional.isPresent()) {
            UserBase user = userOptional.get();
            String userInfo = user.getRoleName() + " " + user.getFullName();
            return Optional.of(userInfo);
        }
        return Optional.empty();
    }
}
