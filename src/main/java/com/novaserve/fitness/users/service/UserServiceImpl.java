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
  private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

  @Autowired UserRepository userRepository;

  @Autowired RoleRepository roleRepository;

  @Autowired GenderRepository genderRepository;

  @Autowired AgeGroupRepository ageGroupRepository;

  @Autowired PasswordEncoder passwordEncoder;

  @Autowired ModelMapper modelMapper;

  @Autowired AuthUtil authUtil;

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
    User user = authUtil.getUserFromAuth(SecurityContextHolder.getContext().getAuthentication());
    Role roleAdmin =
        roleRepository
            .findByName("ROLE_ADMIN")
            .orElseThrow(() -> new NotFoundInternalError(Role.class, "ROLE_ADMIN"));
    Role roleCustomer =
        roleRepository
            .findByName("ROLE_CUSTOMER")
            .orElseThrow(() -> new NotFoundInternalError(Role.class, "ROLE_CUSTOMER"));
    Role roleInstructor =
        roleRepository
            .findByName("ROLE_INSTRUCTOR")
            .orElseThrow(() -> new NotFoundInternalError(Role.class, "ROLE_INSTRUCTOR"));

    boolean superadminCreatesAdminUser =
        user.getRole().getName().equals("ROLE_SUPERADMIN")
            && dto.getRole().equals(roleAdmin.getName());

    boolean adminCreatesCustomerOrInstructorUser =
        user.getRole().getName().equals("ROLE_ADMIN")
            && (dto.getRole().equals(roleCustomer.getName())
                || dto.getRole().equals(roleInstructor.getName()));

    if (superadminCreatesAdminUser || adminCreatesCustomerOrInstructorUser) {
      Gender gender =
          genderRepository
              .findByName(dto.getGender())
              .orElseThrow(() -> new NotFoundInternalError(Gender.class, dto.getGender()));
      AgeGroup ageGroup =
          ageGroupRepository
              .findByName(dto.getAgeGroup())
              .orElseThrow(() -> new NotFoundInternalError(AgeGroup.class, dto.getAgeGroup()));
      Role role =
          roleRepository
              .findByName(dto.getRole())
              .orElseThrow(() -> new NotFoundInternalError(Role.class, dto.getRole()));
      User newUser =
          User.builder()
              .username(dto.getUsername())
              .email(dto.getEmail())
              .phone(dto.getPhone())
              .fullName(dto.getFullName())
              .password(passwordEncoder.encode(dto.getPassword()))
              .role(role)
              .gender(gender)
              .ageGroup(ageGroup)
              .build();
      User saved = userRepository.save(newUser);
      logger.info(
          "User with id {} was created by {} with id {}",
          newUser.getId(),
          user.getRole().getName(),
          user.getId());
      return saved;
    }
    throw new ServerException(ExceptionMessage.ROLES_MISMATCH, HttpStatus.BAD_REQUEST);
  }
}
