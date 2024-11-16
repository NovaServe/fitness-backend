/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.profiles.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.novaserve.fitness.config.Docker;
import com.novaserve.fitness.config.TestBeans;
import com.novaserve.fitness.exceptions.ExceptionMessage;
import com.novaserve.fitness.helpers.DbHelper;
import com.novaserve.fitness.helpers.builders.payments.SubscriptionPlanTestDataBuilder;
import com.novaserve.fitness.helpers.builders.profiles.AreaTestDataBuilder;
import com.novaserve.fitness.helpers.builders.profiles.ClubAddressTestDataBuilder;
import com.novaserve.fitness.helpers.builders.profiles.ClubScheduleTestDataBuilder;
import com.novaserve.fitness.helpers.builders.profiles.ClubTestDataBuilder;
import com.novaserve.fitness.helpers.builders.profiles.UserTestDataBuilder;
import com.novaserve.fitness.payments.model.SubscriptionPlan;
import com.novaserve.fitness.profiles.dto.response.AdminDetailsDto;
import com.novaserve.fitness.profiles.dto.response.CustomerDetailsDto;
import com.novaserve.fitness.profiles.dto.response.InstructorDetailsDto;
import com.novaserve.fitness.profiles.dto.response.UserDetailsBaseDto;
import com.novaserve.fitness.profiles.model.*;
import com.novaserve.fitness.profiles.model.Club;
import com.novaserve.fitness.profiles.model.ClubAddress;
import com.novaserve.fitness.profiles.model.ClubSchedule;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
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
    }

    @ParameterizedTest
    @MethodSource("methodParams_testGetUserDetails_WhenSuperAdminRequest_ShouldReturnUserDetails")
    @WithMockUser(username = "username1", password = "Password1!", roles = "SUPERADMIN")
    public void testGetUserDetails_WhenSuperAdminRequest_ShouldReturnUserDetails(Role roleOfRequestedUser)
            throws Exception {
        ClubAddress clubAddress = new ClubAddressTestDataBuilder().withSeed(1).build();
        ClubSchedule clubSchedule = dbHelper.save(
                new ClubScheduleTestDataBuilder().withSeed(1).withDefaultTime().build());
        Club club = dbHelper.save(new ClubTestDataBuilder()
                .withSeed(1)
                .with(c -> c.setId(1L))
                .with(c -> c.setAddress(clubAddress))
                .with(c -> c.setSchedule(clubSchedule))
                .build());
        Admin principal = dbHelper.save(new UserTestDataBuilder<>(Admin.class)
                .withSeed(1)
                .with(u -> u.setRole(Role.ROLE_SUPERADMIN))
                .with(u -> u.setClubs(Set.of(club)))
                .with(u -> u.setStartDate(LocalDate.now().minusWeeks(1)))
                .build());

        UserBase requestedUser =
                switch (roleOfRequestedUser) {
                    case ROLE_SUPERADMIN, ROLE_ADMIN -> dbHelper.save(new UserTestDataBuilder<>(Admin.class)
                            .withSeed(2)
                            .with(u -> u.setRole(roleOfRequestedUser))
                            .with(u -> u.setClubs(Set.of(club)))
                            .with(u -> u.setStartDate(LocalDate.now().minusWeeks(1)))
                            .build());
                    default -> throw new IllegalArgumentException();
                };

        MvcResult mvcResult = mockMvc.perform(
                        get(URL.GET_USER_DETAILS, requestedUser.getId()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        assertHelper(mvcResult, requestedUser);
    }

    public static Stream<Arguments> methodParams_testGetUserDetails_WhenSuperAdminRequest_ShouldReturnUserDetails() {
        return Stream.of(Arguments.of(Role.ROLE_SUPERADMIN), Arguments.of(Role.ROLE_ADMIN));
    }

    @ParameterizedTest
    @MethodSource("methodParams_testGetUserDetails_WhenAdminRequest_ShouldReturnUserDetails")
    @WithMockUser(username = "username1", password = "Password1!", roles = "ADMIN")
    public void testGetUserDetails_WhenAdminRequest_ShouldReturnUserDetails(Role roleOfRequestedUser) throws Exception {
        ClubAddress clubAddress = new ClubAddressTestDataBuilder().withSeed(1).build();
        ClubSchedule clubSchedule = dbHelper.save(
                new ClubScheduleTestDataBuilder().withSeed(1).withDefaultTime().build());
        Club club = dbHelper.save(new ClubTestDataBuilder()
                .withSeed(1)
                .with(c -> c.setId(1L))
                .with(c -> c.setAddress(clubAddress))
                .with(c -> c.setSchedule(clubSchedule))
                .build());
        Admin principal = dbHelper.save(new UserTestDataBuilder<>(Admin.class)
                .withSeed(1)
                .with(u -> u.setRole(Role.ROLE_ADMIN))
                .with(u -> u.setClubs(Set.of(club)))
                .with(u -> u.setStartDate(LocalDate.now().minusWeeks(1)))
                .build());

        UserBase requestedUser =
                switch (roleOfRequestedUser) {
                    case ROLE_ADMIN -> dbHelper.save(new UserTestDataBuilder<>(Admin.class)
                            .withSeed(2)
                            .with(u -> u.setRole(roleOfRequestedUser))
                            .with(u -> u.setClubs(Set.of(club)))
                            .with(u -> u.setStartDate(LocalDate.now().minusWeeks(1)))
                            .build());
                    case ROLE_INSTRUCTOR -> {
                        Area area1 = dbHelper.save(
                                new AreaTestDataBuilder().withSeed(1).build());
                        Area area2 = dbHelper.save(
                                new AreaTestDataBuilder().withSeed(2).build());
                        yield dbHelper.save(new UserTestDataBuilder<>(Instructor.class)
                                .withSeed(2)
                                .with(u -> u.setRole(roleOfRequestedUser))
                                .with(u -> u.setClubs(Set.of(club)))
                                .with(u -> u.setAreas(Set.of(area1, area2)))
                                .with(u -> u.setStartDate(LocalDate.now().minusWeeks(1)))
                                .build());
                    }
                    case ROLE_CUSTOMER -> {
                        SubscriptionPlan subscriptionPlan = dbHelper.save(new SubscriptionPlanTestDataBuilder()
                                .withSeed(1)
                                .with(s -> s.setClub(club))
                                .build());
                        yield dbHelper.save(new UserTestDataBuilder<>(Customer.class)
                                .withSeed(2)
                                .with(u -> u.setRole(roleOfRequestedUser))
                                .with(u -> u.setSubscriptionPlan(subscriptionPlan))
                                .build());
                    }

                    default -> throw new IllegalArgumentException();
                };

        MvcResult mvcResult = mockMvc.perform(
                        get(URL.GET_USER_DETAILS, requestedUser.getId()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        assertHelper(mvcResult, requestedUser);
    }

    public static Stream<Arguments> methodParams_testGetUserDetails_WhenAdminRequest_ShouldReturnUserDetails() {
        return Stream.of(
                Arguments.of(Role.ROLE_ADMIN), Arguments.of(Role.ROLE_INSTRUCTOR), Arguments.of(Role.ROLE_CUSTOMER));
    }

    @ParameterizedTest
    @MethodSource("methodParams_testGetUserDetails_WhenAdminRequestAndRolesMismatch_ShouldThrowException")
    @WithMockUser(username = "username1", password = "Password1!", roles = "ADMIN")
    public void testGetUserDetails_WhenAdminRequestAndRolesMismatch_ShouldThrowException(Role roleOfRequestedUser)
            throws Exception {
        ClubAddress clubAddress = new ClubAddressTestDataBuilder().withSeed(1).build();
        ClubSchedule clubSchedule = dbHelper.save(
                new ClubScheduleTestDataBuilder().withSeed(1).withDefaultTime().build());
        Club club = dbHelper.save(new ClubTestDataBuilder()
                .withSeed(1)
                .with(c -> c.setId(1L))
                .with(c -> c.setAddress(clubAddress))
                .with(c -> c.setSchedule(clubSchedule))
                .build());
        Admin principal = dbHelper.save(new UserTestDataBuilder<>(Admin.class)
                .withSeed(1)
                .with(u -> u.setRole(Role.ROLE_ADMIN))
                .with(u -> u.setClubs(Set.of(club)))
                .with(u -> u.setStartDate(LocalDate.now().minusWeeks(1)))
                .build());

        UserBase requestedUser =
                switch (roleOfRequestedUser) {
                    case ROLE_SUPERADMIN -> dbHelper.save(new UserTestDataBuilder<>(Admin.class)
                            .withSeed(2)
                            .with(u -> u.setRole(roleOfRequestedUser))
                            .with(u -> u.setClubs(Set.of(club)))
                            .with(u -> u.setStartDate(LocalDate.now().minusWeeks(1)))
                            .build());
                    default -> throw new IllegalArgumentException();
                };

        mockMvc.perform(get(URL.GET_USER_DETAILS, requestedUser.getId()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is(ExceptionMessage.ROLES_MISMATCH.getName())))
                .andDo(print());
    }

    public static Stream<Arguments>
            methodParams_testGetUserDetails_WhenAdminRequestAndRolesMismatch_ShouldThrowException() {
        return Stream.of(Arguments.of(Role.ROLE_SUPERADMIN));
    }

    @ParameterizedTest
    @MethodSource("methodParams_testGetUserDetails_WhenInstructorRequest_ShouldReturnUserDetails")
    @WithMockUser(username = "username1", password = "Password1!", roles = "INSTRUCTOR")
    public void testGetUserDetails_WhenInstructorRequest_ShouldReturnUserDetails(Role roleOfRequestedUser)
            throws Exception {
        ClubAddress clubAddress = new ClubAddressTestDataBuilder().withSeed(1).build();
        ClubSchedule clubSchedule = dbHelper.save(
                new ClubScheduleTestDataBuilder().withSeed(1).withDefaultTime().build());
        Club club = dbHelper.save(new ClubTestDataBuilder()
                .withSeed(1)
                .with(c -> c.setId(1L))
                .with(c -> c.setAddress(clubAddress))
                .with(c -> c.setSchedule(clubSchedule))
                .build());
        Area area1 = dbHelper.save(new AreaTestDataBuilder().withSeed(1).build());
        Area area2 = dbHelper.save(new AreaTestDataBuilder().withSeed(2).build());
        Instructor principal = dbHelper.save(new UserTestDataBuilder<>(Instructor.class)
                .withSeed(1)
                .with(u -> u.setRole(Role.ROLE_INSTRUCTOR))
                .with(u -> u.setClubs(Set.of(club)))
                .with(u -> u.setAreas(Set.of(area1, area2)))
                .with(u -> u.setStartDate(LocalDate.now().minusWeeks(1)))
                .build());

        UserBase requestedUser =
                switch (roleOfRequestedUser) {
                    case ROLE_ADMIN -> dbHelper.save(new UserTestDataBuilder<>(Admin.class)
                            .withSeed(2)
                            .with(u -> u.setRole(roleOfRequestedUser))
                            .with(u -> u.setClubs(Set.of(club)))
                            .with(u -> u.setStartDate(LocalDate.now().minusWeeks(1)))
                            .build());
                    case ROLE_INSTRUCTOR -> dbHelper.save(new UserTestDataBuilder<>(Instructor.class)
                            .withSeed(2)
                            .with(u -> u.setRole(roleOfRequestedUser))
                            .with(u -> u.setClubs(Set.of(club)))
                            .with(u -> u.setAreas(Set.of(area1, area2)))
                            .with(u -> u.setStartDate(LocalDate.now().minusWeeks(1)))
                            .build());
                    case ROLE_CUSTOMER -> {
                        SubscriptionPlan subscriptionPlan = dbHelper.save(new SubscriptionPlanTestDataBuilder()
                                .withSeed(1)
                                .with(s -> s.setClub(club))
                                .build());
                        yield dbHelper.save(new UserTestDataBuilder<>(Customer.class)
                                .withSeed(2)
                                .with(u -> u.setRole(roleOfRequestedUser))
                                .with(u -> u.setSubscriptionPlan(subscriptionPlan))
                                .build());
                    }
                    default -> throw new IllegalArgumentException();
                };

        MvcResult mvcResult = mockMvc.perform(
                        get(URL.GET_USER_DETAILS, requestedUser.getId()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        assertHelper(mvcResult, requestedUser);
    }

    public static Stream<Arguments> methodParams_testGetUserDetails_WhenInstructorRequest_ShouldReturnUserDetails() {
        return Stream.of(
                Arguments.of(Role.ROLE_ADMIN), Arguments.of(Role.ROLE_INSTRUCTOR), Arguments.of(Role.ROLE_CUSTOMER));
    }

    @ParameterizedTest
    @MethodSource("methodParams_testGetUserDetails_WhenInstructorRequestAndRolesMismatch_ShouldThrowException")
    @WithMockUser(username = "username1", password = "Password1!", roles = "INSTRUCTOR")
    public void testGetUserDetails_WhenInstructorRequestAndRolesMismatch_ShouldThrowException(Role roleOfRequestedUser)
            throws Exception {
        ClubAddress clubAddress = new ClubAddressTestDataBuilder().withSeed(1).build();
        ClubSchedule clubSchedule = dbHelper.save(
                new ClubScheduleTestDataBuilder().withSeed(1).withDefaultTime().build());
        Club club = dbHelper.save(new ClubTestDataBuilder()
                .withSeed(1)
                .with(c -> c.setId(1L))
                .with(c -> c.setAddress(clubAddress))
                .with(c -> c.setSchedule(clubSchedule))
                .build());
        Area area1 = dbHelper.save(new AreaTestDataBuilder().withSeed(1).build());
        Area area2 = dbHelper.save(new AreaTestDataBuilder().withSeed(2).build());
        Instructor principal = dbHelper.save(new UserTestDataBuilder<>(Instructor.class)
                .withSeed(1)
                .with(u -> u.setRole(Role.ROLE_INSTRUCTOR))
                .with(u -> u.setClubs(Set.of(club)))
                .with(u -> u.setAreas(Set.of(area1, area2)))
                .with(u -> u.setStartDate(LocalDate.now().minusWeeks(1)))
                .build());

        UserBase requestedUser =
                switch (roleOfRequestedUser) {
                    case ROLE_SUPERADMIN -> dbHelper.save(new UserTestDataBuilder<>(Admin.class)
                            .withSeed(2)
                            .with(u -> u.setRole(roleOfRequestedUser))
                            .with(u -> u.setClubs(Set.of(club)))
                            .with(u -> u.setStartDate(LocalDate.now().minusWeeks(1)))
                            .build());
                    default -> throw new IllegalArgumentException();
                };

        mockMvc.perform(get(URL.GET_USER_DETAILS, requestedUser.getId()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is(ExceptionMessage.ROLES_MISMATCH.getName())))
                .andDo(print());
    }

    public static Stream<Arguments>
            methodParams_testGetUserDetails_WhenInstructorRequestAndRolesMismatch_ShouldThrowException() {
        return Stream.of(Arguments.of(Role.ROLE_SUPERADMIN));
    }

    @ParameterizedTest
    @MethodSource("methodParams_testGetUserDetails_WhenCustomerRequest_ShouldReturnUserDetails")
    @WithMockUser(username = "username1", password = "Password1!", roles = "CUSTOMER")
    public void testGetUserDetails_WhenCustomerRequest_ShouldReturnUserDetails(Role roleOfRequestedUser)
            throws Exception {
        ClubAddress clubAddress = new ClubAddressTestDataBuilder().withSeed(1).build();
        ClubSchedule clubSchedule = dbHelper.save(
                new ClubScheduleTestDataBuilder().withSeed(1).withDefaultTime().build());
        Club club = dbHelper.save(new ClubTestDataBuilder()
                .withSeed(1)
                .with(c -> c.setId(1L))
                .with(c -> c.setAddress(clubAddress))
                .with(c -> c.setSchedule(clubSchedule))
                .build());
        SubscriptionPlan subscriptionPlan = dbHelper.save(new SubscriptionPlanTestDataBuilder()
                .withSeed(1)
                .with(s -> s.setClub(club))
                .build());
        Customer principal = dbHelper.save(new UserTestDataBuilder<>(Customer.class)
                .withSeed(1)
                .with(u -> u.setRole(Role.ROLE_CUSTOMER))
                .with(u -> u.setSubscriptionPlan(subscriptionPlan))
                .build());

        UserBase requestedUser =
                switch (roleOfRequestedUser) {
                    case ROLE_ADMIN -> dbHelper.save(new UserTestDataBuilder<>(Admin.class)
                            .withSeed(2)
                            .with(u -> u.setRole(roleOfRequestedUser))
                            .with(u -> u.setClubs(Set.of(club)))
                            .with(u -> u.setStartDate(LocalDate.now().minusWeeks(1)))
                            .build());
                    case ROLE_INSTRUCTOR -> {
                        Area area1 = dbHelper.save(
                                new AreaTestDataBuilder().withSeed(1).build());
                        Area area2 = dbHelper.save(
                                new AreaTestDataBuilder().withSeed(2).build());

                        yield dbHelper.save(new UserTestDataBuilder<>(Instructor.class)
                                .withSeed(2)
                                .with(u -> u.setRole(roleOfRequestedUser))
                                .with(u -> u.setClubs(Set.of(club)))
                                .with(u -> u.setAreas(Set.of(area1, area2)))
                                .with(u -> u.setStartDate(LocalDate.now().minusWeeks(1)))
                                .build());
                    }
                    case ROLE_CUSTOMER -> principal;
                    default -> throw new IllegalArgumentException();
                };

        MvcResult mvcResult = mockMvc.perform(
                        get(URL.GET_USER_DETAILS, requestedUser.getId()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        assertHelper(mvcResult, requestedUser);
    }

    public static Stream<Arguments> methodParams_testGetUserDetails_WhenCustomerRequest_ShouldReturnUserDetails() {
        return Stream.of(
                Arguments.of(Role.ROLE_ADMIN), Arguments.of(Role.ROLE_INSTRUCTOR), Arguments.of(Role.ROLE_CUSTOMER));
    }

    @ParameterizedTest
    @MethodSource("methodParams_testGetUserDetails_WhenCustomerRequestAndRolesMismatch_ShouldThrowException")
    @WithMockUser(username = "username1", password = "Password1!", roles = "CUSTOMER")
    public void testGetUserDetails_WhenCustomerRequestAndRolesMismatch_ShouldThrowException(Role roleOfRequestedUser)
            throws Exception {
        ClubAddress clubAddress = new ClubAddressTestDataBuilder().withSeed(1).build();
        ClubSchedule clubSchedule = dbHelper.save(
                new ClubScheduleTestDataBuilder().withSeed(1).withDefaultTime().build());
        Club club = dbHelper.save(new ClubTestDataBuilder()
                .withSeed(1)
                .with(c -> c.setId(1L))
                .with(c -> c.setAddress(clubAddress))
                .with(c -> c.setSchedule(clubSchedule))
                .build());
        SubscriptionPlan subscriptionPlan = dbHelper.save(new SubscriptionPlanTestDataBuilder()
                .withSeed(1)
                .with(s -> s.setClub(club))
                .build());
        Customer principal = dbHelper.save(new UserTestDataBuilder<>(Customer.class)
                .withSeed(1)
                .with(u -> u.setRole(Role.ROLE_CUSTOMER))
                .with(u -> u.setSubscriptionPlan(subscriptionPlan))
                .build());

        UserBase requestedUser =
                switch (roleOfRequestedUser) {
                    case ROLE_SUPERADMIN -> dbHelper.save(new UserTestDataBuilder<>(Admin.class)
                            .withSeed(2)
                            .with(u -> u.setRole(roleOfRequestedUser))
                            .with(u -> u.setClubs(Set.of(club)))
                            .with(u -> u.setStartDate(LocalDate.now().minusWeeks(1)))
                            .build());
                    case ROLE_CUSTOMER -> dbHelper.save(new UserTestDataBuilder<>(Customer.class)
                            .withSeed(2)
                            .with(u -> u.setRole(Role.ROLE_CUSTOMER))
                            .with(u -> u.setSubscriptionPlan(subscriptionPlan))
                            .build());
                    default -> throw new IllegalArgumentException();
                };

        mockMvc.perform(get(URL.GET_USER_DETAILS, requestedUser.getId()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is(ExceptionMessage.ROLES_MISMATCH.getName())))
                .andDo(print());
    }

    public static Stream<Arguments>
            methodParams_testGetUserDetails_WhenCustomerRequestAndRolesMismatch_ShouldThrowException() {
        return Stream.of(Arguments.of(Role.ROLE_SUPERADMIN), Arguments.of(Role.ROLE_CUSTOMER));
    }

    private void assertHelper(MvcResult mvcResult, UserBase expectedUser)
            throws UnsupportedEncodingException, JsonProcessingException {
        Class<?> clazz = expectedUser.getClass();
        JavaType javaType =
                switch (clazz.getSimpleName()) {
                    case "Admin" -> TypeFactory.defaultInstance().constructType(AdminDetailsDto.class);
                    case "Instructor" -> TypeFactory.defaultInstance().constructType(InstructorDetailsDto.class);
                    case "Customer" -> TypeFactory.defaultInstance().constructType(CustomerDetailsDto.class);
                    default -> throw new IllegalStateException("Unexpected value: " + clazz.getSimpleName());
                };
        UserDetailsBaseDto userDetailsBaseDto =
                objectMapper.readValue(mvcResult.getResponse().getContentAsString(), javaType);

        BiPredicate<String, Role> roleBiPredicate = (string, enumeration) -> compareHelper(string, enumeration);
        BiPredicate<String, Gender> genderBiPredicate = (string, enumeration) -> compareHelper(string, enumeration);
        BiPredicate<String, AgeGroup> ageGroupBiPredicate = (string, enumeration) -> compareHelper(string, enumeration);
        BiPredicate<LocalDateTime, LocalDateTime> localDateTimeBiPredicate =
                (actual, expected) -> dateTimeCompareHelper(actual, expected);

        String[] ignoringFields = new String[] {"trainings"};

        assertThat(userDetailsBaseDto)
                .usingRecursiveComparison()
                .ignoringFields(ignoringFields)
                .withEqualsForFields(roleBiPredicate, "role")
                .withEqualsForFields(genderBiPredicate, "gender")
                .withEqualsForFields(ageGroupBiPredicate, "ageGroup")
                .withEqualsForFields(localDateTimeBiPredicate, "createdAt", "lastModifiedAt")
                .isEqualTo(expectedUser);
    }

    private <T extends Enum<T>> boolean compareHelper(String string, Enum<T> enumeration) {
        if (string == null) {
            return enumeration == null;
        }
        return string.equals(enumeration == null ? null : enumeration.name());
    }

    private boolean dateTimeCompareHelper(LocalDateTime t1, LocalDateTime t2) {
        int compare = t1.truncatedTo(ChronoUnit.SECONDS).compareTo(t2.truncatedTo(ChronoUnit.SECONDS));
        return compare == 0;
    }
}
