/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.auth.service;

import com.novaserve.fitness.auth.dto.request.LoginRequestDto;
import com.novaserve.fitness.auth.dto.request.ValidateTokenResponseDto;
import com.novaserve.fitness.auth.dto.response.LoginProcessDto;

public interface AuthService {
    LoginProcessDto login(LoginRequestDto requestDto);

    ValidateTokenResponseDto validateToken();
}
