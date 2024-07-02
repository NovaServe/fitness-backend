package com.novaserve.fitness.user.service;

import com.novaserve.fitness.user.model.User;
import com.novaserve.fitness.user.repository.UserRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserUtilImpl implements UserUtil {
    @Autowired
    UserRepository userRepository;

    @Override
    public Optional<User> getUserByUsernameOrEmail(String username, String email) {
        return userRepository.findByUsernameOrEmail(username, email);
    }
}
