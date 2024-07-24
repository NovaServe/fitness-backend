/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.users.service;

import com.novaserve.fitness.users.dto.CreateUserReqDto;
import com.novaserve.fitness.users.dto.UserResDto;
import com.novaserve.fitness.users.model.User;

public interface UserService {
  User getUserById(long userId);

  UserResDto getUserDetailById(long userId);

  User createUser(CreateUserReqDto requestDto);
}
