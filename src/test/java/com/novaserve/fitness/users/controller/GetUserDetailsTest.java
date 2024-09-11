/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.users.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.novaserve.fitness.config.Docker;
import com.novaserve.fitness.config.TestBeans;
import com.novaserve.fitness.exception.ExceptionMessage;
import com.novaserve.fitness.helpers.DbHelper;
import com.novaserve.fitness.helpers.DtoHelper;
import com.novaserve.fitness.users.dto.response.UserResponseDto;
import com.novaserve.fitness.users.model.User;
import com.novaserve.fitness.users.model.enums.AgeGroup;
import com.novaserve.fitness.users.model.enums.Gender;
import com.novaserve.fitness.users.model.enums.Role;
import java.io.UnsupportedEncodingException;
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
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Import(TestBeans.class)
class GetUserDetailsTest {
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
    @MethodSource("methodParams_getUserDetails_shouldReturnUserDetails_whenSuperadminRequestsOwnOrAdminDetail")
    @WithMockUser(username = "username1", password = "Password1!", roles = "SUPERADMIN")
    public void getUserDetails_shouldReturnUserDetails_whenSuperadminRequestsOwnOrAdminDetail(Role roleOfRequestedUser)
            throws Exception {
        User superadminPrincipal =
                helper.user().seed(1).role(Role.ROLE_SUPERADMIN).build().save(User.class);

        User requestedUser =
                switch (roleOfRequestedUser) {
                    case ROLE_SUPERADMIN -> superadminPrincipal;
                    case ROLE_ADMIN -> helper.user()
                            .role(Role.ROLE_ADMIN)
                            .build()
                            .save(User.class);
                    default -> throw new IllegalArgumentException("Unexpected value: " + roleOfRequestedUser);
                };

        MvcResult mvcResult = mockMvc.perform(
                        get(URL.GET_USER_DETAILS, requestedUser.getId()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        assertHelper(mvcResult, requestedUser);
    }

    public static Stream<Arguments>
            methodParams_getUserDetails_shouldReturnUserDetails_whenSuperadminRequestsOwnOrAdminDetail() {
        return Stream.of(Arguments.of(Role.ROLE_SUPERADMIN), Arguments.of(Role.ROLE_ADMIN));
    }

    @ParameterizedTest
    @MethodSource(
            "methodParams_getUserDetails_shouldReturnUserDetails_whenAdminRequestsOwnOrCustomerOrInstructorDetail")
    @WithMockUser(username = "username1", password = "Password1!", roles = "ADMIN")
    public void getUserDetails_shouldReturnUserDetails_whenAdminRequestsOwnOrCustomerOrInstructorDetail(
            Role roleOfRequestedUser) throws Exception {
        User adminPrincipal =
                helper.user().seed(1).role(Role.ROLE_ADMIN).build().save(User.class);

        User requestedUser =
                switch (roleOfRequestedUser) {
                    case ROLE_ADMIN -> adminPrincipal;
                    case ROLE_CUSTOMER -> helper.user()
                            .role(Role.ROLE_CUSTOMER)
                            .build()
                            .save(User.class);
                    case ROLE_INSTRUCTOR -> helper.user()
                            .role(Role.ROLE_INSTRUCTOR)
                            .build()
                            .save(User.class);
                    default -> throw new IllegalArgumentException("Unexpected value: " + roleOfRequestedUser);
                };

        MvcResult mvcResult = mockMvc.perform(
                        get(URL.GET_USER_DETAILS, requestedUser.getId()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        assertHelper(mvcResult, requestedUser);
    }

    public static Stream<Arguments>
            methodParams_getUserDetails_shouldReturnUserDetails_whenAdminRequestsOwnOrCustomerOrInstructorDetail() {
        return Stream.of(
                Arguments.of(Role.ROLE_ADMIN), Arguments.of(Role.ROLE_CUSTOMER), Arguments.of(Role.ROLE_INSTRUCTOR));
    }

    @Test
    @WithMockUser(username = "username1", password = "Password1!", roles = "CUSTOMER")
    public void getUserDetails_shouldReturnUserDetails_whenCustomerRequestsOwnDetail() throws Exception {

        User customerPrincipal =
                helper.user().seed(1).role(Role.ROLE_CUSTOMER).build().save(User.class);

        MvcResult mvcResult = mockMvc.perform(
                        get(URL.GET_USER_DETAILS, customerPrincipal.getId()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        assertHelper(mvcResult, customerPrincipal);
    }

    @Test
    @WithMockUser(username = "username1", password = "Password1!", roles = "INSTRUCTOR")
    public void getUserDetails_shouldReturnUserDetails_whenInstructorRequestsOwnDetail() throws Exception {
        User instructorPrincipal =
                helper.user().seed(1).role(Role.ROLE_INSTRUCTOR).build().save(User.class);

        MvcResult mvcResult = mockMvc.perform(
                        get(URL.GET_USER_DETAILS, instructorPrincipal.getId()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        assertHelper(mvcResult, instructorPrincipal);
    }

    @ParameterizedTest
    @MethodSource("methodParams_getUserDetails_shouldThrowException_whenRolesMismatch")
    @WithMockUser(username = "username1", password = "Password1!", roles = "SUPERADMIN")
    public void getUserDetails_shouldThrowException_whenRolesMismatch(Role principalRole, Role roleOfRequestedUser)
            throws Exception {
        User principal = helper.user().seed(1).role(principalRole).build().save(User.class);
        User requestedUser = helper.user().role(roleOfRequestedUser).build().save(User.class);

        mockMvc.perform(get(URL.GET_USER_DETAILS, requestedUser.getId()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is(ExceptionMessage.ROLES_MISMATCH.getName())))
                .andDo(print());
    }

    public static Stream<Arguments> methodParams_getUserDetails_shouldThrowException_whenRolesMismatch() {
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

    private void assertHelper(MvcResult mvcResult, User expectedUser)
            throws UnsupportedEncodingException, JsonProcessingException {
        UserResponseDto userResponseDto = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(), new TypeReference<UserResponseDto>() {});

        BiPredicate<String, Role> roleBiPredicate = (string, enumeration) -> string.equals(enumeration.name());
        BiPredicate<String, Gender> genderBiPredicate = (string, enumeration) -> string.equals(enumeration.name());
        BiPredicate<String, AgeGroup> ageGroupBiPredicate = (string, enumeration) -> string.equals(enumeration.name());

        assertThat(userResponseDto)
                .usingRecursiveComparison()
                .withEqualsForFields(roleBiPredicate, "role")
                .withEqualsForFields(genderBiPredicate, "gender")
                .withEqualsForFields(ageGroupBiPredicate, "ageGroup")
                .isEqualTo(expectedUser);
    }
}
