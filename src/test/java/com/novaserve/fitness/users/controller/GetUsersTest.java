/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.users.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.novaserve.fitness.config.Docker;
import com.novaserve.fitness.config.TestBeans;
import com.novaserve.fitness.exception.ExceptionMessage;
import com.novaserve.fitness.helpers.DbHelper;
import com.novaserve.fitness.helpers.DtoHelper;
import com.novaserve.fitness.users.dto.UserResponseDto;
import com.novaserve.fitness.users.model.AgeGroup;
import com.novaserve.fitness.users.model.Gender;
import com.novaserve.fitness.users.model.Role;
import com.novaserve.fitness.users.model.User;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Import(TestBeans.class)
public class GetUsersTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    DbHelper helper;

    @Autowired
    DtoHelper dtoHelper;

    final String CREATE_USER_URL = "/api/v1/users";

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
    List<User> users;

    @BeforeEach
    void beforeEach() {
        helper.deleteAll();
        superadminRole = Role.ROLE_SUPERADMIN;
        adminRole = Role.ROLE_ADMIN;
        customerRole = Role.ROLE_CUSTOMER;
        instructorRole = Role.ROLE_INSTRUCTOR;
        gender = Gender.Female;
        ageGroup = AgeGroup.Adult;
        final Map<Integer, Role> SEED_ROLE_MAP = Map.of(
                1, superadminRole,
                2, superadminRole,
                3, adminRole,
                4, adminRole,
                5, customerRole,
                6, customerRole,
                7, instructorRole,
                8, instructorRole);
        users = SEED_ROLE_MAP.entrySet().stream()
                .map(entry -> helper.user()
                        .seed(entry.getKey())
                        .role(entry.getValue())
                        .gender(gender)
                        .ageGroup(ageGroup)
                        .get())
                .toList();
    }

    @ParameterizedTest
    @MethodSource("methodParams_getUsers_shouldReturnPageFilteredByRoles_whenSuperadminRequest")
    @WithMockUser(username = "username0", password = "Password0!", roles = "SUPERADMIN")
    void getUsers_shouldReturnPageFilteredByRoles_whenSuperadminRequest(Role principalRole, List<Role> filterByRoles)
            throws Exception {
        User principal = helper.user()
                .seed(0)
                .role(principalRole)
                .gender(gender)
                .ageGroup(ageGroup)
                .get();

        MvcResult mvc = mockMvc.perform(get(CREATE_USER_URL + getRequestParams(filterByRoles, null))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String res = mvc.getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(res);
        JsonNode content = root.path("content");
        List<UserResponseDto> dto =
                objectMapper.readValue(content.toString(), new TypeReference<List<UserResponseDto>>() {});
        assertHelper(getExpected(filterByRoles, null), dto);
    }

    static Stream<Arguments> methodParams_getUsers_shouldReturnPageFilteredByRoles_whenSuperadminRequest() {
        return Stream.of(Arguments.of(Role.ROLE_SUPERADMIN, List.of(Role.ROLE_ADMIN)));
    }

    @ParameterizedTest
    @MethodSource("methodParams_getUsers_shouldReturnPageFilteredByRoles_whenAdminRequest")
    @WithMockUser(username = "username0", password = "Password0!", roles = "ADMIN")
    void getUsers_shouldReturnPageFilteredByRoles_whenAdminRequest(Role principalRole, List<Role> filterByRoles)
            throws Exception {
        User principal = helper.user()
                .seed(0)
                .role(principalRole)
                .gender(gender)
                .ageGroup(ageGroup)
                .get();

        MvcResult mvc = mockMvc.perform(get(CREATE_USER_URL + getRequestParams(filterByRoles, null))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String res = mvc.getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(res);
        JsonNode content = root.path("content");
        List<UserResponseDto> dto =
                objectMapper.readValue(content.toString(), new TypeReference<List<UserResponseDto>>() {});
        assertHelper(getExpected(filterByRoles, null), dto);
    }

    static Stream<Arguments> methodParams_getUsers_shouldReturnPageFilteredByRoles_whenAdminRequest() {
        return Stream.of(
                Arguments.of(Role.ROLE_ADMIN, List.of(Role.ROLE_CUSTOMER)),
                Arguments.of(Role.ROLE_ADMIN, List.of(Role.ROLE_INSTRUCTOR)),
                Arguments.of(Role.ROLE_ADMIN, List.of(Role.ROLE_CUSTOMER, Role.ROLE_INSTRUCTOR)));
    }

    @ParameterizedTest
    @MethodSource("methodParams_getUsers_shouldReturnPageFilteredByFullName_whenSuperadminRequest")
    @WithMockUser(username = "username0", password = "Password0!", roles = "SUPERADMIN")
    void getUsers_shouldReturnPageFilteredByFullName_whenSuperadminRequest(
            Role principalRole, List<Role> filterByRoles, String fullName) throws Exception {
        User principal = helper.user()
                .seed(0)
                .role(principalRole)
                .gender(gender)
                .ageGroup(ageGroup)
                .get();

        MvcResult mvc = mockMvc.perform(get(CREATE_USER_URL + getRequestParams(filterByRoles, fullName))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String res = mvc.getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(res);
        JsonNode content = root.path("content");
        List<UserResponseDto> dto =
                objectMapper.readValue(content.toString(), new TypeReference<List<UserResponseDto>>() {});
        assertHelper(getExpected(filterByRoles, fullName), dto);
    }

    static Stream<Arguments> methodParams_getUsers_shouldReturnPageFilteredByFullName_whenSuperadminRequest() {
        return Stream.of(
                Arguments.of(Role.ROLE_SUPERADMIN, List.of(Role.ROLE_ADMIN), "Three"),
                Arguments.of(Role.ROLE_SUPERADMIN, List.of(Role.ROLE_ADMIN), "Zero"));
    }

    @ParameterizedTest
    @MethodSource("methodParams_getUsers_shouldReturnPageFilteredByFullName_whenAdminRequest")
    @WithMockUser(username = "username0", password = "Password0!", roles = "SUPERADMIN")
    void getUsers_shouldReturnPageFilteredByFullName_whenAdminRequest(
            Role principalRole, List<Role> filterByRoles, String fullName) throws Exception {
        User principal = helper.user()
                .seed(0)
                .role(principalRole)
                .gender(gender)
                .ageGroup(ageGroup)
                .get();

        MvcResult mvc = mockMvc.perform(get(CREATE_USER_URL + getRequestParams(filterByRoles, fullName))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String res = mvc.getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(res);
        JsonNode content = root.path("content");
        List<UserResponseDto> dto =
                objectMapper.readValue(content.toString(), new TypeReference<List<UserResponseDto>>() {});
        assertHelper(getExpected(filterByRoles, fullName), dto);
    }

    static Stream<Arguments> methodParams_getUsers_shouldReturnPageFilteredByFullName_whenAdminRequest() {
        return Stream.of(
                Arguments.of(Role.ROLE_ADMIN, List.of(Role.ROLE_CUSTOMER), "Five"),
                Arguments.of(Role.ROLE_ADMIN, List.of(Role.ROLE_INSTRUCTOR), "Seven"),
                Arguments.of(Role.ROLE_ADMIN, List.of(Role.ROLE_CUSTOMER, Role.ROLE_INSTRUCTOR), "Seven"),
                Arguments.of(Role.ROLE_ADMIN, List.of(Role.ROLE_CUSTOMER, Role.ROLE_INSTRUCTOR), "Zero"));
    }

    @ParameterizedTest
    @MethodSource("methodParams_getUsers_shouldThrowException_whenSuperadminRequest_rolesMismatch")
    @WithMockUser(username = "username0", password = "Password0!", roles = "SUPERADMIN")
    void getUsers_shouldThrowException_whenSuperadminRequest_rolesMismatch(Role principalRole, List<Role> filterByRoles)
            throws Exception {
        User principal = helper.user()
                .seed(0)
                .role(principalRole)
                .gender(gender)
                .ageGroup(ageGroup)
                .get();

        mockMvc.perform(get(CREATE_USER_URL + getRequestParams(filterByRoles, null))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is(ExceptionMessage.ROLES_MISMATCH.getName())))
                .andDo(print());
    }

    static Stream<Arguments> methodParams_getUsers_shouldThrowException_whenSuperadminRequest_rolesMismatch() {
        return Stream.of(
                Arguments.of(Role.ROLE_SUPERADMIN, List.of(Role.ROLE_SUPERADMIN)),
                Arguments.of(Role.ROLE_SUPERADMIN, List.of(Role.ROLE_CUSTOMER)),
                Arguments.of(Role.ROLE_SUPERADMIN, List.of(Role.ROLE_INSTRUCTOR)));
    }

    @ParameterizedTest
    @MethodSource("methodParams_getUsers_shouldThrowException_whenAdminRequest_rolesMismatch")
    @WithMockUser(username = "username0", password = "Password0!", roles = "ADMIN")
    void getUsers_shouldThrowException_whenAdminRequest_rolesMismatch(Role principalRole, List<Role> roles)
            throws Exception {
        User principal = helper.user()
                .seed(0)
                .role(principalRole)
                .gender(gender)
                .ageGroup(ageGroup)
                .get();

        mockMvc.perform(get(CREATE_USER_URL + getRequestParams(roles, null)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is(ExceptionMessage.ROLES_MISMATCH.getName())))
                .andDo(print());
    }

    static Stream<Arguments> methodParams_getUsers_shouldThrowException_whenAdminRequest_rolesMismatch() {
        return Stream.of(
                Arguments.of(Role.ROLE_ADMIN, List.of(Role.ROLE_ADMIN)),
                Arguments.of(Role.ROLE_ADMIN, List.of(Role.ROLE_SUPERADMIN)));
    }

    void assertHelper(List<User> expected, List<UserResponseDto> actual) {
        assertEquals(expected.size(), actual.size());
        BiPredicate<String, Role> roleBiPredicate = (string, enumeration) -> string.equals(enumeration.name());
        IntStream.range(0, expected.size()).forEach(i -> assertThat(actual.get(i))
                .usingRecursiveComparison()
                .withEqualsForFields(roleBiPredicate, "role")
                .isEqualTo(expected.get(i)));
    }

    List<User> getExpected(List<Role> roles, String fullName) {
        return users.stream()
                .filter(user -> roles.contains(user.getRole()))
                .filter(user -> fullName == null || user.getFullName().contains(fullName))
                .sorted(Comparator.comparingLong(User::getId))
                .toList();
    }

    String getRequestParams(List<Role> roles, String fullName) {
        List<String> rolesString = roles.stream().map(Enum::name).toList();
        StringBuilder sb = new StringBuilder("?roles=");
        sb.append(String.join(",", rolesString));
        if (fullName != null) {
            sb.append("&fullName=");
            sb.append(fullName);
        }
        return sb.toString();
    }
}
