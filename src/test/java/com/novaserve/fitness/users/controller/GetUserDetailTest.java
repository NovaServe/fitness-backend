/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.users.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.novaserve.fitness.config.Docker;
import com.novaserve.fitness.config.TestBeans;
import com.novaserve.fitness.exception.ExceptionMessage;
import com.novaserve.fitness.helpers.DbHelper;
import com.novaserve.fitness.helpers.DtoHelper;
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
class GetUserDetailTest {
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

    final String GET_USER_DETAIL_URL = "/api/v1/users/{userId}";

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
        helper.deleteAll();
        superadminRole = Role.ROLE_SUPERADMIN;
        adminRole = Role.ROLE_ADMIN;
        customerRole = Role.ROLE_CUSTOMER;
        instructorRole = Role.ROLE_INSTRUCTOR;
        gender = Gender.Female;
        ageGroup = AgeGroup.Adult;
    }

    @ParameterizedTest
    @MethodSource("provideUserDetailParams")
    @WithMockUser(username = "username1", password = "Password1!", roles = "SUPERADMIN")
    void getUserDetail_shouldReturnDto_whenSuperadminRequestsOwnOrAdminDetail() throws Exception {
        User superadmin = helper.user()
                .seed(1)
                .role(superadminRole)
                .gender(gender)
                .ageGroup(ageGroup)
                .get();
        User admin = helper.user()
                .seed(2)
                .role(adminRole)
                .gender(gender)
                .ageGroup(ageGroup)
                .get();

        mockMvc.perform(get(GET_USER_DETAIL_URL, superadmin.getId()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(superadmin.getUsername()));

        mockMvc.perform(get(GET_USER_DETAIL_URL, admin.getId()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(admin.getUsername()));
    }

    static Stream<Arguments> provideUserDetailParams() {
        return Stream.of(Arguments.of(1L, Role.ROLE_SUPERADMIN), Arguments.of(2L, Role.ROLE_ADMIN));
    }

    @ParameterizedTest
    @MethodSource("provideUserDetailsParams")
    @WithMockUser(username = "username1", password = "Password1!", roles = "ADMIN")
    void getUserDetail_shouldReturnDto_whenAdminRequestsOwnOrCustomerOrInstructorDetail() throws Exception {
        User admin = helper.user()
                .seed(1)
                .role(adminRole)
                .gender(gender)
                .ageGroup(ageGroup)
                .get();
        User customer = helper.user()
                .seed(2)
                .role(customerRole)
                .gender(gender)
                .ageGroup(ageGroup)
                .get();
        User instructor = helper.user()
                .seed(3)
                .role(instructorRole)
                .gender(gender)
                .ageGroup(ageGroup)
                .get();

        mockMvc.perform(get(GET_USER_DETAIL_URL, admin.getId()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(admin.getUsername()));

        mockMvc.perform(get(GET_USER_DETAIL_URL, customer.getId()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(customer.getUsername()));

        mockMvc.perform(get(GET_USER_DETAIL_URL, instructor.getId()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(instructor.getUsername()));
    }

    static Stream<Arguments> provideUserDetailsParams() {
        return Stream.of(
                Arguments.of(1L, Role.ROLE_ADMIN),
                Arguments.of(2L, Role.ROLE_CUSTOMER),
                Arguments.of(3L, Role.ROLE_INSTRUCTOR));
    }

    @Test
    @WithMockUser(username = "username1", password = "Password1!", roles = "CUSTOMER")
    void getUserDetail_shouldReturnDto_whenCustomerRequestsOwnDetail() throws Exception {
        User customer = helper.user()
                .seed(1)
                .role(customerRole)
                .gender(gender)
                .ageGroup(ageGroup)
                .get();

        mockMvc.perform(get(GET_USER_DETAIL_URL, customer.getId()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(customer.getUsername()));
    }

    @Test
    @WithMockUser(username = "username1", password = "Password1!", roles = "INSTRUCTOR")
    void getUserDetail_shouldReturnDto_whenInstructorRequestsOwnDetail() throws Exception {
        User instructor = helper.user()
                .seed(1)
                .role(instructorRole)
                .gender(gender)
                .ageGroup(ageGroup)
                .get();

        mockMvc.perform(get(GET_USER_DETAIL_URL, instructor.getId()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(instructor.getUsername()));
    }

    @ParameterizedTest
    @MethodSource("getUserDetailParams_rolesMismatch")
    @WithMockUser(username = "username1", password = "Password1!", roles = "SUPERADMIN")
    void getUserDetail_shouldThrowException_whenRolesMismatch(Role principalRoleName, Role userRoleName)
            throws Exception {
        User principal = helper.user()
                .seed(1)
                .role(principalRoleName)
                .gender(gender)
                .ageGroup(ageGroup)
                .get();

        User user = helper.user()
                .seed(2)
                .role(userRoleName)
                .gender(gender)
                .ageGroup(ageGroup)
                .get();

        mockMvc.perform(get(GET_USER_DETAIL_URL, user.getId()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is(ExceptionMessage.ROLES_MISMATCH.getName())))
                .andDo(print());
    }

    static Stream<Arguments> getUserDetailParams_rolesMismatch() {
        return Stream.of(
                Arguments.of(Role.ROLE_SUPERADMIN, Role.ROLE_SUPERADMIN),
                Arguments.of(Role.ROLE_SUPERADMIN, Role.ROLE_CUSTOMER),
                Arguments.of(Role.ROLE_SUPERADMIN, Role.ROLE_INSTRUCTOR),
                Arguments.of(Role.ROLE_ADMIN, Role.ROLE_ADMIN),
                Arguments.of(Role.ROLE_ADMIN, Role.ROLE_SUPERADMIN),
                Arguments.of(Role.ROLE_CUSTOMER, Role.ROLE_CUSTOMER),
                Arguments.of(Role.ROLE_CUSTOMER, Role.ROLE_SUPERADMIN),
                Arguments.of(Role.ROLE_CUSTOMER, Role.ROLE_ADMIN),
                Arguments.of(Role.ROLE_CUSTOMER, Role.ROLE_INSTRUCTOR),
                Arguments.of(Role.ROLE_INSTRUCTOR, Role.ROLE_INSTRUCTOR),
                Arguments.of(Role.ROLE_INSTRUCTOR, Role.ROLE_CUSTOMER),
                Arguments.of(Role.ROLE_INSTRUCTOR, Role.ROLE_SUPERADMIN),
                Arguments.of(Role.ROLE_INSTRUCTOR, Role.ROLE_ADMIN));
    }
}