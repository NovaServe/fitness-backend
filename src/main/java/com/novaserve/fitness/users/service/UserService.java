/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.users.service;

import com.novaserve.fitness.users.dto.CreateUserRequestDto;
import com.novaserve.fitness.users.dto.UserResponseDto;
import com.novaserve.fitness.users.model.User;
import java.util.List;
import org.springframework.data.domain.Page;

public interface UserService {
    User getUserById(long userId);

    UserResponseDto getUserDetailById(long userId);

    User createUser(CreateUserRequestDto requestDto);

    Page<UserResponseDto> getUsers(
            List<String> roles, String fullName, String sortBy, String order, int pageSize, int pageNumber);
}
