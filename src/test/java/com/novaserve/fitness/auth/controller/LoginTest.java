/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.auth.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.novaserve.fitness.auth.dto.request.LoginRequestDto;
import com.novaserve.fitness.config.Docker;
import com.novaserve.fitness.config.TestBeans;
import com.novaserve.fitness.exceptions.ExceptionMessage;
import com.novaserve.fitness.helpers.DbHelper;
import com.novaserve.fitness.helpers.builders.profiles.UserTestDataBuilder;
import com.novaserve.fitness.helpers.builders.profiles.UserUtil;
import com.novaserve.fitness.profiles.model.Customer;
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
    private DbHelper dbHelper;

    @DynamicPropertySource
    public static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresqlContainer::getUsername);
        registry.add("spring.datasource.password", postgresqlContainer::getPassword);
    }

    @BeforeEach
    public void beforeEach() {
        dbHelper.deleteAll();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @ParameterizedTest
    @MethodSource("loginCredentialTypes")
    public void testLogin_WhenValidCredentials_ShouldAuthenticateUser(String credentialType) throws Exception {
        Customer principal = dbHelper.save(
                new UserTestDataBuilder<>(Customer.class).withSeed(1).build());
        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .password(UserUtil.generateRawPasswordWithSeed(1))
                .build();
        switch (credentialType) {
            case "username" -> loginRequestDto.setUsernameOrEmailOrPhone(principal.getUsername());
            case "email" -> loginRequestDto.setUsernameOrEmailOrPhone(principal.getEmail());
            case "phone" -> loginRequestDto.setUsernameOrEmailOrPhone(principal.getPhone());
        }

        mockMvc.perform(post(URL.LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role", is(principal.getRole().name())))
                .andExpect(jsonPath("$.fullName", is(principal.getFullName())))
                .andExpect(jsonPath("$.token", is(notNullValue())))
                .andDo(print());
    }

    public static Stream<Arguments> loginCredentialTypes() {
        return Stream.of(Arguments.of("username"), Arguments.of("email"), Arguments.of("phone"));
    }

    @Test
    public void testLogin_WhenInvalidCredentials_ShouldThrowException() throws Exception {
        Customer principal = dbHelper.save(
                new UserTestDataBuilder<>(Customer.class).withSeed(1).build());
        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .usernameOrEmailOrPhone(principal.getUsername())
                .password(UserUtil.generateRawPasswordWithSeed(2))
                .build();

        mockMvc.perform(post(URL.LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", is(ExceptionMessage.INVALID_CREDENTIALS.getName())))
                .andDo(print());
    }
}
