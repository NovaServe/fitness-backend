/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.users.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import com.novaserve.fitness.auth.service.AuthUtil;
import com.novaserve.fitness.exception.ExceptionMessage;
import com.novaserve.fitness.exception.ServerException;
import com.novaserve.fitness.helpers.DtoHelper;
import com.novaserve.fitness.helpers.MockHelper;
import com.novaserve.fitness.users.dto.CreateUserRequestDto;
import com.novaserve.fitness.users.model.AgeGroup;
import com.novaserve.fitness.users.model.Gender;
import com.novaserve.fitness.users.model.Role;
import com.novaserve.fitness.users.model.User;
import com.novaserve.fitness.users.repository.AgeGroupRepository;
import com.novaserve.fitness.users.repository.GenderRepository;
import com.novaserve.fitness.users.repository.RoleRepository;
import com.novaserve.fitness.users.repository.UserRepository;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class CreateUserTest {
  @InjectMocks UserServiceImpl userService;

  @Mock AuthUtil authUtil;

  @Mock RoleRepository roleRepository;

  @Mock GenderRepository genderRepository;

  @Mock AgeGroupRepository ageGroupRepository;

  @Mock UserRepository userRepository;

  @Spy PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  @Spy MockHelper $mock;

  @Spy DtoHelper $dto;

  Role superadminRole;
  Role adminRole;
  Role customerRole;
  Role instructorRole;
  Gender gender;
  AgeGroup ageGroup;

  @BeforeEach
  public void beforeEach() {
    superadminRole = $mock.superadminRole();
    adminRole = $mock.adminRole();
    customerRole = $mock.customerRole();
    instructorRole = $mock.instructorRole();
    gender = $mock.female();
    ageGroup = $mock.adult();

    lenient().when(genderRepository.findByName(gender.getName())).thenReturn(Optional.of(gender));
    lenient()
        .when(ageGroupRepository.findByName(ageGroup.getName()))
        .thenReturn(Optional.of(ageGroup));
    when(roleRepository.findByName(adminRole.getName())).thenReturn(Optional.of(adminRole));
    when(roleRepository.findByName(customerRole.getName())).thenReturn(Optional.of(customerRole));
    lenient()
        .when(roleRepository.findByName(instructorRole.getName()))
        .thenReturn(Optional.of(instructorRole));
    lenient()
        .when(userRepository.save(any(User.class)))
        .then(
            invocation -> {
              User user = invocation.getArgument(0);
              user.setId(1000L);
              return user;
            });
  }

  void assertHelper(User actual, CreateUserRequestDto dto) {
    String[] comparatorIgnoreFields = new String[] {"id", "password", "role", "ageGroup", "gender"};
    assertThat(actual)
        .usingRecursiveComparison()
        .ignoringFields(comparatorIgnoreFields)
        .isEqualTo(dto);
    assertEquals(actual.getRole().getName(), dto.getRole());
    assertEquals(actual.getAgeGroup().getName(), dto.getAgeGroup());
    assertEquals(actual.getGender().getName(), dto.getGender());
    assertNotNull(actual.getId());
  }

  @Test
  void createUser_shouldCreateAdmin_whenSuperadminRequests() {
    User superadmin =
        $mock.user().seed(1).role(superadminRole).gender(gender).ageGroup(ageGroup).get();

    CreateUserRequestDto dto =
        $dto.createUserRequestDto()
            .seed(2)
            .role(adminRole.getName())
            .gender(gender.getName())
            .ageGroup(ageGroup.getName())
            .get();

    when(authUtil.getUserFromAuth(any())).thenReturn(superadmin);

    User actual = userService.createUser(dto);
    assertHelper(actual, dto);
  }

  @ParameterizedTest
  @MethodSource("createUserParams")
  void createUser_shouldCreateCustomerOrInstructor_whenAdminRequests(String roleName) {
    User admin = $mock.user().seed(1).role(adminRole).gender(gender).ageGroup(ageGroup).get();

    CreateUserRequestDto dto =
        $dto.createUserRequestDto()
            .seed(2)
            .role(roleName)
            .gender(gender.getName())
            .ageGroup(ageGroup.getName())
            .get();

    when(authUtil.getUserFromAuth(any())).thenReturn(admin);

    User actual = userService.createUser(dto);
    assertHelper(actual, dto);
  }

  static Stream<Arguments> createUserParams() {
    return Stream.of(Arguments.of("ROLE_CUSTOMER"), Arguments.of("ROLE_INSTRUCTOR"));
  }

  @ParameterizedTest
  @MethodSource("createUserParams_rolesMismatch")
  void createUser_shouldThrowException_whenRolesMismatch(
      String creatorRoleName, String createdRoleName) {
    User user =
        $mock
            .user()
            .seed(1)
            .role(getRoleHelper(creatorRoleName))
            .gender(gender)
            .ageGroup(ageGroup)
            .get();

    CreateUserRequestDto dto =
        $dto.createUserRequestDto()
            .seed(2)
            .role(createdRoleName)
            .gender(gender.getName())
            .ageGroup(ageGroup.getName())
            .get();

    when(authUtil.getUserFromAuth(any())).thenReturn(user);

    ServerException actual = assertThrows(ServerException.class, () -> userService.createUser(dto));
    assertEquals(actual.getMessage(), ExceptionMessage.ROLES_MISMATCH.getName());
    assertEquals(actual.getStatus(), HttpStatus.BAD_REQUEST);
  }

  static Stream<Arguments> createUserParams_rolesMismatch() {
    return Stream.of(
        Arguments.of("ROLE_SUPERADMIN", "ROLE_SUPERADMIN"),
        Arguments.of("ROLE_SUPERADMIN", "ROLE_CUSTOMER"),
        Arguments.of("ROLE_SUPERADMIN", "ROLE_INSTRUCTOR"),
        Arguments.of("ROLE_ADMIN", "ROLE_ADMIN"),
        Arguments.of("ROLE_ADMIN", "ROLE_SUPERADMIN"),
        Arguments.of("ROLE_CUSTOMER", "ROLE_CUSTOMER"),
        Arguments.of("ROLE_CUSTOMER", "ROLE_SUPERADMIN"),
        Arguments.of("ROLE_CUSTOMER", "ROLE_ADMIN"),
        Arguments.of("ROLE_CUSTOMER", "ROLE_INSTRUCTOR"),
        Arguments.of("ROLE_INSTRUCTOR", "ROLE_INSTRUCTOR"),
        Arguments.of("ROLE_INSTRUCTOR", "ROLE_CUSTOMER"),
        Arguments.of("ROLE_INSTRUCTOR", "ROLE_SUPERADMIN"),
        Arguments.of("ROLE_INSTRUCTOR", "ROLE_ADMIN"));
  }

  Role getRoleHelper(String roleName) {
    Role role = null;
    switch (roleName) {
      case "ROLE_SUPERADMIN" -> role = superadminRole;
      case "ROLE_ADMIN" -> role = adminRole;
      case "ROLE_CUSTOMER" -> role = customerRole;
      case "ROLE_INSTRUCTOR" -> role = instructorRole;
    }
    return role;
  }
}
