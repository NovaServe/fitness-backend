/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.profiles.service;
//
// import com.novaserve.fitness.auth.service.AuthUtil;
// import com.novaserve.fitness.profiles.model.Club;
// import com.novaserve.fitness.profiles.model.ClubAddress;
// import com.novaserve.fitness.profiles.model.ClubSchedule;
// import com.novaserve.fitness.exception.ExceptionMessage;
// import com.novaserve.fitness.exception.ServerException;
// import com.novaserve.fitness.helpers.builders.users.ClubAddressTestDataBuilder;
// import com.novaserve.fitness.helpers.builders.users.ClubScheduleTestDataBuilder;
// import com.novaserve.fitness.helpers.builders.users.ClubTestDataBuilder;
// import com.novaserve.fitness.helpers.builders.payment.SubscriptionPlanTestDataBuilder;
// import com.novaserve.fitness.helpers.builders.users.AreaTestDataBuilder;
// import com.novaserve.fitness.helpers.builders.users.UserDtoTestDataBuilder;
// import com.novaserve.fitness.helpers.builders.users.UserTestDataBuilder;
// import com.novaserve.fitness.payment.model.SubscriptionPlan;
// import com.novaserve.fitness.users.dto.request.AdminCreationDto;
// import com.novaserve.fitness.users.dto.request.UserCreationBaseDto;
// import com.novaserve.fitness.users.model.*;
// import com.novaserve.fitness.users.repository.UserRepository;
// import org.aspectj.lang.ProceedingJoinPoint;
// import org.aspectj.lang.Signature;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.junit.jupiter.params.ParameterizedTest;
// import org.junit.jupiter.params.provider.Arguments;
// import org.junit.jupiter.params.provider.MethodSource;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.Spy;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.http.HttpStatus;
// import java.lang.reflect.Method;
// import java.time.LocalDate;
// import java.util.Optional;
// import java.util.Set;
// import java.util.stream.Stream;
//
// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.*;
//
// @ExtendWith(MockitoExtension.class)
// class RolesConfigAspectTest {
//    @InjectMocks
//    private RolesConfigAspect rolesConfigAspect;
//
//    @Mock
//    private AuthUtil authUtil;
//
////    @Mock
//    private RolesConfigFactory rolesConfigFactory;
//
////    @Mock
//    @Spy
//    private CreateUserConfigStrategy createUserConfigStrategy;
////
////    @Mock
//    @Spy
//    private GetUsersConfigStrategy getUsersConfigStrategy;
////
////    @Mock
////    @Spy
//    private GetUserDetailsConfigStrategy getUserDetailsConfigStrategy;
//
//    @Mock
//    UserRepository userRepository;
//
//    @Mock
//    ProceedingJoinPoint joinPoint;
//
//    @BeforeEach
//    public void beforeEach() {
//        GetUserDetailsConfigStrategy getUserDetailsConfigStrategyInst =
//                new GetUserDetailsConfigStrategy(userRepository);
//        this.getUserDetailsConfigStrategy = spy(getUserDetailsConfigStrategyInst);
//        RolesConfigFactory rolesConfigFactoryInst = new RolesConfigFactory(
//                createUserConfigStrategy, getUsersConfigStrategy, getUserDetailsConfigStrategy);
//        this.rolesConfigFactory = spy(rolesConfigFactoryInst);
////        MockitoAnnotations.openMocks(this);
////        rolesConfigFactory = new RolesConfigFactory(
////                createUserConfigStrategy, getUsersConfigStrategy, getUserDetailsConfigStrategy
////        );
//        rolesConfigAspect = new RolesConfigAspect(authUtil, rolesConfigFactory);
//    }
//
//    @ParameterizedTest
//    @MethodSource("methodParams_testCheckRoles_WhenCreateUserAndRolesMismatch_ShouldThrowException")
//    void testCheckRoles_WhenCreateUserAndRolesMismatch_ShouldThrowException(Role principalRole, Role
// requestedUserRole) throws Throwable {
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
//
//        UserBase principal = switch (principalRole) {
//            case ROLE_SUPERADMIN, ROLE_ADMIN -> new UserTestDataBuilder<>(Admin.class)
//                    .withSeed(1)
//                    .with(u -> u.setRole(principalRole))
//                    .with(u -> u.setClubs(Set.of(club)))
//                    .with(u -> u.setStartDate(LocalDate.now().minusWeeks(1)))
//                    .build();
//            case ROLE_INSTRUCTOR -> {
//                Area area1 = new AreaTestDataBuilder()
//                        .withSeed(1)
//                        .build();
//                Area area2 = new AreaTestDataBuilder()
//                        .withSeed(2)
//                        .build();
//                yield new UserTestDataBuilder<>(Instructor.class)
//                        .withSeed(1)
//                        .with(u -> u.setRole(principalRole))
//                        .with(u -> u.setAreas(Set.of(area1, area2)))
//                        .with(u -> u.setClubs(Set.of(club)))
//                        .with(u -> u.setStartDate(LocalDate.now().minusWeeks(1)))
//                        .build();
//            }
//            case ROLE_CUSTOMER -> {
//                SubscriptionPlan subscriptionPlan = new SubscriptionPlanTestDataBuilder()
//                        .withSeed(1)
//                        .build();
//                yield new UserTestDataBuilder<>(Customer.class)
//                        .withSeed(2)
//                        .with(dto -> dto.setRole(Role.ROLE_CUSTOMER))
//                        .with(dto -> dto.setSubscriptionPlan(subscriptionPlan))
//                        .build();
//            }
//        };
//
//        when(authUtil.getUserFromAuth(any())).thenReturn(Optional.of(principal));
//        Signature signature = mock(Signature.class);
//        when(joinPoint.getSignature()).thenReturn(signature);
//        when(signature.getName()).thenReturn("createUser");
//        AdminCreationDto creationDto = new UserDtoTestDataBuilder<>(AdminCreationDto.class).build();
//        creationDto.setRole(Role.ROLE_ADMIN);
//        when(joinPoint.getArgs())
//                .thenReturn(new UserCreationBaseDto[] {creationDto});
//
//        Method method = UserServiceImpl.class.getMethod("createUser", UserCreationBaseDto.class);
//        RolesConfig rolesConfig = method.getAnnotation(RolesConfig.class);
//
//        ServerException actual =
//                 assertThrows(ServerException.class, () -> rolesConfigAspect.checkRoles(joinPoint, rolesConfig));
//
//         ServerException expected = new ServerException(ExceptionMessage.ROLES_MISMATCH, HttpStatus.BAD_REQUEST);;
//         assertEquals(expected.getMessage(), actual.getMessage());
//         assertEquals(expected.getStatus(), actual.getStatus());
//
//    }
//
//        public static Stream<Arguments>
// methodParams_testCheckRoles_WhenCreateUserAndRolesMismatch_ShouldThrowException() {
//            return Stream.of(
//                    Arguments.of(Role.ROLE_SUPERADMIN, Role.ROLE_SUPERADMIN),
//                    Arguments.of(Role.ROLE_SUPERADMIN, Role.ROLE_CUSTOMER),
//                    Arguments.of(Role.ROLE_SUPERADMIN, Role.ROLE_INSTRUCTOR),
//                    Arguments.of(Role.ROLE_ADMIN, Role.ROLE_ADMIN),
//                    Arguments.of(Role.ROLE_ADMIN, Role.ROLE_SUPERADMIN),
//                    Arguments.of(Role.ROLE_CUSTOMER, Role.ROLE_CUSTOMER),
//                    Arguments.of(Role.ROLE_CUSTOMER, Role.ROLE_SUPERADMIN),
//                    Arguments.of(Role.ROLE_CUSTOMER, Role.ROLE_ADMIN),
//                    Arguments.of(Role.ROLE_CUSTOMER, Role.ROLE_INSTRUCTOR),
//                    Arguments.of(Role.ROLE_INSTRUCTOR, Role.ROLE_INSTRUCTOR),
//                    Arguments.of(Role.ROLE_INSTRUCTOR, Role.ROLE_INSTRUCTOR),
//                    Arguments.of(Role.ROLE_INSTRUCTOR, Role.ROLE_SUPERADMIN),
//                    Arguments.of(Role.ROLE_INSTRUCTOR, Role.ROLE_ADMIN));
//        }
//
//    }
