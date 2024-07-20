package com.novaserve.fitness.users.auth.service;

import com.novaserve.fitness.users.auth.dto.LoginProcessData;
import com.novaserve.fitness.users.auth.dto.LoginRequestDto;
import com.novaserve.fitness.users.auth.dto.ValidateTokenResponseDto;

public interface AuthService {
    LoginProcessData login(LoginRequestDto requestDto);

    ValidateTokenResponseDto validateToken();
}
