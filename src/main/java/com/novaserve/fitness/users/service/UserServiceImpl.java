package com.novaserve.fitness.users.service;

import com.novaserve.fitness.exception.ApiException;
import com.novaserve.fitness.exception.ErrorMessage;
import com.novaserve.fitness.users.dto.UserResponseDto;
import com.novaserve.fitness.users.model.User;
import com.novaserve.fitness.users.repository.RoleRepository;
import com.novaserve.fitness.users.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    ModelMapper modelMapper;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public User getUserById(long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new ApiException(ErrorMessage.USER_NOT_FOUND, userId, HttpStatus.BAD_REQUEST));
    }

    @Override
    public UserResponseDto getUserDetailById(long userId) {
        return userRepository
                .findById(userId)
                .map(user -> modelMapper.map(user, UserResponseDto.class))
                .orElseThrow(() -> new ApiException(ErrorMessage.USER_NOT_FOUND, userId, HttpStatus.NOT_FOUND));
    }
}
