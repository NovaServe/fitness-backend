/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.auth.service;

import static java.util.Objects.isNull;

import com.novaserve.fitness.auth.dto.LoginProcessDto;
import com.novaserve.fitness.auth.dto.LoginRequestDto;
import com.novaserve.fitness.auth.dto.ValidateTokenResponseDto;
import com.novaserve.fitness.exception.ExceptionMessage;
import com.novaserve.fitness.exception.ServerException;
import com.novaserve.fitness.security.auth.JwtTokenProvider;
import com.novaserve.fitness.users.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    AuthUtil authUtil;

    @Autowired
    UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Override
    @Transactional
    public LoginProcessDto login(LoginRequestDto dto) {
        try {
            var auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getUsernameOrEmailOrPhone(), dto.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(auth);
            var user = authUtil.getPrincipal(auth)
                    .orElseThrow(() -> new ServerException(ExceptionMessage.UNAUTHORIZED, HttpStatus.UNAUTHORIZED));
            var token = jwtTokenProvider.generateToken(auth);
            var cookieExpires = authUtil.formatCookieExpires(jwtTokenProvider.getExpiresFromJwt(token));
            logger.info("Logged in: " + dto.getUsernameOrEmailOrPhone());
            return LoginProcessDto.builder()
                    .token(token)
                    .role(user.getRoleName())
                    .cookieExpires(cookieExpires)
                    .fullName(user.getFullName())
                    .build();
        } catch (Exception e) {
            logger.error("Login failed: " + dto.getUsernameOrEmailOrPhone());
            logger.error("Login error: " + e.getMessage());
            throw new ServerException(ExceptionMessage.INVALID_CREDENTIALS, HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    @Transactional
    public ValidateTokenResponseDto validateToken() {
        var userId = authUtil.getPrincipalId(SecurityContextHolder.getContext().getAuthentication());
        if (isNull(userId)) {
            logger.error("Token is not validated, userId is null");
            return null;
        }
        var user = userRepository.findById(userId).orElseThrow(() -> {
            logger.error("Token is not validated, user with id {} not found", userId);
            return new ServerException(ExceptionMessage.INVALID_TOKEN, HttpStatus.UNAUTHORIZED);
        });
        logger.info("Token validated for {}", user.getUsername());
        return ValidateTokenResponseDto.builder()
                .fullName(user.getFullName())
                .role(user.getRoleName())
                .build();
    }
}
