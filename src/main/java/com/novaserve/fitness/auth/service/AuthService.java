/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.auth.service;

import com.novaserve.fitness.auth.dto.LoginProcess;
import com.novaserve.fitness.auth.dto.LoginReqDto;
import com.novaserve.fitness.auth.dto.ValidateTokenResDto;

public interface AuthService {
  LoginProcess login(LoginReqDto reqDto);

  ValidateTokenResDto validateToken();
}
