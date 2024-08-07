/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.users.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import com.novaserve.fitness.users.dto.CreateUserRequestDto;
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
    DbHelper $db;

    @Autowired
    DtoHelper $dto;

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
        $db.deleteAll();
        superadminRole = $db.superadminRole();
        adminRole = $db.adminRole();
        customerRole = $db.customerRole();
        instructorRole = $db.instructorRole();
        gender = $db.female();
        ageGroup = $db.adult();
    }

    void assertHelper(CreateUserRequestDto dto) {
        var actual = $db.getUser(dto.getUsername());
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
    void getUserDetail_superadminRequestsOwnOrAdminDetail() throws Exception {
        var superadmin = $db.user()
                .seed(1)
                .role(superadminRole)
                .gender(gender)
                .ageGroup(ageGroup)
                .get();
        var admin = $db.user()
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

    @Test
    @WithMockUser(username = "username1", password = "Password1!", roles = "ADMIN")
    void getUserDetail_adminRequestsOwnOrCustomerOrInstructorDetail() throws Exception {
        var admin = $db.user()
                .seed(1)
                .role(adminRole)
                .gender(gender)
                .ageGroup(ageGroup)
                .get();
        var customer = $db.user()
                .seed(2)
                .role(customerRole)
                .gender(gender)
                .ageGroup(ageGroup)
                .get();
        var instructor = $db.user()
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

    @Test
    @WithMockUser(username = "username1", password = "Password1!", roles = "CUSTOMER")
    void getUserDetail_customerRequestsOwnDetail() throws Exception {
        var customer = $db.user()
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
    void getUserDetail_instructorRequestsOwnDetail() throws Exception {
        var instructor = $db.user()
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
    void createUser_shouldThrowException_whenRolesMismatch(String principalRoleName, String userRoleName)
            throws Exception {
        var principal = $db.user()
                .seed(1)
                .role(getRole(principalRoleName))
                .gender(gender)
                .ageGroup(ageGroup)
                .get();

        var user = $db.user()
                .seed(2)
                .role(getRole(userRoleName))
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
        return switch (roleName) {
            case "ROLE_SUPERADMIN" -> superadminRole;
            case "ROLE_ADMIN" -> adminRole;
            case "ROLE_CUSTOMER" -> customerRole;
            case "ROLE_INSTRUCTOR" -> instructorRole;
            default -> throw new IllegalStateException("Unexpected value: " + roleName);
        };
    }
}
