/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.profiles.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

import com.novaserve.fitness.auth.service.AuthUtil;
import com.novaserve.fitness.profiles.dto.request.AdminCreationDto;
import com.novaserve.fitness.profiles.dto.request.CustomerCreationDto;
import com.novaserve.fitness.profiles.dto.response.AdminDetailsDto;
import com.novaserve.fitness.profiles.dto.response.CustomerDetailsDto;
import com.novaserve.fitness.profiles.dto.response.UserDetailsBaseDto;
import com.novaserve.fitness.profiles.model.Admin;
import com.novaserve.fitness.profiles.model.Customer;
import com.novaserve.fitness.profiles.model.Role;
import com.novaserve.fitness.profiles.model.UserBase;
import com.novaserve.fitness.profiles.repository.UserRepository;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
public class CreateUserTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private AuthUtil authUtil;

    @Mock
    private ProfileUtil profileUtil;

    @Mock
    private UserCreationFactory userCreationFactory;

    @Mock
    private UserCreationStrategy userCreationStrategy;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void testCreateUser_ShouldCreateUserSuccessfully() throws NoSuchAlgorithmException {
        // Given
        AdminCreationDto userCreationDto = mock(AdminCreationDto.class);
        UserBase principal = mock(Admin.class);
        UserBase savedUser = mock(Admin.class);
        AdminDetailsDto userDto = mock(AdminDetailsDto.class);

        when(authUtil.getUserFromAuth(any())).thenReturn(Optional.of(principal));
        when(userCreationFactory.createStrategyInstance(principal.getRole())).thenReturn(userCreationStrategy);
        when(userCreationStrategy.createUser(userCreationDto)).thenReturn(savedUser);
        when(modelMapper.map(savedUser, AdminDetailsDto.class)).thenReturn(userDto);

        // When
        UserDetailsBaseDto result = userService.createUser(userCreationDto);

        // Then
        assertNotNull(result);
        verify(userCreationFactory, times(1)).createStrategyInstance(principal.getRole());
        verify(userCreationStrategy, times(1)).createUser(userCreationDto);
        verify(modelMapper, times(1)).map(savedUser, AdminDetailsDto.class);
    }

    @Test
    void testSignupCustomer_ShouldCreateCustomerSuccessfully() throws NoSuchAlgorithmException {
        // Given
        CustomerCreationDto userCreationDto = mock(CustomerCreationDto.class);
        UserBase savedUser = mock(Customer.class);
        CustomerDetailsDto customerDetailsDto = mock(CustomerDetailsDto.class);

        when(userCreationFactory.createStrategyInstance(Role.ROLE_CUSTOMER)).thenReturn(userCreationStrategy);
        when(userCreationStrategy.createUser(userCreationDto)).thenReturn(savedUser);
        when(modelMapper.map(savedUser, CustomerDetailsDto.class)).thenReturn(customerDetailsDto);
        when(savedUser.getId()).thenReturn(1L);

        // When
        CustomerDetailsDto result = userService.signupCustomer(userCreationDto);

        // Then
        assertNotNull(result);
        assertEquals(customerDetailsDto, result);
        verify(userCreationFactory, times(1)).createStrategyInstance(Role.ROLE_CUSTOMER);
        verify(userCreationStrategy, times(1)).createUser(userCreationDto);
        verify(modelMapper, times(1)).map(savedUser, CustomerDetailsDto.class);
        verify(savedUser, times(1)).getId();
    }
}
