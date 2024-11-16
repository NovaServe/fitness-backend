/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.profiles.aspect;

import com.novaserve.fitness.profiles.model.Role;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RolesConfig {
    PrincipalToUsersRolesMapping[] principalToUsersRolesMapping() default {};

    @interface PrincipalToUsersRolesMapping {
        Role principalRole();

        Role[] usersRoles();
    }
}
