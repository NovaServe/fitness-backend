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
import com.novaserve.fitness.auth.dto.request.LoginRequestDto;
import com.novaserve.fitness.config.Docker;
import com.novaserve.fitness.config.TestBeans;
import com.novaserve.fitness.exception.ExceptionMessage;
import com.novaserve.fitness.helpers.DbHelper;
import com.novaserve.fitness.helpers.DtoHelper;
import com.novaserve.fitness.users.model.User;
import com.novaserve.fitness.users.model.enums.Role;
import jakarta.servlet.http.Cookie;
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
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Import(TestBeans.class)
class LoginTest {
    @Container
    public static PostgreSQLContainer<?> postgresqlContainer =
            new PostgreSQLContainer<>(DockerImageName.parse(Docker.POSTGRES));

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DbHelper helper;

    @Autowired
    private DtoHelper dtoHelper;

    @DynamicPropertySource
    public static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresqlContainer::getUsername);
        registry.add("spring.datasource.password", postgresqlContainer::getPassword);
    }

    @BeforeEach
    public void beforeEach() {
        helper.deleteAll();
    }

    @ParameterizedTest
    @MethodSource("loginCredentialTypes")
    public void login_shouldAuthenticateUser_whenValidCredentials(Role role, String credentialType) throws Exception {
        User user = helper.user().seed(1).role(role).build().save(User.class);
        LoginRequestDto loginRequestDto = getDto(credentialType);

        MvcResult mvcResult = mockMvc.perform(post(URL.LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role", is(role.name())))
                .andExpect(jsonPath("$.fullName", is(user.getFullName())))
                .andDo(print())
                .andReturn();

        Cookie cookie = mvcResult.getResponse().getCookie("token");
        assertNotNull(cookie.getValue());
        assertNotNull(cookie.getAttribute("Expires"));
        assertEquals("true", cookie.getAttribute("HttpOnly"));
        assertEquals("/api/v1", cookie.getAttribute("Path"));
        assertEquals("Strict", cookie.getAttribute("SameSite"));
    }

    public static Stream<Arguments> loginCredentialTypes() {
        return Stream.of(
                Arguments.of(Role.ROLE_SUPERADMIN, "username"),
                Arguments.of(Role.ROLE_SUPERADMIN, "email"),
                Arguments.of(Role.ROLE_SUPERADMIN, "phone"),
                Arguments.of(Role.ROLE_ADMIN, "username"),
                Arguments.of(Role.ROLE_ADMIN, "email"),
                Arguments.of(Role.ROLE_ADMIN, "phone"),
                Arguments.of(Role.ROLE_CUSTOMER, "username"),
                Arguments.of(Role.ROLE_CUSTOMER, "email"),
                Arguments.of(Role.ROLE_CUSTOMER, "phone"),
                Arguments.of(Role.ROLE_INSTRUCTOR, "username"),
                Arguments.of(Role.ROLE_INSTRUCTOR, "email"),
                Arguments.of(Role.ROLE_INSTRUCTOR, "phone"));
    }

    @Test
    public void login_shouldThrowException_whenInvalidCredentials() throws Exception {
        User principal = helper.user().seed(1).role(Role.ROLE_ADMIN).build().save(User.class);
        LoginRequestDto loginRequestDto =
                dtoHelper.loginRequestDto().seed(2).withUsername().build();

        mockMvc.perform(post(URL.LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", is(ExceptionMessage.INVALID_CREDENTIALS.getName())))
                .andDo(print());
    }

    private LoginRequestDto getDto(String credentialType) {
        return switch (credentialType) {
            case "username" -> dtoHelper
                    .loginRequestDto()
                    .seed(1)
                    .withUsername()
                    .build();
            case "email" -> dtoHelper.loginRequestDto().seed(1).withEmail().build();
            case "phone" -> dtoHelper.loginRequestDto().seed(1).withPhone().build();
            default -> throw new IllegalStateException("Unexpected value: " + credentialType);
        };
    }
}
