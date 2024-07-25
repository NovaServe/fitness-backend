/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.users.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.novaserve.fitness.config.Docker;
import com.novaserve.fitness.config.TestBeans;
import com.novaserve.fitness.exception.ExceptionMessage;
import com.novaserve.fitness.helpers.DbHelper;
import com.novaserve.fitness.helpers.DtoHelper;
import com.novaserve.fitness.users.dto.CreateUserRequestDto;
import com.novaserve.fitness.users.model.AgeGroup;
import com.novaserve.fitness.users.model.Gender;
import com.novaserve.fitness.users.model.Role;
import com.novaserve.fitness.users.model.User;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Import(TestBeans.class)
class CreateUserTest {
  @Autowired private MockMvc mockMvc;

  @Autowired PasswordEncoder passwordEncoder;

  @Autowired ObjectMapper objectMapper;

  @Autowired DbHelper $db;

  @Autowired DtoHelper $dto;

  static String CREATE_USER_URL = "/api/v1/users";

  @Container
  static PostgreSQLContainer<?> postgresqlContainer =
      new PostgreSQLContainer<>(DockerImageName.parse(Docker.POSTGRES));

  @DynamicPropertySource
  static void postgresProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgresqlContainer::getJdbcUrl);
    registry.add("spring.datasource.username", postgresqlContainer::getUsername);
    registry.add("spring.datasource.password", postgresqlContainer::getPassword);
  }

  Role superadminRole;
  Role adminRole;
  Role customerRole;
  Role instructorRole;
  Gender gender;
  AgeGroup ageGroup;

  @BeforeEach
  void beforeEach() {
    $db.deleteAll();
    superadminRole = $db.superadminRole();
    adminRole = $db.adminRole();
    customerRole = $db.customerRole();
    instructorRole = $db.instructorRole();
    gender = $db.female();
    ageGroup = $db.adult();
  }

  void assertHelper(CreateUserRequestDto dto) {
    User actual = $db.getUser(dto.getUsername());
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
  @WithMockUser(username = "username1", password = "Password1!", roles = "SUPERADMIN")
  void createUser_shouldCreateAdmin_whenSuperadminRequests() throws Exception {
    User superadmin =
        $db.user().seed(1).role(superadminRole).gender(gender).ageGroup(ageGroup).get();

    CreateUserRequestDto dto =
        $dto.createUserRequestDto()
            .seed(2)
            .role(adminRole.getName())
            .gender(gender.getName())
            .ageGroup(ageGroup.getName())
            .get();

    mockMvc
        .perform(
            post(CREATE_USER_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$").doesNotExist())
        .andDo(print());
    assertHelper(dto);
  }

  @ParameterizedTest
  @MethodSource("createUserParams")
  @WithMockUser(username = "username1", password = "Password1!", roles = "ADMIN")
  void createUser_shouldCreateCustomerOrInstructor_whenAdminRequests(String roleName)
      throws Exception {
    User admin = $db.user().seed(1).role(adminRole).gender(gender).ageGroup(ageGroup).get();

    CreateUserRequestDto dto =
        $dto.createUserRequestDto()
            .seed(2)
            .role(roleName)
            .gender(gender.getName())
            .ageGroup(ageGroup.getName())
            .get();

    mockMvc
        .perform(
            post(CREATE_USER_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$").doesNotExist())
        .andDo(print());
    assertHelper(dto);
  }

  static Stream<Arguments> createUserParams() {
    return Stream.of(Arguments.of("ROLE_CUSTOMER"), Arguments.of("ROLE_INSTRUCTOR"));
  }

  @ParameterizedTest
  @MethodSource("createUserParams_rolesMismatch")
  @WithMockUser(username = "username1", password = "Password1!", roles = "SUPERADMIN")
  void createUser_shouldThrowException_whenRolesMismatch(
      String creatorRoleName, String createdRoleName) throws Exception {
    User user =
        $db.user().seed(1).role(getRole(creatorRoleName)).gender(gender).ageGroup(ageGroup).get();

    CreateUserRequestDto dto =
        $dto.createUserRequestDto()
            .seed(2)
            .role(createdRoleName)
            .gender(gender.getName())
            .ageGroup(ageGroup.getName())
            .get();

    mockMvc
        .perform(
            post(CREATE_USER_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message", is(ExceptionMessage.ROLES_MISMATCH.getName())))
        .andDo(print());
    assertNull($db.getUser(dto.getUsername()));
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

  Role getRole(String roleName) {
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
