package com.novaserve.fitness.user.service;

import com.novaserve.fitness.user.model.User;
import java.util.Optional;

public interface UserUtil {
    Optional<User> getUserByUsernameOrEmail(String username, String email);
}
