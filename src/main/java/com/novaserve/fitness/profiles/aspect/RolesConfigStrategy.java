/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.profiles.aspect;

import com.novaserve.fitness.exceptions.ExceptionMessage;
import com.novaserve.fitness.exceptions.ServerException;
import com.novaserve.fitness.profiles.dto.request.UserCreationBaseDto;
import com.novaserve.fitness.profiles.model.Role;
import com.novaserve.fitness.profiles.model.UserBase;
import com.novaserve.fitness.profiles.service.ProfileUtil;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

public interface RolesConfigStrategy {
    void validateRolesOrThrowException(ProceedingJoinPoint joinPoint, RolesConfig rolesConfig, UserBase principal);

    default void checkRolesMatchOrThrowException(Set<Role> allowedToRequestUsersRoles, Set<Role> requestedUsersRoles) {
        if (allowedToRequestUsersRoles.isEmpty() || !allowedToRequestUsersRoles.containsAll(requestedUsersRoles)) {
            throw new ServerException(ExceptionMessage.ROLES_MISMATCH, HttpStatus.BAD_REQUEST);
        }
    }

    default void checkRolesMatchOrThrowException(Set<Role> allowedToRequestUsersRoles, Role requestedUsersRole) {
        this.checkRolesMatchOrThrowException(allowedToRequestUsersRoles, Set.of(requestedUsersRole));
    }
}

@Component
class CreateUserConfigStrategy implements RolesConfigStrategy {
    @Override
    public void validateRolesOrThrowException(
            ProceedingJoinPoint joinPoint, RolesConfig rolesConfig, UserBase principal) {
        Set<Role> allowedToRequestUsersRoles = new HashSet<>();
        for (RolesConfig.PrincipalToUsersRolesMapping mapping : rolesConfig.principalToUsersRolesMapping()) {
            if (principal.getRole().equals(mapping.principalRole())) {
                allowedToRequestUsersRoles.addAll(Set.of(mapping.usersRoles()));
            }
        }

        UserCreationBaseDto userCreationDto = (UserCreationBaseDto) joinPoint.getArgs()[0];
        Role requestedUsersRole = userCreationDto.getRole();
        this.checkRolesMatchOrThrowException(allowedToRequestUsersRoles, requestedUsersRole);
    }
}

@Component
class GetUserDetailsConfigStrategy implements RolesConfigStrategy {
    private final ProfileUtil profileUtil;

    GetUserDetailsConfigStrategy(ProfileUtil profileUtil) {
        this.profileUtil = profileUtil;
    }

    @Override
    public void validateRolesOrThrowException(
            ProceedingJoinPoint joinPoint, RolesConfig rolesConfig, UserBase principal) {
        long requestedUserId = (long) joinPoint.getArgs()[0];
        UserBase requestedUser = profileUtil.getUserByIdOrThrowNotFound(requestedUserId);
        boolean customerRequestsDetailsOfAnotherCustomer = principal.getRole().equals(Role.ROLE_CUSTOMER)
                && requestedUser.getRole().equals(Role.ROLE_CUSTOMER)
                && !principal.getId().equals(requestedUser.getId());
        if (customerRequestsDetailsOfAnotherCustomer) {
            throw new ServerException(ExceptionMessage.ROLES_MISMATCH, HttpStatus.BAD_REQUEST);
        }

        Role requestedUsersRole = requestedUser.getRole();
        Set<Role> allowedToRequestUsersRoles = new HashSet<>();

        for (RolesConfig.PrincipalToUsersRolesMapping mapping : rolesConfig.principalToUsersRolesMapping()) {
            if (principal.getRole().equals(mapping.principalRole())) {
                allowedToRequestUsersRoles.addAll(Set.of(mapping.usersRoles()));
            }
        }

        this.checkRolesMatchOrThrowException(allowedToRequestUsersRoles, requestedUsersRole);
    }
}

@Component
class GetUsersConfigStrategy implements RolesConfigStrategy {
    @Override
    public void validateRolesOrThrowException(
            ProceedingJoinPoint joinPoint, RolesConfig rolesConfig, UserBase principal) {
        Set<Role> allowedToRequestUsersRoles = new HashSet<>();
        for (RolesConfig.PrincipalToUsersRolesMapping mapping : rolesConfig.principalToUsersRolesMapping()) {
            if (principal.getRole().equals(mapping.principalRole())) {
                allowedToRequestUsersRoles.addAll(Set.of(mapping.usersRoles()));
            }
        }

        Set<Role> requestedUsersRoles = new HashSet<>();
        if (joinPoint.getArgs()[0] instanceof Set<?> set) {
            for (Object s : set) {
                if (s instanceof Role role) {
                    requestedUsersRoles.add(role);
                }
            }
        } else {
            throw new ClassCastException();
        }

        this.checkRolesMatchOrThrowException(allowedToRequestUsersRoles, requestedUsersRoles);
    }
}

@Component
class RolesConfigFactory {
    private final CreateUserConfigStrategy createUserConfigStrategy;

    private final GetUserDetailsConfigStrategy getUserDetailsConfigStrategy;

    private final GetUsersConfigStrategy getUsersConfigStrategy;

    private final Map<String, RolesConfigStrategy> strategies;

    RolesConfigFactory(
            CreateUserConfigStrategy createUserConfigStrategy,
            GetUserDetailsConfigStrategy getUserDetailsConfigStrategy,
            GetUsersConfigStrategy getUsersConfigStrategy) {
        this.createUserConfigStrategy = createUserConfigStrategy;
        this.getUserDetailsConfigStrategy = getUserDetailsConfigStrategy;
        this.getUsersConfigStrategy = getUsersConfigStrategy;

        this.strategies = new HashMap<>();
        this.strategies.put("createUser", createUserConfigStrategy);
        this.strategies.put("getUserDetails", getUserDetailsConfigStrategy);
        this.strategies.put("getUsers", getUsersConfigStrategy);
    }

    RolesConfigStrategy getStrategy(String annotatedMethodName) {
        return strategies.get(annotatedMethodName);
    }
}
