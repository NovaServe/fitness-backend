/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.profiles.service;

import com.novaserve.fitness.emails.ConfirmationCodeHelper;
import com.novaserve.fitness.emails.EmailSenderService;
import com.novaserve.fitness.exceptions.InvalidDtoType;
import com.novaserve.fitness.profiles.dto.request.AdminCreationDto;
import com.novaserve.fitness.profiles.dto.request.UserCreationBaseDto;
import com.novaserve.fitness.profiles.model.*;
import com.novaserve.fitness.profiles.repository.UserRepository;
import java.security.NoSuchAlgorithmException;
import java.util.Set;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminCreationStrategyImpl implements UserCreationStrategy {
    private final ProfileUtil profileUtil;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final EmailSenderService emailSenderService;

    private final ConfirmationCodeHelper confirmationCodeHelper;

    public AdminCreationStrategyImpl(
            ProfileUtil profileUtil,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            EmailSenderService emailSenderService,
            ConfirmationCodeHelper confirmationCodeHelper) {
        this.profileUtil = profileUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailSenderService = emailSenderService;
        this.confirmationCodeHelper = confirmationCodeHelper;
    }

    @Override
    @Transactional
    public UserBase createUser(UserCreationBaseDto userCreationDto) throws NoSuchAlgorithmException {
        if (userCreationDto instanceof AdminCreationDto creationDto) {
            profileUtil.throwUserAlreadyExistsIfSo(
                    userCreationDto.getUsername(), userCreationDto.getEmail(), userCreationDto.getPhone());

            Set<Club> clubs = profileUtil.getClubsByIds(creationDto.getClubsIds());

            Admin admin = Admin.builder()
                    .username(creationDto.getUsername())
                    .email(creationDto.getEmail())
                    .phone(creationDto.getPhone())
                    .fullName(creationDto.getFullName())
                    .password(passwordEncoder.encode(creationDto.getPassword()))
                    .role(creationDto.getRole())
                    .gender(creationDto.getGender())
                    .ageGroup(creationDto.getAgeGroup())
                    .clubs(clubs)
                    .startDate(creationDto.getStartDate())
                    .build();

            UserBase savedUser = userRepository.save(admin);

            String confirmationCode = confirmationCodeHelper.generateConfirmationCodeAndSave(savedUser);
            emailSenderService.sendEmailOnNewAccountCreationAsync(
                    savedUser.getFullName(), confirmationCode, savedUser.getEmail());

            return savedUser;
        }

        throw new InvalidDtoType();
    }
}
