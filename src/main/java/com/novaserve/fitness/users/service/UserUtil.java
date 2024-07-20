package com.novaserve.fitness.users.service;

import com.novaserve.fitness.users.model.User;
import java.util.Optional;

public interface UserUtil {
    Optional<User> getUserByUsername(String username);

    Optional<User> getUserByUsernameOrEmailOrPhone(String username, String email, String phone);
}
