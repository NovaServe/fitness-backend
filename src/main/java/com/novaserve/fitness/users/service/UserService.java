/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.users.service;

import com.novaserve.fitness.users.dto.request.CreateUserRequestDto;
import com.novaserve.fitness.users.dto.response.UserResponseDto;
import com.novaserve.fitness.users.model.User;
import com.novaserve.fitness.users.model.enums.Role;
import java.util.List;
import org.springframework.data.domain.Page;

public interface UserService {
    User getUserById(long userId);

    UserResponseDto getUserDetailById(long userId);

    User createUser(CreateUserRequestDto requestDto);

    UserResponseDto getUserDetails(long userId);

    Page<UserResponseDto> getUsers(
            List<Role> roles, String fullName, String sortBy, String order, int pageSize, int pageNumber);
}
