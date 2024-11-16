/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.profiles.aspect;

import com.novaserve.fitness.auth.service.AuthUtil;
import com.novaserve.fitness.exceptions.ExceptionMessage;
import com.novaserve.fitness.exceptions.ServerException;
import com.novaserve.fitness.exceptions.UserInactiveException;
import com.novaserve.fitness.profiles.model.UserBase;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CheckUserActiveAspect {
    private final AuthUtil authUtil;

    public CheckUserActiveAspect(AuthUtil authUtil) {
        this.authUtil = authUtil;
    }

    @Before("@annotation(CheckUserActive)")
    public void checkUserIsActive() {
        UserBase principal = authUtil.getUserFromAuth(
                        SecurityContextHolder.getContext().getAuthentication())
                .orElseThrow(() -> new ServerException(ExceptionMessage.UNAUTHORIZED, HttpStatus.UNAUTHORIZED));

        if (principal != null && !principal.isActive()) {
            throw new UserInactiveException();
        }
    }
}
