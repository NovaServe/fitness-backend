/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.profiles.service;

import com.novaserve.fitness.auth.service.AuthUtil;
import com.novaserve.fitness.exceptions.*;
import com.novaserve.fitness.profiles.aspect.CheckUserActive;
import com.novaserve.fitness.profiles.aspect.RolesConfig;
import com.novaserve.fitness.profiles.dto.request.CustomerCreationDto;
import com.novaserve.fitness.profiles.dto.request.UserCreationBaseDto;
import com.novaserve.fitness.profiles.dto.response.AdminDetailsDto;
import com.novaserve.fitness.profiles.dto.response.CustomerDetailsDto;
import com.novaserve.fitness.profiles.dto.response.InstructorDetailsDto;
import com.novaserve.fitness.profiles.dto.response.UserDetailsBaseDto;
import com.novaserve.fitness.profiles.model.*;
import com.novaserve.fitness.profiles.repository.UserRepository;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    private final AuthUtil authUtil;

    private final ProfileUtil profileUtil;

    private final UserCreationFactory userCreationFactory;

    public UserServiceImpl(
            UserRepository userRepository,
            ModelMapper modelMapper,
            AuthUtil authUtil,
            ProfileUtil profileUtil,
            UserCreationFactory userCreationFactory) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.authUtil = authUtil;
        this.profileUtil = profileUtil;
        this.userCreationFactory = userCreationFactory;
    }

    @Override
    @Transactional
    @CheckUserActive
    @RolesConfig(
            principalToUsersRolesMapping = {
                @RolesConfig.PrincipalToUsersRolesMapping(
                        principalRole = Role.ROLE_SUPERADMIN,
                        usersRoles = {Role.ROLE_ADMIN, Role.ROLE_INSTRUCTOR, Role.ROLE_CUSTOMER}),
                @RolesConfig.PrincipalToUsersRolesMapping(
                        principalRole = Role.ROLE_ADMIN,
                        usersRoles = {Role.ROLE_INSTRUCTOR, Role.ROLE_CUSTOMER})
            })
    public UserDetailsBaseDto createUser(UserCreationBaseDto userCreationDto) throws NoSuchAlgorithmException {
        UserBase principal = authUtil.getUserFromAuth(
                        SecurityContextHolder.getContext().getAuthentication())
                .orElseThrow(() -> new ServerException(ExceptionMessage.UNAUTHORIZED, HttpStatus.UNAUTHORIZED));

        UserCreationStrategy userCreationStrategy =
                userCreationFactory.createStrategyInstance(userCreationDto.getRole());
        UserBase savedUser = userCreationStrategy.createUser(userCreationDto);
        logger.info(
                "User with id {} was created by {} with id {}",
                savedUser.getId(),
                principal.getRoleName(),
                principal.getId());
        UserDetailsBaseDto userDto = map(savedUser);
        return userDto;
    }

    @Override
    @Transactional
    public CustomerDetailsDto signupCustomer(CustomerCreationDto userCreationDto) throws NoSuchAlgorithmException {
        UserCreationStrategy userCreationStrategy = userCreationFactory.createStrategyInstance(Role.ROLE_CUSTOMER);
        UserBase savedUser = userCreationStrategy.createUser(userCreationDto);
        logger.info("User with id {} was created by customer", savedUser.getId());
        CustomerDetailsDto userDto = map(savedUser);
        return userDto;
    }

    @Override
    @Transactional
    @CheckUserActive
    @RolesConfig(
            principalToUsersRolesMapping = {
                @RolesConfig.PrincipalToUsersRolesMapping(
                        principalRole = Role.ROLE_SUPERADMIN,
                        usersRoles = {Role.ROLE_SUPERADMIN, Role.ROLE_ADMIN, Role.ROLE_INSTRUCTOR, Role.ROLE_CUSTOMER}),
                @RolesConfig.PrincipalToUsersRolesMapping(
                        principalRole = Role.ROLE_ADMIN,
                        usersRoles = {Role.ROLE_ADMIN, Role.ROLE_INSTRUCTOR, Role.ROLE_CUSTOMER}),
                @RolesConfig.PrincipalToUsersRolesMapping(
                        principalRole = Role.ROLE_INSTRUCTOR,
                        usersRoles = {Role.ROLE_ADMIN, Role.ROLE_INSTRUCTOR, Role.ROLE_CUSTOMER}),
                @RolesConfig.PrincipalToUsersRolesMapping(
                        principalRole = Role.ROLE_CUSTOMER,
                        usersRoles = {Role.ROLE_ADMIN, Role.ROLE_INSTRUCTOR, Role.ROLE_CUSTOMER})
            })
    public UserDetailsBaseDto getUserDetails(long userId) {
        UserBase user = profileUtil.getUserByIdOrThrowNotFound(userId);
        UserDetailsBaseDto userDetailsDto = map(user);
        return userDetailsDto;
    }

    @Override
    @Transactional
    @CheckUserActive
    @RolesConfig(
            principalToUsersRolesMapping = {
                @RolesConfig.PrincipalToUsersRolesMapping(
                        principalRole = Role.ROLE_SUPERADMIN,
                        usersRoles = {Role.ROLE_SUPERADMIN, Role.ROLE_ADMIN, Role.ROLE_INSTRUCTOR, Role.ROLE_CUSTOMER}),
                @RolesConfig.PrincipalToUsersRolesMapping(
                        principalRole = Role.ROLE_ADMIN,
                        usersRoles = {Role.ROLE_ADMIN, Role.ROLE_INSTRUCTOR, Role.ROLE_CUSTOMER}),
                @RolesConfig.PrincipalToUsersRolesMapping(
                        principalRole = Role.ROLE_INSTRUCTOR,
                        usersRoles = {Role.ROLE_ADMIN, Role.ROLE_INSTRUCTOR, Role.ROLE_CUSTOMER}),
                @RolesConfig.PrincipalToUsersRolesMapping(
                        principalRole = Role.ROLE_CUSTOMER,
                        usersRoles = {Role.ROLE_ADMIN, Role.ROLE_INSTRUCTOR})
            })
    public Page<UserDetailsBaseDto> getUsers(
            Set<Role> roles, String fullName, String sortBy, String orderBy, int pageSize, int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.fromString(orderBy), sortBy));
        Page<UserDetailsBaseDto> userResponseDtoPage =
                userRepository.getUsers(roles, fullName, pageable).map(user -> map(user));
        return userResponseDtoPage;
    }

    private <S, D extends UserDetailsBaseDto> D map(S source) {
        if (source instanceof Admin instance) {
            return (D) modelMapper.map(instance, AdminDetailsDto.class);
        }
        if (source instanceof Instructor instance) {
            return (D) modelMapper.map(instance, InstructorDetailsDto.class);
        }
        if (source instanceof Customer instance) {
            return (D) modelMapper.map(instance, CustomerDetailsDto.class);
        }
        return null;
    }
}
