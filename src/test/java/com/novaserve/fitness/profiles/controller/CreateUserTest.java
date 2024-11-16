/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.profiles.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.novaserve.fitness.config.Docker;
import com.novaserve.fitness.config.TestBeans;
import com.novaserve.fitness.emails.RestTemplateService;
import com.novaserve.fitness.exceptions.ExceptionMessage;
import com.novaserve.fitness.helpers.DbHelper;
import com.novaserve.fitness.helpers.builders.payments.SubscriptionPlanTestDataBuilder;
import com.novaserve.fitness.helpers.builders.profiles.AreaTestDataBuilder;
import com.novaserve.fitness.helpers.builders.profiles.ClubAddressTestDataBuilder;
import com.novaserve.fitness.helpers.builders.profiles.ClubScheduleTestDataBuilder;
import com.novaserve.fitness.helpers.builders.profiles.ClubTestDataBuilder;
import com.novaserve.fitness.helpers.builders.profiles.UserDtoTestDataBuilder;
import com.novaserve.fitness.helpers.builders.profiles.UserTestDataBuilder;
import com.novaserve.fitness.payments.model.SubscriptionPlan;
import com.novaserve.fitness.profiles.dto.request.*;
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
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
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
class CreateUserTest {
    @Container
    public static PostgreSQLContainer<?> postgresqlContainer =
            new PostgreSQLContainer<>(DockerImageName.parse(Docker.POSTGRES));

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DbHelper dbHelper;

    @MockBean
    private RestTemplateService restTemplateService;

    @DynamicPropertySource
    public static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresqlContainer::getUsername);
        registry.add("spring.datasource.password", postgresqlContainer::getPassword);
    }

    @BeforeEach
    public void beforeEach() {
        dbHelper.deleteAll();
        doNothing().when(restTemplateService).postWithoutResponseBody(any(), any(), any());
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Test
    @WithMockUser(username = "username1", password = "Password1!", roles = "SUPERADMIN")
    public void testCreateUser_WhenSuperAdminCreatesAdmin_ShouldCreateSuccessfully() throws Exception {
        ClubAddress clubAddress = new ClubAddressTestDataBuilder().withSeed(1).build();
        ClubSchedule clubSchedule = dbHelper.save(
                new ClubScheduleTestDataBuilder().withSeed(1).withDefaultTime().build());
        Club club = dbHelper.save(new ClubTestDataBuilder()
                .withSeed(1)
                .with(c -> c.setAddress(clubAddress))
                .with(c -> c.setSchedule(clubSchedule))
                .build());
        SuperAdmin principal = dbHelper.save(new UserTestDataBuilder<>(SuperAdmin.class)
                .withSeed(1)
                .with(u -> u.setClubs(Set.of(club)))
                .with(u -> u.setStartDate(LocalDate.now().minusWeeks(1)))
                .build());

        AdminCreationDto userCreationDto = new UserDtoTestDataBuilder<>(AdminCreationDto.class)
                .withSeed(2)
                .with(dto -> dto.setClubsIds(Set.of(club.getId())))
                .build();

        MvcResult mvcResult = mockMvc.perform(post(URL.CREATE_USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreationDto)))
                .andExpect(status().isCreated())
                .andDo(print())
                .andReturn();

        UserDetailsBaseDto responseDto = parseResponseBody(mvcResult, AdminDetailsDto.class);
        assertNotNull(responseDto);
        assertNotNull(dbHelper.getUserByUsername(userCreationDto.getUsername()));
    }

    @Test
    @WithMockUser(username = "username1", password = "Password1!", roles = "SUPERADMIN")
    public void testCreateUser_WhenSuperAdminCreatesInstructor_ShouldCreateSuccessfully() throws Exception {
        ClubAddress clubAddress = new ClubAddressTestDataBuilder().withSeed(1).build();
        ClubSchedule clubSchedule = dbHelper.save(
                new ClubScheduleTestDataBuilder().withSeed(1).withDefaultTime().build());
        Club club = dbHelper.save(new ClubTestDataBuilder()
                .withSeed(1)
                .with(c -> c.setAddress(clubAddress))
                .with(c -> c.setSchedule(clubSchedule))
                .build());
        SuperAdmin principal = dbHelper.save(new UserTestDataBuilder<>(SuperAdmin.class)
                .withSeed(1)
                .with(u -> u.setClubs(Set.of(club)))
                .with(u -> u.setStartDate(LocalDate.now().minusWeeks(1)))
                .build());

        Area area1 = dbHelper.save(new AreaTestDataBuilder().withSeed(1).build());
        Area area2 = dbHelper.save(new AreaTestDataBuilder().withSeed(2).build());
        InstructorCreationDto userCreationDto = new UserDtoTestDataBuilder<>(InstructorCreationDto.class)
                .withSeed(2)
                .with(dto -> dto.setAreasIds(Set.of(area1.getId(), area2.getId())))
                .with(u -> u.setClubsIds(Set.of(club.getId())))
                .with(u -> u.setStartDate(LocalDate.now().minusWeeks(1)))
                .build();

        MvcResult mvcResult = mockMvc.perform(post(URL.CREATE_USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreationDto)))
                .andExpect(status().isCreated())
                .andDo(print())
                .andReturn();

        UserDetailsBaseDto responseDto = parseResponseBody(mvcResult, InstructorDetailsDto.class);
        assertNotNull(responseDto);
        assertNotNull(dbHelper.getUserByUsername(userCreationDto.getUsername()));
    }

    @Test
    @WithMockUser(username = "username1", password = "Password1!", roles = "SUPERADMIN")
    public void testCreateUser_WhenSuperAdminCreatesCustomer_ShouldCreateSuccessfully() throws Exception {
        ClubAddress clubAddress = new ClubAddressTestDataBuilder().withSeed(1).build();
        ClubSchedule clubSchedule = dbHelper.save(
                new ClubScheduleTestDataBuilder().withSeed(1).withDefaultTime().build());
        Club club = dbHelper.save(new ClubTestDataBuilder()
                .withSeed(1)
                .with(c -> c.setAddress(clubAddress))
                .with(c -> c.setSchedule(clubSchedule))
                .build());
        SuperAdmin principal = dbHelper.save(new UserTestDataBuilder<>(SuperAdmin.class)
                .withSeed(1)
                .with(u -> u.setClubs(Set.of(club)))
                .with(u -> u.setStartDate(LocalDate.now().minusWeeks(1)))
                .build());

        SubscriptionPlan subscriptionPlan = dbHelper.save(new SubscriptionPlanTestDataBuilder()
                .withSeed(1)
                .with(s -> s.setClub(club))
                .build());
        CustomerCreationDto userCreationDto = new UserDtoTestDataBuilder<>(CustomerCreationDto.class)
                .withSeed(2)
                .with(dto -> dto.setSubscriptionPlanId(subscriptionPlan.getId()))
                .build();

        MvcResult mvcResult = mockMvc.perform(post(URL.CREATE_USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreationDto)))
                .andExpect(status().isCreated())
                .andDo(print())
                .andReturn();

        UserDetailsBaseDto responseDto = parseResponseBody(mvcResult, CustomerDetailsDto.class);
        assertNotNull(responseDto);
        assertNotNull(dbHelper.getUserByUsername(userCreationDto.getUsername()));
    }

    @Test
    @WithMockUser(username = "username1", password = "Password1!", roles = "SUPERADMIN")
    public void testCreateUser_WhenSuperAdminCreatesAdminWhichAlreadyExists_ShouldThrowException() throws Exception {
        ClubAddress clubAddress = new ClubAddressTestDataBuilder().withSeed(1).build();
        ClubSchedule clubSchedule = dbHelper.save(
                new ClubScheduleTestDataBuilder().withSeed(1).withDefaultTime().build());
        Club club = dbHelper.save(new ClubTestDataBuilder()
                .withSeed(1)
                .with(c -> c.setAddress(clubAddress))
                .with(c -> c.setSchedule(clubSchedule))
                .build());
        SuperAdmin principal = dbHelper.save(new UserTestDataBuilder<>(SuperAdmin.class)
                .withSeed(1)
                .with(u -> u.setClubs(Set.of(club)))
                .with(u -> u.setStartDate(LocalDate.now().minusWeeks(1)))
                .build());
        SuperAdmin userAlreadyExists = dbHelper.save(new UserTestDataBuilder<>(SuperAdmin.class)
                .withSeed(2)
                .with(u -> u.setClubs(Set.of(club)))
                .with(u -> u.setStartDate(LocalDate.now().minusWeeks(1)))
                .build());

        AdminCreationDto userCreationDto = new UserDtoTestDataBuilder<>(AdminCreationDto.class)
                .withSeed(2)
                .with(dto -> dto.setClubsIds(Set.of(club.getId())))
                .build();

        mockMvc.perform(post(URL.CREATE_USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreationDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is(ExceptionMessage.USER_ALREADY_EXISTS.getName())))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "username1", password = "Password1!", roles = "SUPERADMIN")
    public void testCreateUser_WhenSuperAdminCreatesInstructorWhichAlreadyExists_ShouldThrowException()
            throws Exception {
        ClubAddress clubAddress = new ClubAddressTestDataBuilder().withSeed(1).build();
        ClubSchedule clubSchedule = dbHelper.save(
                new ClubScheduleTestDataBuilder().withSeed(1).withDefaultTime().build());
        Club club = dbHelper.save(new ClubTestDataBuilder()
                .withSeed(1)
                .with(c -> c.setAddress(clubAddress))
                .with(c -> c.setSchedule(clubSchedule))
                .build());
        SuperAdmin principal = dbHelper.save(new UserTestDataBuilder<>(SuperAdmin.class)
                .withSeed(1)
                .with(u -> u.setClubs(Set.of(club)))
                .with(u -> u.setStartDate(LocalDate.now().minusWeeks(1)))
                .build());

        Area area1 = dbHelper.save(new AreaTestDataBuilder().withSeed(1).build());
        Area area2 = dbHelper.save(new AreaTestDataBuilder().withSeed(2).build());
        Instructor userAlreadyExists = dbHelper.save(new UserTestDataBuilder<>(Instructor.class)
                .withSeed(2)
                .with(dto -> dto.setAreas(Set.of(area1, area2)))
                .with(u -> u.setClubs(Set.of(club)))
                .with(u -> u.setStartDate(LocalDate.now().minusWeeks(1)))
                .build());

        InstructorCreationDto userCreationDto = new UserDtoTestDataBuilder<>(InstructorCreationDto.class)
                .withSeed(2)
                .with(dto -> dto.setAreasIds(Set.of(area1.getId(), area2.getId())))
                .with(u -> u.setClubsIds(Set.of(club.getId())))
                .with(u -> u.setStartDate(LocalDate.now().minusWeeks(1)))
                .build();

        mockMvc.perform(post(URL.CREATE_USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreationDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is(ExceptionMessage.USER_ALREADY_EXISTS.getName())))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "username1", password = "Password1!", roles = "SUPERADMIN")
    public void testCreateUser_WhenSuperAdminCreatesCustomerWhichAlreadyExists_ShouldThrowException() throws Exception {
        ClubAddress clubAddress = new ClubAddressTestDataBuilder().withSeed(1).build();
        ClubSchedule clubSchedule = dbHelper.save(
                new ClubScheduleTestDataBuilder().withSeed(1).withDefaultTime().build());
        Club club = dbHelper.save(new ClubTestDataBuilder()
                .withSeed(1)
                .with(c -> c.setAddress(clubAddress))
                .with(c -> c.setSchedule(clubSchedule))
                .build());
        SuperAdmin principal = dbHelper.save(new UserTestDataBuilder<>(SuperAdmin.class)
                .withSeed(1)
                .with(u -> u.setClubs(Set.of(club)))
                .with(u -> u.setStartDate(LocalDate.now().minusWeeks(1)))
                .build());

        SubscriptionPlan subscriptionPlan = dbHelper.save(new SubscriptionPlanTestDataBuilder()
                .withSeed(1)
                .with(s -> s.setClub(club))
                .build());
        Customer userAlreadyExists = dbHelper.save(new UserTestDataBuilder<>(Customer.class)
                .withSeed(2)
                .with(u -> u.setSubscriptionPlan(subscriptionPlan))
                .build());

        CustomerCreationDto userCreationDto = new UserDtoTestDataBuilder<>(CustomerCreationDto.class)
                .withSeed(2)
                .with(dto -> dto.setSubscriptionPlanId(subscriptionPlan.getId()))
                .build();

        mockMvc.perform(post(URL.CREATE_USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreationDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is(ExceptionMessage.USER_ALREADY_EXISTS.getName())))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "username1", password = "Password1!", roles = "SUPERADMIN")
    public void testCreateUser_WhenSuperAdminIsInactive_ShouldThrowException() throws Exception {
        ClubAddress clubAddress = new ClubAddressTestDataBuilder().withSeed(1).build();
        ClubSchedule clubSchedule = dbHelper.save(
                new ClubScheduleTestDataBuilder().withSeed(1).withDefaultTime().build());
        Club club = dbHelper.save(new ClubTestDataBuilder()
                .withSeed(1)
                .with(c -> c.setAddress(clubAddress))
                .with(c -> c.setSchedule(clubSchedule))
                .build());
        SuperAdmin principal = dbHelper.save(new UserTestDataBuilder<>(SuperAdmin.class)
                .withSeed(1)
                .with(u -> u.setClubs(Set.of(club)))
                .with(u -> u.setStartDate(LocalDate.now().minusWeeks(1)))
                .with(u -> u.setActive(false))
                .build());

        SubscriptionPlan subscriptionPlan = dbHelper.save(new SubscriptionPlanTestDataBuilder()
                .withSeed(1)
                .with(s -> s.setClub(club))
                .build());
        CustomerCreationDto userCreationDto = new UserDtoTestDataBuilder<>(CustomerCreationDto.class)
                .withSeed(2)
                .with(dto -> dto.setSubscriptionPlanId(subscriptionPlan.getId()))
                .build();

        mockMvc.perform(post(URL.CREATE_USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreationDto)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", is(ExceptionMessage.USER_INACTIVE.getName())))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "username1", password = "Password1!", roles = "ADMIN")
    public void testCreateUser_WhenAdminCreatesInstructorRequest_ShouldCreateSuccessfully() throws Exception {
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
                .with(u -> u.setClubs(Set.of(club)))
                .with(u -> u.setStartDate(LocalDate.now().minusWeeks(1)))
                .build());

        Area area1 = dbHelper.save(new AreaTestDataBuilder().withSeed(1).build());
        Area area2 = dbHelper.save(new AreaTestDataBuilder().withSeed(2).build());
        InstructorCreationDto userCreationDto = new UserDtoTestDataBuilder<>(InstructorCreationDto.class)
                .withSeed(2)
                .with(dto -> dto.setAreasIds(Set.of(area1.getId(), area2.getId())))
                .with(u -> u.setClubsIds(Set.of(club.getId())))
                .with(u -> u.setStartDate(LocalDate.now().minusWeeks(1)))
                .build();

        MvcResult mvcResult = mockMvc.perform(post(URL.CREATE_USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreationDto)))
                .andExpect(status().isCreated())
                .andDo(print())
                .andReturn();

        UserDetailsBaseDto responseDto = parseResponseBody(mvcResult, InstructorDetailsDto.class);
        assertNotNull(responseDto);
        assertNotNull(dbHelper.getUserByUsername(userCreationDto.getUsername()));
    }

    @Test
    @WithMockUser(username = "username1", password = "Password1!", roles = "ADMIN")
    public void testCreateUser_WhenAdminCreatesCustomer_ShouldCreateSuccessfully() throws Exception {
        ClubAddress clubAddress = new ClubAddressTestDataBuilder().withSeed(1).build();
        ClubSchedule clubSchedule = dbHelper.save(
                new ClubScheduleTestDataBuilder().withSeed(1).withDefaultTime().build());
        Club club = dbHelper.save(new ClubTestDataBuilder()
                .withSeed(1)
                .with(c -> c.setAddress(clubAddress))
                .with(c -> c.setSchedule(clubSchedule))
                .build());
        Admin principal = dbHelper.save(new UserTestDataBuilder<>(Admin.class)
                .withSeed(1)
                .with(u -> u.setClubs(Set.of(club)))
                .with(u -> u.setStartDate(LocalDate.now().minusWeeks(1)))
                .build());

        SubscriptionPlan subscriptionPlan = dbHelper.save(new SubscriptionPlanTestDataBuilder()
                .withSeed(1)
                .with(s -> s.setClub(club))
                .build());
        CustomerCreationDto userCreationDto = new UserDtoTestDataBuilder<>(CustomerCreationDto.class)
                .withSeed(2)
                .with(dto -> dto.setSubscriptionPlanId(subscriptionPlan.getId()))
                .build();

        MvcResult mvcResult = mockMvc.perform(post(URL.CREATE_USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreationDto)))
                .andExpect(status().isCreated())
                .andDo(print())
                .andReturn();

        UserDetailsBaseDto responseDto = parseResponseBody(mvcResult, CustomerDetailsDto.class);
        assertNotNull(responseDto);
        assertNotNull(dbHelper.getUserByUsername(userCreationDto.getUsername()));
    }

    @Test
    @WithMockUser(username = "username1", password = "Password1!", roles = "ADMIN")
    public void testCreateUser_WhenAdminCreatesAdmin_ShouldThrowException() throws Exception {
        ClubAddress clubAddress = new ClubAddressTestDataBuilder().withSeed(1).build();
        ClubSchedule clubSchedule = dbHelper.save(
                new ClubScheduleTestDataBuilder().withSeed(1).withDefaultTime().build());
        Club club = dbHelper.save(new ClubTestDataBuilder()
                .withSeed(1)
                .with(c -> c.setAddress(clubAddress))
                .with(c -> c.setSchedule(clubSchedule))
                .build());
        Admin principal = dbHelper.save(new UserTestDataBuilder<>(Admin.class)
                .withSeed(1)
                .with(u -> u.setClubs(Set.of(club)))
                .with(u -> u.setStartDate(LocalDate.now().minusWeeks(1)))
                .build());

        Area area1 = dbHelper.save(new AreaTestDataBuilder().withSeed(1).build());
        Area area2 = dbHelper.save(new AreaTestDataBuilder().withSeed(2).build());
        Admin userAlreadyExists = dbHelper.save(new UserTestDataBuilder<>(Admin.class)
                .withSeed(2)
                .with(u -> u.setClubs(Set.of(club)))
                .with(u -> u.setStartDate(LocalDate.now().minusWeeks(1)))
                .build());

        AdminCreationDto userCreationDto = new UserDtoTestDataBuilder<>(AdminCreationDto.class)
                .withSeed(2)
                .with(dto -> dto.setClubsIds(Set.of(club.getId())))
                .build();

        mockMvc.perform(post(URL.CREATE_USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreationDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is(ExceptionMessage.ROLES_MISMATCH.getName())))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "username1", password = "Password1!", roles = "ADMIN")
    public void testCreateUser_WhenAdminCreatesInstructorWhichAlreadyExists_ShouldThrowException() throws Exception {
        ClubAddress clubAddress = new ClubAddressTestDataBuilder().withSeed(1).build();
        ClubSchedule clubSchedule = dbHelper.save(
                new ClubScheduleTestDataBuilder().withSeed(1).withDefaultTime().build());
        Club club = dbHelper.save(new ClubTestDataBuilder()
                .withSeed(1)
                .with(c -> c.setAddress(clubAddress))
                .with(c -> c.setSchedule(clubSchedule))
                .build());
        Admin principal = dbHelper.save(new UserTestDataBuilder<>(Admin.class)
                .withSeed(1)
                .with(u -> u.setClubs(Set.of(club)))
                .with(u -> u.setStartDate(LocalDate.now().minusWeeks(1)))
                .build());

        Area area1 = dbHelper.save(new AreaTestDataBuilder().withSeed(1).build());
        Area area2 = dbHelper.save(new AreaTestDataBuilder().withSeed(2).build());
        Instructor userAlreadyExists = dbHelper.save(new UserTestDataBuilder<>(Instructor.class)
                .withSeed(2)
                .with(u -> u.setAreas(Set.of(area1, area2)))
                .with(u -> u.setClubs(Set.of(club)))
                .with(u -> u.setStartDate(LocalDate.now().minusWeeks(1)))
                .build());

        InstructorCreationDto userCreationDto = new UserDtoTestDataBuilder<>(InstructorCreationDto.class)
                .withSeed(2)
                .with(dto -> dto.setClubsIds(Set.of(club.getId())))
                .with(dto -> dto.setAreasIds(Set.of(area1.getId(), area2.getId())))
                .build();

        mockMvc.perform(post(URL.CREATE_USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreationDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is(ExceptionMessage.USER_ALREADY_EXISTS.getName())))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "username1", password = "Password1!", roles = "ADMIN")
    public void testCreateUser_WhenAdminCreatesCustomerWhichAlreadyExists_ShouldThrowException() throws Exception {
        ClubAddress clubAddress = new ClubAddressTestDataBuilder().withSeed(1).build();
        ClubSchedule clubSchedule = dbHelper.save(
                new ClubScheduleTestDataBuilder().withSeed(1).withDefaultTime().build());
        Club club = dbHelper.save(new ClubTestDataBuilder()
                .withSeed(1)
                .with(c -> c.setAddress(clubAddress))
                .with(c -> c.setSchedule(clubSchedule))
                .build());
        Admin principal = dbHelper.save(new UserTestDataBuilder<>(Admin.class)
                .withSeed(1)
                .with(u -> u.setClubs(Set.of(club)))
                .with(u -> u.setStartDate(LocalDate.now().minusWeeks(1)))
                .build());

        Customer userAlreadyExists = dbHelper.save(
                new UserTestDataBuilder<>(Customer.class).withSeed(2).build());

        SubscriptionPlan subscriptionPlan = dbHelper.save(new SubscriptionPlanTestDataBuilder()
                .withSeed(1)
                .with(s -> s.setClub(club))
                .build());
        CustomerCreationDto userCreationDto = new UserDtoTestDataBuilder<>(CustomerCreationDto.class)
                .withSeed(2)
                .with(dto -> dto.setSubscriptionPlanId(subscriptionPlan.getId()))
                .build();

        mockMvc.perform(post(URL.CREATE_USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreationDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is(ExceptionMessage.USER_ALREADY_EXISTS.getName())))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "username1", password = "Password1!", roles = "ADMIN")
    public void testCreateUser_WhenAdminIsInactive_ShouldThrowException() throws Exception {
        ClubAddress clubAddress = new ClubAddressTestDataBuilder().withSeed(1).build();
        ClubSchedule clubSchedule = dbHelper.save(
                new ClubScheduleTestDataBuilder().withSeed(1).withDefaultTime().build());
        Club club = dbHelper.save(new ClubTestDataBuilder()
                .withSeed(1)
                .with(c -> c.setAddress(clubAddress))
                .with(c -> c.setSchedule(clubSchedule))
                .build());
        Admin principal = dbHelper.save(new UserTestDataBuilder<>(Admin.class)
                .withSeed(1)
                .with(u -> u.setClubs(Set.of(club)))
                .with(u -> u.setStartDate(LocalDate.now().minusWeeks(1)))
                .with(u -> u.setActive(false))
                .build());

        Area area1 = dbHelper.save(new AreaTestDataBuilder().withSeed(1).build());
        Area area2 = dbHelper.save(new AreaTestDataBuilder().withSeed(2).build());
        SubscriptionPlan subscriptionPlan = dbHelper.save(new SubscriptionPlanTestDataBuilder()
                .withSeed(1)
                .with(s -> s.setClub(club))
                .build());
        CustomerCreationDto userCreationDto = new UserDtoTestDataBuilder<>(CustomerCreationDto.class)
                .withSeed(2)
                .with(dto -> dto.setSubscriptionPlanId(subscriptionPlan.getId()))
                .build();

        mockMvc.perform(post(URL.CREATE_USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreationDto)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", is(ExceptionMessage.USER_INACTIVE.getName())))
                .andDo(print());
    }

    private <U extends UserDetailsBaseDto> UserDetailsBaseDto parseResponseBody(MvcResult mvcResult, Class<U> type)
            throws UnsupportedEncodingException, JsonProcessingException {
        JavaType javaType =
                switch (type.getSimpleName()) {
                    case "AdminDetailsDto" -> TypeFactory.defaultInstance().constructType(AdminDetailsDto.class);
                    case "InstructorDetailsDto" -> TypeFactory.defaultInstance()
                            .constructType(InstructorDetailsDto.class);
                    case "CustomerDetailsDto" -> TypeFactory.defaultInstance().constructType(CustomerDetailsDto.class);
                    default -> throw new IllegalStateException("Unexpected value: " + type.getSimpleName());
                };
        UserDetailsBaseDto userDetailsBaseDto =
                objectMapper.readValue(mvcResult.getResponse().getContentAsString(), javaType);
        return userDetailsBaseDto;
    }
}
