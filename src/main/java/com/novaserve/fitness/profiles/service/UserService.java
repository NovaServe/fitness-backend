/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.profiles.service;

import com.novaserve.fitness.profiles.dto.request.CustomerCreationDto;
import com.novaserve.fitness.profiles.dto.request.UserCreationBaseDto;
import com.novaserve.fitness.profiles.dto.response.CustomerDetailsDto;
import com.novaserve.fitness.profiles.dto.response.UserDetailsBaseDto;
import com.novaserve.fitness.profiles.model.Role;
import java.security.NoSuchAlgorithmException;
import java.util.Set;
import org.springframework.data.domain.Page;

public interface UserService {
    UserDetailsBaseDto createUser(UserCreationBaseDto userCreationDto) throws NoSuchAlgorithmException;

    CustomerDetailsDto signupCustomer(CustomerCreationDto userCreationDto) throws NoSuchAlgorithmException;

    UserDetailsBaseDto getUserDetails(long userId);

    Page<UserDetailsBaseDto> getUsers(
            Set<Role> roles, String fullName, String sortBy, String orderBy, int pageSize, int pageNumber);
}
