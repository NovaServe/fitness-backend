/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.auth.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.novaserve.fitness.auth.dto.LoginRequestDto;
import com.novaserve.fitness.config.Docker;
import com.novaserve.fitness.config.TestBeans;
import com.novaserve.fitness.exception.ExceptionMessage;
import com.novaserve.fitness.helpers.DbHelper;
import com.novaserve.fitness.helpers.DtoHelper;
import com.novaserve.fitness.users.model.AgeGroup;
import com.novaserve.fitness.users.model.Gender;
import com.novaserve.fitness.users.model.Role;
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
class LoginTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    DbHelper $db;

    @Autowired
    DtoHelper $dto;

    final String LOGIN_URL = "/api/v1/auth/login";

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

    @Container
    static PostgreSQLContainer<?> postgresqlContainer =
            new PostgreSQLContainer<>(DockerImageName.parse(Docker.POSTGRES));

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresqlContainer::getUsername);
        registry.add("spring.datasource.password", postgresqlContainer::getPassword);
    }

    @ParameterizedTest
    @MethodSource("loginCredentialTypes")
    void login_shouldAuthenticateUser_whenValidCredentials(String roleName, String credentialType) throws Exception {
        var user = $db.user()
                .seed(1)
                .role(getRole(roleName))
                .gender(gender)
                .ageGroup(ageGroup)
                .get();
        var dto = getDto(credentialType);

        var mvcResult = mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role", is(getRole(roleName).getName())))
                .andExpect(jsonPath("$.fullName", is(user.getFullName())))
                .andDo(print())
                .andReturn();

        var cookie = mvcResult.getResponse().getCookie("token");
        assertNotNull(cookie.getValue());
        assertNotNull(cookie.getAttribute("Expires"));
        assertEquals("true", cookie.getAttribute("HttpOnly"));
        assertEquals("/api/v1", cookie.getAttribute("Path"));
        assertEquals("Strict", cookie.getAttribute("SameSite"));
    }

    static Stream<Arguments> loginCredentialTypes() {
        return Stream.of(
                Arguments.of("ROLE_SUPERADMIN", "username"),
                Arguments.of("ROLE_SUPERADMIN", "email"),
                Arguments.of("ROLE_SUPERADMIN", "phone"),
                Arguments.of("ROLE_ADMIN", "username"),
                Arguments.of("ROLE_ADMIN", "email"),
                Arguments.of("ROLE_ADMIN", "phone"),
                Arguments.of("ROLE_CUSTOMER", "username"),
                Arguments.of("ROLE_CUSTOMER", "email"),
                Arguments.of("ROLE_CUSTOMER", "phone"),
                Arguments.of("ROLE_INSTRUCTOR", "username"),
                Arguments.of("ROLE_INSTRUCTOR", "email"),
                Arguments.of("ROLE_INSTRUCTOR", "phone"));
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

    LoginRequestDto getDto(String credentialType) {
        return switch (credentialType) {
            case "username" -> $dto.loginRequestDto().seed(1).withUsername().get();
            case "email" -> $dto.loginRequestDto().seed(1).withEmail().get();
            case "phone" -> $dto.loginRequestDto().seed(1).withPhone().get();
            default -> throw new IllegalStateException("Unexpected value: " + credentialType);
        };
    }

    @Test
    void login_shouldThrowException_whenInvalidCredentials() throws Exception {
        $db.user().seed(1).role(adminRole).gender(gender).ageGroup(ageGroup).get();
        var dto = $dto.loginRequestDto().seed(2).withUsername().get();

        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", is(ExceptionMessage.INVALID_CREDENTIALS.getName())))
                .andDo(print());
    }
}
