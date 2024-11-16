/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.profiles.service;

import com.novaserve.fitness.exceptions.NotFound;
import com.novaserve.fitness.exceptions.UserAlreadyExists;
import com.novaserve.fitness.exceptions.UserNotFound;
import com.novaserve.fitness.profiles.model.Club;
import com.novaserve.fitness.profiles.model.UserBase;
import com.novaserve.fitness.profiles.repository.ClubRepository;
import com.novaserve.fitness.profiles.repository.UserRepository;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileUtil {
    private final UserRepository userRepository;

    private final ClubRepository clubRepository;

    public ProfileUtil(UserRepository userRepository, ClubRepository clubRepository) {
        this.userRepository = userRepository;
        this.clubRepository = clubRepository;
    }

    public Optional<UserBase> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<UserBase> getUserByUsernameOrEmailOrPhone(String username, String email, String phone) {
        if (phone == null) {
            phone = "";
        }
        return userRepository.findByUsernameOrEmailOrPhone(username, email, phone);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public UserBase getUserByIdOrThrowNotFound(String username, String email, String phone) {
        Optional<UserBase> userOpt = getUserByUsernameOrEmailOrPhone(username, email, phone);
        if (userOpt.isPresent()) {
            return userOpt.get();
        }
        throw new UserNotFound();
    }

    public void throwUserAlreadyExistsIfSo(String username, String email, String phone) {
        Optional<UserBase> alreadyExists = getUserByUsernameOrEmailOrPhone(username, email, phone);
        if (alreadyExists.isPresent()) {
            throw new UserAlreadyExists();
        }
    }

    public UserBase getUserByIdOrThrowNotFound(long id) {
        UserBase user = userRepository.findById(id).orElseThrow(() -> new UserNotFound());
        return user;
    }

    public Set<Club> getClubsByIds(Set<Long> clubsIds) {
        Set<Club> clubs = clubsIds.stream()
                .map(clubId -> clubRepository.findById(clubId).orElseThrow(() -> new NotFound(Club.class, clubId)))
                .collect(Collectors.toSet());
        return clubs;
    }
}
