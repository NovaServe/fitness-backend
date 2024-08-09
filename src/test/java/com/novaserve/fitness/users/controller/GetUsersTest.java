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
        superadminRole = helper.superadminRole();
        adminRole = helper.adminRole();
        customerRole = helper.customerRole();
        instructorRole = helper.instructorRole();
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
        users = Collections.unmodifiableList(SEED_ROLE_MAP.entrySet().stream()
                .map(entry -> helper.user()
                        .seed(entry.getKey())
                        .role(entry.getValue())
                        .gender(gender)
                        .ageGroup(ageGroup)
                        .get())
                .toList());
    }

    @ParameterizedTest
    @MethodSource("methodParams_getUsers_shouldReturnPageFilteredByRoles_whenSuperadminRequest")
    @WithMockUser(username = "username0", password = "Password0!", roles = "SUPERADMIN")
    void getUsers_shouldReturnPageFilteredByRoles_whenSuperadminRequest(String principalRoleName, List<String> roles)
            throws Exception {
        User principal = helper.user()
                .seed(0)
                .role(getRole(principalRoleName))
                .gender(gender)
                .ageGroup(ageGroup)
                .get();

        MvcResult mvc = mockMvc.perform(
                        get(CREATE_USER_URL + getRequestParams(roles, null)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String res = mvc.getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(res);
        JsonNode content = root.path("content");
        List<UserResponseDto> dto =
                objectMapper.readValue(content.toString(), new TypeReference<List<UserResponseDto>>() {});
        assertHelper(getExpected(roles, null), dto);
    }

    static Stream<Arguments> methodParams_getUsers_shouldReturnPageFilteredByRoles_whenSuperadminRequest() {
        return Stream.of(Arguments.of("ROLE_SUPERADMIN", List.of("ROLE_ADMIN")));
    }

    @ParameterizedTest
    @MethodSource("methodParams_getUsers_shouldReturnPageFilteredByRoles_whenAdminRequest")
    @WithMockUser(username = "username0", password = "Password0!", roles = "ADMIN")
    void getUsers_shouldReturnPageFilteredByRoles_whenAdminRequest(String principalRoleName, List<String> roles)
            throws Exception {
        User principal = helper.user()
                .seed(0)
                .role(getRole(principalRoleName))
                .gender(gender)
                .ageGroup(ageGroup)
                .get();

        MvcResult mvc = mockMvc.perform(
                        get(CREATE_USER_URL + getRequestParams(roles, null)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String res = mvc.getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(res);
        JsonNode content = root.path("content");
        List<UserResponseDto> dto =
                objectMapper.readValue(content.toString(), new TypeReference<List<UserResponseDto>>() {});
        assertHelper(getExpected(roles, null), dto);
    }

    static Stream<Arguments> methodParams_getUsers_shouldReturnPageFilteredByRoles_whenAdminRequest() {
        return Stream.of(
                Arguments.of("ROLE_ADMIN", List.of("ROLE_CUSTOMER")),
                Arguments.of("ROLE_ADMIN", List.of("ROLE_INSTRUCTOR")),
                Arguments.of("ROLE_ADMIN", List.of("ROLE_CUSTOMER", "ROLE_INSTRUCTOR")));
    }

    @ParameterizedTest
    @MethodSource("methodParams_getUsers_shouldReturnPageFilteredByFullName_whenSuperadminRequest")
    @WithMockUser(username = "username0", password = "Password0!", roles = "SUPERADMIN")
    void getUsers_shouldReturnPageFilteredByFullName_whenSuperadminRequest(
            String principalRoleName, List<String> roles, String fullName) throws Exception {
        User principal = helper.user()
                .seed(0)
                .role(getRole(principalRoleName))
                .gender(gender)
                .ageGroup(ageGroup)
                .get();

        MvcResult mvc = mockMvc.perform(get(CREATE_USER_URL + getRequestParams(roles, fullName))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String res = mvc.getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(res);
        JsonNode content = root.path("content");
        List<UserResponseDto> dto =
                objectMapper.readValue(content.toString(), new TypeReference<List<UserResponseDto>>() {});
        assertHelper(getExpected(roles, fullName), dto);
    }

    static Stream<Arguments> methodParams_getUsers_shouldReturnPageFilteredByFullName_whenSuperadminRequest() {
        return Stream.of(
                Arguments.of("ROLE_SUPERADMIN", List.of("ROLE_ADMIN"), "Three"),
                Arguments.of("ROLE_SUPERADMIN", List.of("ROLE_ADMIN"), "Zero"));
    }

    @ParameterizedTest
    @MethodSource("methodParams_getUsers_shouldReturnPageFilteredByFullName_whenAdminRequest")
    @WithMockUser(username = "username0", password = "Password0!", roles = "SUPERADMIN")
    void getUsers_shouldReturnPageFilteredByFullName_whenAdminRequest(
            String principalRoleName, List<String> roles, String fullName) throws Exception {
        User principal = helper.user()
                .seed(0)
                .role(getRole(principalRoleName))
                .gender(gender)
                .ageGroup(ageGroup)
                .get();

        MvcResult mvc = mockMvc.perform(get(CREATE_USER_URL + getRequestParams(roles, fullName))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String res = mvc.getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(res);
        JsonNode content = root.path("content");
        List<UserResponseDto> dto =
                objectMapper.readValue(content.toString(), new TypeReference<List<UserResponseDto>>() {});
        assertHelper(getExpected(roles, fullName), dto);
    }

    static Stream<Arguments> methodParams_getUsers_shouldReturnPageFilteredByFullName_whenAdminRequest() {
        return Stream.of(
                Arguments.of("ROLE_ADMIN", List.of("ROLE_CUSTOMER"), "Five"),
                Arguments.of("ROLE_ADMIN", List.of("ROLE_INSTRUCTOR"), "Seven"),
                Arguments.of("ROLE_ADMIN", List.of("ROLE_CUSTOMER", "ROLE_INSTRUCTOR"), "Seven"),
                Arguments.of("ROLE_ADMIN", List.of("ROLE_CUSTOMER", "ROLE_INSTRUCTOR"), "Zero"));
    }

    @ParameterizedTest
    @MethodSource("methodParams_getUsers_shouldThrowException_whenSuperadminRequest_rolesMismatch")
    @WithMockUser(username = "username0", password = "Password0!", roles = "SUPERADMIN")
    void getUsers_shouldThrowException_whenSuperadminRequest_rolesMismatch(String principalRoleName, List<String> roles)
            throws Exception {
        User principal = helper.user()
                .seed(0)
                .role(getRole(principalRoleName))
                .gender(gender)
                .ageGroup(ageGroup)
                .get();

        mockMvc.perform(get(CREATE_USER_URL + getRequestParams(roles, null)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is(ExceptionMessage.ROLES_MISMATCH.getName())))
                .andDo(print());
    }

    static Stream<Arguments> methodParams_getUsers_shouldThrowException_whenSuperadminRequest_rolesMismatch() {
        return Stream.of(
                Arguments.of("ROLE_SUPERADMIN", List.of("ROLE_SUPERADMIN")),
                Arguments.of("ROLE_SUPERADMIN", List.of("ROLE_CUSTOMER")),
                Arguments.of("ROLE_SUPERADMIN", List.of("ROLE_INSTRUCTOR")));
    }

    @ParameterizedTest
    @MethodSource("methodParams_getUsers_shouldThrowException_whenAdminRequest_rolesMismatch")
    @WithMockUser(username = "username0", password = "Password0!", roles = "ADMIN")
    void getUsers_shouldThrowException_whenAdminRequest_rolesMismatch(String principalRoleName, List<String> roles)
            throws Exception {
        User principal = helper.user()
                .seed(0)
                .role(getRole(principalRoleName))
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
                Arguments.of("ROLE_ADMIN", List.of("ROLE_ADMIN")),
                Arguments.of("ROLE_ADMIN", List.of("ROLE_SUPERADMIN")));
    }

    void assertHelper(List<User> expected, List<UserResponseDto> actual) {
        BiPredicate<String, Gender> genderBiPredicate = (genderName, gender) -> genderName.equals(gender.name());
        BiPredicate<String, AgeGroup> ageGroupBiPredicate =
                (ageGroupName, ageGroup) -> ageGroupName.equals(ageGroup.name());
        BiPredicate<String, Role> roleBiPredicate = (roleName_, role) -> roleName_.equals(role.getName());
        assertEquals(expected.size(), actual.size());
        IntStream.range(0, expected.size()).forEach(i -> assertThat(actual.get(i))
                .usingRecursiveComparison()
                .withEqualsForFields(genderBiPredicate, "gender")
                .withEqualsForFields(ageGroupBiPredicate, "ageGroup")
                .withEqualsForFields(roleBiPredicate, "role")
                .isEqualTo(expected.get(i)));
    }

    List<User> getExpected(List<String> roles, String fullName) {
        return users.stream()
                .filter(user -> roles.contains(user.getRoleName()))
                .filter(user -> fullName == null || user.getFullName().contains(fullName))
                .sorted(Comparator.comparingLong(User::getId))
                .toList();
    }

    Role getRole(String roleName) {
        return switch (roleName) {
            case "ROLE_SUPERADMIN" -> superadminRole;
            case "ROLE_ADMIN" -> adminRole;
            case "ROLE_CUSTOMER" -> customerRole;
            case "ROLE_INSTRUCTOR" -> instructorRole;
            default -> throw new IllegalStateException("Unexpected value: " + roleName);
        };
    }

    String getRequestParams(List<String> roles, String fullName) {
        StringBuilder sb = new StringBuilder("?roles=");
        sb.append(String.join(",", roles));
        if (fullName != null) {
            sb.append("&fullName=");
            sb.append(fullName);
        }
        return sb.toString();
    }
}
