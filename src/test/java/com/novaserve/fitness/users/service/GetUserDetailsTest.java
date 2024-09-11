/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.users.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.novaserve.fitness.auth.service.AuthUtil;
import com.novaserve.fitness.exception.ExceptionMessage;
import com.novaserve.fitness.exception.ServerException;
import com.novaserve.fitness.helpers.DtoHelper;
import com.novaserve.fitness.helpers.MockHelper;
import com.novaserve.fitness.users.dto.response.UserResponseDto;
import com.novaserve.fitness.users.model.User;
import com.novaserve.fitness.users.model.enums.Role;
import com.novaserve.fitness.users.repository.UserRepository;
import com.novaserve.fitness.users.service.impl.UserServiceImpl;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class GetUserDetailsTest {
    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private AuthUtil authUtil;

    @Mock
    private UserRepository userRepository;

    @Spy
    private ModelMapper modelMapper;

    @Spy
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Spy
    private MockHelper helper;

    @Spy
    private DtoHelper dtoHelper;

    @BeforeEach
    public void beforeEach() {
        lenient().when(userRepository.save(any(User.class))).then(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1000L);
            return user;
        });
    }

    @ParameterizedTest
    @MethodSource("methodParams_getUserDetails_shouldReturnUserDetails_whenSuperadminRequestsOwnOrAdminDetail")
    public void getUserDetails_shouldReturnUserDetails_whenSuperadminRequestsOwnOrAdminDetail(Role roleOfRequestedUser)
            throws Exception {
        User superadminPrincipal =
                helper.user().seed(1).role(Role.ROLE_SUPERADMIN).build();

        User requestedUser =
                switch (roleOfRequestedUser) {
                    case ROLE_SUPERADMIN -> superadminPrincipal;
                    case ROLE_ADMIN -> helper.user()
                            .seed(2)
                            .role(Role.ROLE_ADMIN)
                            .build();
                    default -> throw new IllegalArgumentException("Unexpected value: " + roleOfRequestedUser);
                };

        when(authUtil.getUserFromAuth(any())).thenReturn(Optional.of(superadminPrincipal));
        when(userRepository.findById(requestedUser.getId())).thenReturn(Optional.of(requestedUser));

        UserResponseDto userResponseDto = userService.getUserDetails(requestedUser.getId());

        verify(userRepository, times(1)).findById(requestedUser.getId());
        assertNotNull(userResponseDto);
    }

    public static Stream<Arguments>
            methodParams_getUserDetails_shouldReturnUserDetails_whenSuperadminRequestsOwnOrAdminDetail() {
        return Stream.of(Arguments.of(Role.ROLE_SUPERADMIN), Arguments.of(Role.ROLE_ADMIN));
    }

    @ParameterizedTest
    @MethodSource(
            "methodParams_getUserDetails_shouldReturnUserDetails_whenAdminRequestsOwnOrCustomerOrInstructorDetail")
    public void getUserDetails_shouldReturnUserDetails_whenAdminRequestsOwnOrCustomerOrInstructorDetail(
            Role roleOfRequestedUser) throws Exception {
        User adminPrincipal = helper.user().seed(1).role(Role.ROLE_ADMIN).build();

        User requestedUser =
                switch (roleOfRequestedUser) {
                    case ROLE_ADMIN -> adminPrincipal;
                    case ROLE_CUSTOMER -> helper.user()
                            .seed(2)
                            .role(Role.ROLE_CUSTOMER)
                            .build();
                    case ROLE_INSTRUCTOR -> helper.user()
                            .seed(2)
                            .role(Role.ROLE_INSTRUCTOR)
                            .build();
                    default -> throw new IllegalArgumentException("Unexpected value: " + roleOfRequestedUser);
                };

        when(authUtil.getUserFromAuth(any())).thenReturn(Optional.of(adminPrincipal));
        when(userRepository.findById(requestedUser.getId())).thenReturn(Optional.of(requestedUser));

        UserResponseDto userResponseDto = userService.getUserDetails(requestedUser.getId());

        verify(userRepository, times(1)).findById(requestedUser.getId());
        assertNotNull(userResponseDto);
    }

    public static Stream<Arguments>
            methodParams_getUserDetails_shouldReturnUserDetails_whenAdminRequestsOwnOrCustomerOrInstructorDetail() {
        return Stream.of(
                Arguments.of(Role.ROLE_ADMIN), Arguments.of(Role.ROLE_CUSTOMER), Arguments.of(Role.ROLE_INSTRUCTOR));
    }

    @Test
    public void getUserDetails_shouldReturnUserDetails_whenCustomerRequestsOwnDetail() throws Exception {
        User customerPrincipal = helper.user().role(Role.ROLE_CUSTOMER).build();

        when(authUtil.getUserFromAuth(any())).thenReturn(Optional.of(customerPrincipal));
        when(userRepository.findById(customerPrincipal.getId())).thenReturn(Optional.of(customerPrincipal));

        UserResponseDto userResponseDto = userService.getUserDetails(customerPrincipal.getId());

        verify(userRepository, times(1)).findById(customerPrincipal.getId());
        assertNotNull(userResponseDto);
    }

    @Test
    public void getUserDetails_shouldReturnUserDetails_whenInstructorRequestsOwnDetail() throws Exception {
        User instructorPrincipal = helper.user().role(Role.ROLE_INSTRUCTOR).build();

        when(authUtil.getUserFromAuth(any())).thenReturn(Optional.of(instructorPrincipal));
        when(userRepository.findById(instructorPrincipal.getId())).thenReturn(Optional.of(instructorPrincipal));

        UserResponseDto userResponseDto = userService.getUserDetails(instructorPrincipal.getId());

        verify(userRepository, times(1)).findById(instructorPrincipal.getId());
        assertNotNull(userResponseDto);
    }

    @ParameterizedTest
    @MethodSource("methodParams_getUserDetails_shouldThrowException_whenRolesMismatch")
    public void getUserDetails_shouldThrowException_whenRolesMismatch(Role principalRole, Role roleOfRequestedUser)
            throws Exception {
        User principal = helper.user().seed(1).role(principalRole).build();
        User requestedUser = helper.user().role(roleOfRequestedUser).build();

        when(authUtil.getUserFromAuth(any())).thenReturn(Optional.of(principal));
        when(userRepository.findById(requestedUser.getId())).thenReturn(Optional.of(requestedUser));

        ServerException actual =
                assertThrows(ServerException.class, () -> userService.getUserDetails(requestedUser.getId()));

        assertEquals(actual.getMessage(), ExceptionMessage.ROLES_MISMATCH.getName());
        assertEquals(actual.getStatus(), HttpStatus.BAD_REQUEST);
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
}
