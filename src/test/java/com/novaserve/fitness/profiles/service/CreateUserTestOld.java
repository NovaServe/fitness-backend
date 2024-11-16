/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.profiles.service;
//
// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.*;
//
// import com.novaserve.fitness.auth.service.AuthUtil;
// import com.novaserve.fitness.payments.service.PaymentUtil;
// import com.novaserve.fitness.profiles.dto.response.UserDetailsBaseDto;
// import com.novaserve.fitness.profiles.model.Club;
// import com.novaserve.fitness.profiles.model.ClubAddress;
// import com.novaserve.fitness.profiles.model.ClubSchedule;
// import com.novaserve.fitness.profiles.repository.ClubRepository;
// import com.novaserve.fitness.exceptions.ExceptionMessage;
// import com.novaserve.fitness.exceptions.ServerException;
// import com.novaserve.fitness.helpers.builders.users.ClubAddressTestDataBuilder;
// import com.novaserve.fitness.helpers.builders.users.ClubScheduleTestDataBuilder;
// import com.novaserve.fitness.helpers.builders.users.ClubTestDataBuilder;
// import com.novaserve.fitness.helpers.builders.payment.SubscriptionPlanTestDataBuilder;
// import com.novaserve.fitness.helpers.builders.users.AreaTestDataBuilder;
// import com.novaserve.fitness.helpers.builders.users.UserDtoTestDataBuilder;
// import com.novaserve.fitness.helpers.builders.users.UserTestDataBuilder;
// import com.novaserve.fitness.payments.model.SubscriptionPlan;
// import com.novaserve.fitness.payments.repository.SubscriptionPlanRepository;
// import com.novaserve.fitness.profiles.dto.request.*;
// import com.novaserve.fitness.profiles.model.*;
// import com.novaserve.fitness.profiles.repository.UserRepository;
//
// import java.time.LocalDate;
// import java.util.Optional;
// import java.util.Set;
// import java.util.stream.Stream;
//
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.junit.jupiter.params.ParameterizedTest;
// import org.junit.jupiter.params.provider.Arguments;
// import org.junit.jupiter.params.provider.MethodSource;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.MockitoAnnotations;
// import org.mockito.Spy;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.modelmapper.ModelMapper;
// import org.springframework.http.HttpStatus;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;
//
// @ExtendWith(MockitoExtension.class)
// class CreateUserTestOld {
//    @InjectMocks
//    private UserServiceImpl userService;
//
//    @Mock
//    private AuthUtil authUtil;
//
//    @Mock
//    private ProfileUtil profileUtil;
//
//    @Mock
//    PaymentUtil paymentUtil;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private ClubRepository clubRepository;
//
//    @Mock
//    SubscriptionPlanRepository subscriptionPlanRepository;
//
//    @Spy
//    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//
//    @Spy
//    private ModelMapper modelMapper;
//
//    private AdminCreationStrategyImpl adminCreationStrategy;
//
//    private InstructorCreationStrategyImpl instructorCreationStrategy;
//
//    private CustomerCreationStrategyImpl customerCreationStrategy;
//
//    private UserCreationFactory userCreationFactory;
//
//
//    @BeforeEach
//    public void beforeEach() {
//        MockitoAnnotations.openMocks(this);
//        adminCreationStrategy = spy(new AdminCreationStrategyImpl(profileUtil, userRepository, passwordEncoder));
//        instructorCreationStrategy = spy(new InstructorCreationStrategyImpl(profileUtil, userRepository,
// passwordEncoder));
//        customerCreationStrategy = spy(new CustomerCreationStrategyImpl(paymentUtil, userRepository,
// passwordEncoder));
//        userCreationFactory = spy(new UserCreationFactory(adminCreationStrategy, instructorCreationStrategy,
// customerCreationStrategy));
//        userService = new UserServiceImpl(userRepository, modelMapper, authUtil, profileUtil, userCreationFactory);
//    }
//
//    @Test
//    public void testCreateUser_WhenSuperAdminRequest_ShouldCreateAdmin() {
//        ClubAddress clubAddress = new ClubAddressTestDataBuilder()
//                .withSeed(1)
//                .build();
//        ClubSchedule clubSchedule = new ClubScheduleTestDataBuilder()
//                .withSeed(1)
//                .withDefaultTime()
//                .build();
//        Club club = new ClubTestDataBuilder()
//                .withSeed(1)
//                .with(c -> c.setId(1L))
//                .with(c -> c.setAddress(clubAddress))
//                .with(c -> c.setSchedule(clubSchedule))
//                .build();
//        Admin principal = new UserTestDataBuilder<>(Admin.class)
//                .withSeed(1)
//                .with(u -> u.setRole(Role.ROLE_SUPERADMIN))
//                .with(u -> u.setClubs(Set.of(club)))
//                .with(u -> u.setStartDate(LocalDate.now().minusWeeks(1)))
//                .build();
//
//        AdminCreationDto userCreationDto = new UserDtoTestDataBuilder<>(AdminCreationDto.class)
//                .withSeed(2)
//                .with(dto -> dto.setRole(Role.ROLE_ADMIN))
//                .with(dto -> dto.setClubsIds(Set.of(club.getId())))
//                .build();
//
//        when(authUtil.getUserFromAuth(any())).thenReturn(Optional.ofNullable(principal));
//        when(userRepository.findByUsernameOrEmailOrPhone(any(), any(), any())).thenReturn(Optional.empty());
//        when(clubRepository.findById(any())).thenReturn(Optional.of(club));
//        when(userRepository.save(any())).then(invocation -> invocation.getArguments()[0]);
//
//        UserDetailsBaseDto actual = userService.createUser(userCreationDto);
//        assertNotNull(actual);
//        assertEquals(Admin.class, actual.getClass());
//        verify(authUtil).getUserFromAuth(any());
//        verify(userCreationFactory, times(1)).createStrategyInstance(principal.getRole());
//        verify(adminCreationStrategy, times(1)).createUser(userCreationDto);
//        verify(userRepository, times(1)).findByUsernameOrEmailOrPhone(any(), any(), any());
//        verify(clubRepository, times(1)).findById(any());
//        verify(userRepository, times(1)).save(any());
//    }
//
//    @Test
//    public void testCreateUser_WhenAdminRequest_ShouldCreateCustomer() {
//        ClubAddress clubAddress = new ClubAddressTestDataBuilder()
//                .withSeed(1)
//                .build();
//        ClubSchedule clubSchedule = new ClubScheduleTestDataBuilder()
//                .withSeed(1)
//                .withDefaultTime()
//                .build();
//        Club club = new ClubTestDataBuilder()
//                .withSeed(1)
//                .with(c -> c.setId(1L))
//                .with(c -> c.setAddress(clubAddress))
//                .with(c -> c.setSchedule(clubSchedule))
//                .build();
//        Admin principal = new UserTestDataBuilder<>(Admin.class)
//                .withSeed(1)
//                .with(u -> u.setRole(Role.ROLE_ADMIN))
//                .with(u -> u.setClubs(Set.of(club)))
//                .with(u -> u.setStartDate(LocalDate.now().minusWeeks(1)))
//                .build();
//
//        SubscriptionPlan subscriptionPlan = new SubscriptionPlanTestDataBuilder()
//                .withSeed(1)
//                .with(s -> s.setClub(club))
//                .build();
//        UserCreationBaseDto userCreationDto = new UserDtoTestDataBuilder<>(CustomerCreationDto.class)
//                .withSeed(2)
//                .with(dto -> dto.setRole(Role.ROLE_CUSTOMER))
//                .with(dto -> dto.setSubscriptionPlanId(subscriptionPlan.getId()))
//                .build();
//
//        when(authUtil.getUserFromAuth(any())).thenReturn(Optional.ofNullable(principal));
//        when(userRepository.findByUsernameOrEmailOrPhone(any(), any(), any())).thenReturn(Optional.empty());
//        when(subscriptionPlanRepository.findById(any())).thenReturn(Optional.of(subscriptionPlan));
//        when(userRepository.save(any())).then(invocation -> invocation.getArguments()[0]);
//
//        UserBase actual = userService.createUser(userCreationDto);
//        assertNotNull(actual);
//        assertEquals(Customer.class, actual.getClass());
//        verify(authUtil).getUserFromAuth(any());
//        verify(userCreationFactory).createStrategyInstance(principal.getRole());
//        verify(adminCreationStrategy).createUser(userCreationDto);
//        verify(userRepository).findByUsernameOrEmailOrPhone(any(), any(), any());
//        verify(subscriptionPlanRepository).findById(any());
//        verify(userRepository).save(any());
//    }
//
//    @Test
//    public void testCreateUser_WhenAdminRequest_ShouldCreateInstructor() {
//        ClubAddress clubAddress = new ClubAddressTestDataBuilder()
//                .withSeed(1)
//                .build();
//        ClubSchedule clubSchedule = new ClubScheduleTestDataBuilder()
//                .withSeed(1)
//                .withDefaultTime()
//                .build();
//        Club club = new ClubTestDataBuilder()
//                .withSeed(1)
//                .with(c -> c.setId(1L))
//                .with(c -> c.setAddress(clubAddress))
//                .with(c -> c.setSchedule(clubSchedule))
//                .build();
//        Admin principal = new UserTestDataBuilder<>(Admin.class)
//                .withSeed(1)
//                .with(u -> u.setRole(Role.ROLE_ADMIN))
//                .with(u -> u.setClubs(Set.of(club)))
//                .with(u -> u.setStartDate(LocalDate.now().minusWeeks(1)))
//                .build();
//
//        Area area1 = new AreaTestDataBuilder()
//                .withSeed(1)
//                .with(a -> a.setId(1L))
//                .build();
//        Area area2 = new AreaTestDataBuilder()
//                .withSeed(2)
//                .with(a -> a.setId(2L))
//                .build();
//        UserCreationBaseDto userCreationDto = new UserDtoTestDataBuilder<>(InstructorCreationDto.class)
//                .withSeed(2)
//                .with(dto -> dto.setRole(Role.ROLE_INSTRUCTOR))
//                .with(dto -> dto.setClubsIds(Set.of(club.getId())))
//                .with(dto -> dto.setAreasIds(Set.of(area1.getId(), area2.getId())))
//                .with(dto -> dto.setStartDate(LocalDate.now().minusWeeks(1)))
//                .build();
//
//        when(authUtil.getUserFromAuth(any())).thenReturn(Optional.ofNullable(principal));
//        when(userRepository.findByUsernameOrEmailOrPhone(any(), any(), any())).thenReturn(Optional.empty());
//        when(clubRepository.findById(any())).thenReturn(Optional.of(club));
//        when(userRepository.save(any())).then(invocation -> invocation.getArguments()[0]);
//
//        UserBase actual = userService.createUser(userCreationDto);
//        assertNotNull(actual);
//        assertEquals(Instructor.class, actual.getClass());
//        verify(authUtil).getUserFromAuth(any());
//        verify(userCreationFactory).createStrategyInstance(principal.getRole());
//        verify(adminCreationStrategy).createUser(userCreationDto);
//        verify(userRepository).findByUsernameOrEmailOrPhone(any(), any(), any());
//        verify(clubRepository).findById(any());
//        verify(userRepository).save(any());
//    }
//
//    @Test
//    public void testCreateUser_WhenSuperAdminRequestAndUserAlreadyExists_ShouldThrowException() {
//        ClubAddress clubAddress = new ClubAddressTestDataBuilder()
//                .withSeed(1)
//                .build();
//        ClubSchedule clubSchedule = new ClubScheduleTestDataBuilder()
//                .withSeed(1)
//                .withDefaultTime()
//                .build();
//        Club club = new ClubTestDataBuilder()
//                .withSeed(1)
//                .with(c -> c.setId(1L))
//                .with(c -> c.setAddress(clubAddress))
//                .with(c -> c.setSchedule(clubSchedule))
//                .build();
//        Admin principal = new UserTestDataBuilder<>(Admin.class)
//                .withSeed(1)
//                .with(u -> u.setRole(Role.ROLE_SUPERADMIN))
//                .with(u -> u.setClubs(Set.of(club)))
//                .with(u -> u.setStartDate(LocalDate.now().minusWeeks(1)))
//                .build();
//
//        UserBase userAlreadyExists = new UserTestDataBuilder<>(Admin.class)
//                .withSeed(2)
//                .build();
//        UserCreationBaseDto userCreationDto = new UserDtoTestDataBuilder<>(AdminCreationDto.class)
//                .withSeed(2)
//                .build();
//
//        when(authUtil.getUserFromAuth(any())).thenReturn(Optional.ofNullable(principal));
//        when(userRepository.findByUsernameOrEmailOrPhone(any(), any(), any()))
//                .thenReturn(Optional.ofNullable(userAlreadyExists));
//
//        ServerException actual =
//                assertThrows(ServerException.class, () -> userService.createUser(userCreationDto));
//        ServerException expected =
//                new ServerException(ExceptionMessage.ALREADY_EXISTS, HttpStatus.BAD_REQUEST);
//        assertEquals(expected.getMessage(), actual.getMessage());
//        assertEquals(expected.getStatus(), actual.getStatus());
//        verify(authUtil).getUserFromAuth(any());
//        verify(userCreationFactory).createStrategyInstance(principal.getRole());
//        verify(superAdminUserCreationStrategy).createUser(userCreationDto);
//        verify(userRepository).findByUsernameOrEmailOrPhone(any(), any(), any());
//    }
//
//    @ParameterizedTest
//    @MethodSource("methodParams_testCreateUser_WhenAdminRequestAndUserAlreadyExists_ShouldThrowException")
//    public void testCreateUser_WhenAdminRequestAndUserAlreadyExists_ShouldThrowException(Role userRole) {
//        ClubAddress clubAddress = new ClubAddressTestDataBuilder()
//                .withSeed(1)
//                .build();
//        ClubSchedule clubSchedule = new ClubScheduleTestDataBuilder()
//                .withSeed(1)
//                .withDefaultTime()
//                .build();
//        Club club = new ClubTestDataBuilder()
//                .withSeed(1)
//                .with(c -> c.setId(1L))
//                .with(c -> c.setAddress(clubAddress))
//                .with(c -> c.setSchedule(clubSchedule))
//                .build();
//        Admin principal = new UserTestDataBuilder<>(Admin.class)
//                .withSeed(1)
//                .with(u -> u.setRole(Role.ROLE_ADMIN))
//                .with(u -> u.setClubs(Set.of(club)))
//                .with(u -> u.setStartDate(LocalDate.now().minusWeeks(1)))
//                .build();
//
//        UserBase userAlreadyExists;
//        UserCreationBaseDto userCreationDto;
//
//        switch (userRole) {
//            case ROLE_CUSTOMER -> {
//                userAlreadyExists = new UserTestDataBuilder<>(Customer.class)
//                        .withSeed(2)
//                        .build();
//                userCreationDto = new UserDtoTestDataBuilder<>(CustomerCreationDto.class)
//                        .withSeed(2)
//                        .build();
//            }
//            case ROLE_INSTRUCTOR -> {
//                userAlreadyExists = new UserTestDataBuilder<>(Instructor.class)
//                        .withSeed(2)
//                        .build();
//                userCreationDto = new UserDtoTestDataBuilder<>(InstructorCreationDto.class)
//                        .withSeed(2)
//                        .build();
//            }
//            default -> throw new IllegalArgumentException();
//        }
//
//        when(authUtil.getUserFromAuth(any())).thenReturn(Optional.ofNullable(principal));
//        when(userRepository.findByUsernameOrEmailOrPhone(any(), any(), any()))
//                .thenReturn(Optional.ofNullable(userAlreadyExists));
//
//        ServerException actual =
//                assertThrows(ServerException.class, () -> userService.createUser(userCreationDto));
//        ServerException expected =
//                new ServerException(ExceptionMessage.ALREADY_EXISTS, HttpStatus.BAD_REQUEST);
//        assertEquals(expected.getMessage(), actual.getMessage());
//        assertEquals(expected.getStatus(), actual.getStatus());
//        verify(authUtil).getUserFromAuth(any());
//        verify(userCreationFactory).createStrategyInstance(principal.getRole());
//        verify(adminCreationStrategy).createUser(userCreationDto);
//        verify(userRepository).findByUsernameOrEmailOrPhone(any(), any(), any());
//    }
//
//    public static Stream<Arguments>
//    methodParams_testCreateUser_WhenAdminRequestAndUserAlreadyExists_ShouldThrowException() {
//        return Stream.of(Arguments.of(Role.ROLE_CUSTOMER), Arguments.of(Role.ROLE_INSTRUCTOR));
//    }
// }
