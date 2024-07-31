/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.users.service;

import com.novaserve.fitness.auth.service.AuthUtil;
import com.novaserve.fitness.exception.*;
import com.novaserve.fitness.users.dto.CreateUserRequestDto;
import com.novaserve.fitness.users.dto.UserResponseDto;
import com.novaserve.fitness.users.model.AgeGroup;
import com.novaserve.fitness.users.model.Gender;
import com.novaserve.fitness.users.model.Role;
import com.novaserve.fitness.users.model.User;
import com.novaserve.fitness.users.repository.AgeGroupRepository;
import com.novaserve.fitness.users.repository.GenderRepository;
import com.novaserve.fitness.users.repository.RoleRepository;
import com.novaserve.fitness.users.repository.UserRepository;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    GenderRepository genderRepository;

    @Autowired
    AgeGroupRepository ageGroupRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    AuthUtil authUtil;

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public User getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFound(User.class, userId));
    }

    @Override
    public UserResponseDto getUserDetailById(long userId) {
        return userRepository
                .findById(userId)
                .map(user -> modelMapper.map(user, UserResponseDto.class))
                .orElseThrow(() -> new NotFound(User.class, userId));
    }

    @Override
    @Transactional
    public User createUser(CreateUserRequestDto dto) {
        var createdBy = authUtil.getPrincipal(SecurityContextHolder.getContext().getAuthentication())
                .orElseThrow(() -> new ServerException(ExceptionMessage.UNAUTHORIZED, HttpStatus.UNAUTHORIZED));
        var roles = getRoles();
        var superadminCreatesAdmin = createdBy.isSuperadmin() && isRoleAdmin(dto.getRole());
        var adminCreatesCustomerOrInstructor = createdBy.isAdmin() && isRoleCustomerOrInstructor(dto.getRole());

        if (superadminCreatesAdmin || adminCreatesCustomerOrInstructor) {
            var saved = processCreateUser(dto, roles);
            logger.info(
                    "User with id {} was created by {} with id {}",
                    saved.getId(),
                    createdBy.getRoleName(),
                    createdBy.getId());
            return saved;
        }
        throw new ServerException(ExceptionMessage.ROLES_MISMATCH, HttpStatus.BAD_REQUEST);
    }

    private Map<String, Role> getRoles() {
        return Collections.unmodifiableMap(Stream.of("ROLE_ADMIN", "ROLE_CUSTOMER", "ROLE_INSTRUCTOR")
                .collect(Collectors.toMap(roleName -> roleName, roleName -> roleRepository
                        .findByName(roleName)
                        .orElseThrow(() -> new NotFoundInternalError(Role.class, roleName)))));
    }

    private boolean isRoleAdmin(String roleName) {
        return "ROLE_ADMIN".equals(roleName);
    }

    private boolean isRoleCustomerOrInstructor(String roleName) {
        return "ROLE_CUSTOMER".equals(roleName) || "ROLE_INSTRUCTOR".equals(roleName);
    }

    private User processCreateUser(CreateUserRequestDto dto, Map<String, Role> roles) {
        var gender = genderRepository
                .findByName(dto.getGender())
                .orElseThrow(() -> new NotFoundInternalError(Gender.class, dto.getGender()));
        var ageGroup = ageGroupRepository
                .findByName(dto.getAgeGroup())
                .orElseThrow(() -> new NotFoundInternalError(AgeGroup.class, dto.getAgeGroup()));
        var role = roles.get(dto.getRole());
        return userRepository.save(User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .fullName(dto.getFullName())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(role)
                .gender(gender)
                .ageGroup(ageGroup)
                .build());
    }

    @Override
    @Transactional
    public UserResponseDto getUserDetail(long userId) {
        User principal = authUtil.getPrincipal(
                        SecurityContextHolder.getContext().getAuthentication())
                .orElseThrow(() -> new ServerException(ExceptionMessage.UNAUTHORIZED, HttpStatus.UNAUTHORIZED));
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFound(User.class, userId));

        boolean superadminRequestsOwnOrAdminDetail =
                principal.isSuperadmin() && (user.getId().equals(principal.getId()) || user.isAdmin());
        boolean adminRequestsOwnOrCustomerOrInstructorDetail =
                user.isAdmin() && (user.getId().equals(principal.getId()) || user.isCustomer() || user.isInstructor());
        boolean customerRequestsOwnDetail = user.isCustomer() && user.getId().equals(principal.getId());
        boolean instructorRequestsOwnDetail =
                user.isInstructor() && user.getId().equals(principal.getId());

        if (superadminRequestsOwnOrAdminDetail
                || adminRequestsOwnOrCustomerOrInstructorDetail
                || customerRequestsOwnDetail
                || instructorRequestsOwnDetail) {
            return modelMapper.map(user, UserResponseDto.class);
        }
        throw new ServerException(ExceptionMessage.ROLES_MISMATCH, HttpStatus.BAD_REQUEST);
    }
}
