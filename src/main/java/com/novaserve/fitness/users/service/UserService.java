/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.users.service;

import com.novaserve.fitness.users.dto.CreateUserRequestDto;
import com.novaserve.fitness.users.dto.UserResponseDto;
import com.novaserve.fitness.users.model.User;

public interface UserService {
    User getUserById(long userId);

    UserResponseDto getUserDetailById(long userId);

    User createUser(CreateUserRequestDto requestDto);
}
