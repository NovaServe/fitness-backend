/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.users.service;

import com.novaserve.fitness.users.model.User;
import com.novaserve.fitness.users.repository.UserRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserUtilImpl implements UserUtil {
  @Autowired UserRepository userRepository;

  @Override
  public Optional<User> getUserByUsername(String username) {
    return userRepository.findByUsername(username);
  }

  @Override
  public Optional<User> getUserByUsernameOrEmailOrPhone(
      String username, String email, String phone) {
    return userRepository.findByUsernameOrEmailOrPhone(username, email, phone);
  }
}
