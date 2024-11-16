/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.profiles.aspect;

import com.novaserve.fitness.auth.service.AuthUtil;
import com.novaserve.fitness.exceptions.ExceptionMessage;
import com.novaserve.fitness.exceptions.ServerException;
import com.novaserve.fitness.profiles.model.UserBase;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RolesConfigAspect {
    private final AuthUtil authUtil;

    private final RolesConfigFactory rolesConfigFactory;

    public RolesConfigAspect(AuthUtil authUtil, RolesConfigFactory rolesConfigFactory) {
        this.authUtil = authUtil;
        this.rolesConfigFactory = rolesConfigFactory;
    }

    @Around("@annotation(rolesConfig)")
    public Object checkRoles(ProceedingJoinPoint joinPoint, RolesConfig rolesConfig) throws Throwable {
        UserBase principal = authUtil.getUserFromAuth(
                        SecurityContextHolder.getContext().getAuthentication())
                .orElseThrow(() -> new ServerException(ExceptionMessage.UNAUTHORIZED, HttpStatus.UNAUTHORIZED));

        String annotatedMethodName = joinPoint.getSignature().getName();
        RolesConfigStrategy rolesConfigStrategy = rolesConfigFactory.getStrategy(annotatedMethodName);
        rolesConfigStrategy.validateRolesOrThrowException(joinPoint, rolesConfig, principal);
        return joinPoint.proceed();
    }
}
