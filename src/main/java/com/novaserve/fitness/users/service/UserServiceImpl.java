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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
        User requestedBy = authUtil.getUserFromAuth(
                        SecurityContextHolder.getContext().getAuthentication())
                .orElseThrow(() -> new ServerException(ExceptionMessage.UNAUTHORIZED, HttpStatus.UNAUTHORIZED));
        Map<String, Role> roles = getRoles();
        boolean superadminCreatesAdmin = requestedBy.isSuperadmin() && isRoleAdmin(dto.getRole());
        boolean adminCreatesCustomerOrInstructor = requestedBy.isAdmin() && isRoleCustomerOrInstructor(dto.getRole());

        if (superadminCreatesAdmin || adminCreatesCustomerOrInstructor) {
            User saved = processCreateUser(dto, roles);
            logger.info(
                    "User with id {} was created by {} with id {}",
                    saved.getId(),
                    requestedBy.getRoleName(),
                    requestedBy.getId());
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
        userRepository
                .findByUsernameOrEmailOrPhone(dto.getUsername(), dto.getEmail(), dto.getPhone())
                .ifPresent(user -> {
                    throw new ServerException(ExceptionMessage.ALREADY_EXISTS, HttpStatus.BAD_REQUEST);
                });
        Gender gender = genderRepository
                .findByName(dto.getGender())
                .orElseThrow(() -> new NotFoundInternalError(Gender.class, dto.getGender()));
        AgeGroup ageGroup = ageGroupRepository
                .findByName(dto.getAgeGroup())
                .orElseThrow(() -> new NotFoundInternalError(AgeGroup.class, dto.getAgeGroup()));
        Role role = roles.get(dto.getRole());
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
    public Page<UserResponseDto> getUsers(
            List<String> roles, String fullName, String sortBy, String order, int pageSize, int pageNumber) {
        User principal = authUtil.getUserFromAuth(
                        SecurityContextHolder.getContext().getAuthentication())
                .orElseThrow(() -> new ServerException(ExceptionMessage.UNAUTHORIZED, HttpStatus.UNAUTHORIZED));
        boolean superadminGetsAdmins = principal.isSuperadmin() && roles.size() == 1 && isRoleAdmin(roles.get(0));
        boolean adminGetsCustomersOrInstructors =
                principal.isAdmin() && roles.stream().allMatch(this::isRoleCustomerOrInstructor);

        if (superadminGetsAdmins || adminGetsCustomersOrInstructors) {
            Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.fromString(order), sortBy));
            return userRepository
                    .getUsers(roles, fullName, pageable)
                    .map(user -> modelMapper.map(user, UserResponseDto.class));
        }
        throw new ServerException(ExceptionMessage.ROLES_MISMATCH, HttpStatus.BAD_REQUEST);
    }
}
