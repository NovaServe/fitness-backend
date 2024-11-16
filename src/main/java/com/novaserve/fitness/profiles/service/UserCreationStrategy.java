/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.profiles.service;

import com.novaserve.fitness.profiles.dto.request.UserCreationBaseDto;
import com.novaserve.fitness.profiles.model.UserBase;
import java.security.NoSuchAlgorithmException;

public interface UserCreationStrategy {
    UserBase createUser(UserCreationBaseDto userCreationDto) throws NoSuchAlgorithmException;
}
