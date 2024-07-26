/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.auth.service;

import com.novaserve.fitness.auth.dto.LoginProcessDto;
import com.novaserve.fitness.auth.dto.LoginRequestDto;
import com.novaserve.fitness.auth.dto.ValidateTokenResponseDto;

public interface AuthService {
    LoginProcessDto login(LoginRequestDto requestDto);

    ValidateTokenResponseDto validateToken();
}
