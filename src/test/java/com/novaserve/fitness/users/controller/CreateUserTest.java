/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.users.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.*;
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
import com.novaserve.fitness.users.model.Role;
import com.novaserve.fitness.users.model.User;
import java.util.function.BiPredicate;
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

    @BeforeEach
    void beforeEach() {
        helper.deleteAll();
    }

    @Test
    @WithMockUser(username = "username1", password = "Password1!", roles = "SUPERADMIN")
    void createUser_shouldCreateAdmin_whenSuperadminRequests() throws Exception {
        User superadmin =
                helper.user().seed(1).role(Role.ROLE_SUPERADMIN).build().save(User.class);

        CreateUserRequestDto dto =
                dtoHelper.createUserRequestDto().seed(2).role(Role.ROLE_ADMIN).build();

        mockMvc.perform(post(CREATE_USER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").doesNotExist())
                .andDo(print());
        assertHelper(dto);
    }

    @ParameterizedTest
    @MethodSource("methodParams_createUser_shouldCreateCustomerOrInstructor_whenAdminRequests")
    @WithMockUser(username = "username1", password = "Password1!", roles = "ADMIN")
    void createUser_shouldCreateCustomerOrInstructor_whenAdminRequests(Role role) throws Exception {
        User admin = helper.user().seed(1).role(Role.ROLE_ADMIN).build().save(User.class);

        CreateUserRequestDto dto =
                dtoHelper.createUserRequestDto().seed(2).role(role).build();

        mockMvc.perform(post(CREATE_USER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").doesNotExist())
                .andDo(print());
        assertHelper(dto);
    }

    static Stream<Arguments> methodParams_createUser_shouldCreateCustomerOrInstructor_whenAdminRequests() {
        return Stream.of(Arguments.of(Role.ROLE_CUSTOMER), Arguments.of(Role.ROLE_INSTRUCTOR));
    }

    @ParameterizedTest
    @MethodSource("methodParams_createUser_shouldThrowException_whenRolesMismatch")
    @WithMockUser(username = "username1", password = "Password1!", roles = "SUPERADMIN")
    void createUser_shouldThrowException_whenRolesMismatch(Role principalRole, Role newUserRole) throws Exception {
        User principal = helper.user().seed(1).role(principalRole).build().save(User.class);

        CreateUserRequestDto dto =
                dtoHelper.createUserRequestDto().seed(2).role(newUserRole).build();

        mockMvc.perform(post(CREATE_USER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is(ExceptionMessage.ROLES_MISMATCH.getName())))
                .andDo(print());
        assertNull(helper.getUser(dto.getUsername()));
    }

    static Stream<Arguments> methodParams_createUser_shouldThrowException_whenRolesMismatch() {
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
                Arguments.of(Role.ROLE_INSTRUCTOR, Role.ROLE_INSTRUCTOR),
                Arguments.of(Role.ROLE_INSTRUCTOR, Role.ROLE_SUPERADMIN),
                Arguments.of(Role.ROLE_INSTRUCTOR, Role.ROLE_ADMIN));
    }

    @Test
    @WithMockUser(username = "username1", password = "Password1!", roles = "ADMIN")
    void createUser_shouldThrowException_whenUserAlreadyExists() throws Exception {
        User user = helper.user().seed(1).role(Role.ROLE_ADMIN).build().save(User.class);

        User userAlreadyExists =
                helper.user().seed(2).role(Role.ROLE_CUSTOMER).build().save(User.class);

        CreateUserRequestDto dto = dtoHelper
                .createUserRequestDto()
                .seed(2)
                .role(Role.ROLE_CUSTOMER)
                .build();

        mockMvc.perform(post(CREATE_USER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is(ExceptionMessage.ALREADY_EXISTS.getName())))
                .andDo(print());
    }

    void assertHelper(CreateUserRequestDto dto) {
        User actual = helper.getUser(dto.getUsername());

        String[] comparatorIgnoreFields = new String[] {"id", "assignments", "instructorTrainings"};
        BiPredicate<String, String> passwordBiPredicate = (encoded, raw) -> passwordEncoder.matches(raw, encoded);

        assertThat(actual)
                .usingRecursiveComparison()
                .withEqualsForFields(passwordBiPredicate, "password")
                .ignoringFields(comparatorIgnoreFields)
                .isEqualTo(dto);
        assertNotNull(actual.getId());
    }
}
