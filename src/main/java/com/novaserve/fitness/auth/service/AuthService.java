package com.novaserve.fitness.auth.service;

import com.novaserve.fitness.auth.dto.LoginProcessData;
import com.novaserve.fitness.auth.dto.LoginRequestDto;
import com.novaserve.fitness.auth.dto.ValidateTokenResponseDto;

public interface AuthService {
    LoginProcessData login(LoginRequestDto requestDto);

    ValidateTokenResponseDto validateToken();
}
