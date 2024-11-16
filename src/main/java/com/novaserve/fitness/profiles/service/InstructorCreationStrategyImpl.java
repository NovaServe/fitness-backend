/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.profiles.service;

import com.novaserve.fitness.emails.ConfirmationCodeHelper;
import com.novaserve.fitness.emails.EmailSenderService;
import com.novaserve.fitness.exceptions.InvalidDtoType;
import com.novaserve.fitness.profiles.dto.request.InstructorCreationDto;
import com.novaserve.fitness.profiles.dto.request.UserCreationBaseDto;
import com.novaserve.fitness.profiles.model.Club;
import com.novaserve.fitness.profiles.model.Instructor;
import com.novaserve.fitness.profiles.model.UserBase;
import com.novaserve.fitness.profiles.repository.UserRepository;
import java.security.NoSuchAlgorithmException;
import java.util.Set;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InstructorCreationStrategyImpl implements UserCreationStrategy {
    private final ProfileUtil profileUtil;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final EmailSenderService emailSenderService;

    private final ConfirmationCodeHelper confirmationCodeHelper;

    public InstructorCreationStrategyImpl(
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
        if (userCreationDto instanceof InstructorCreationDto instructorCreationDto) {
            profileUtil.throwUserAlreadyExistsIfSo(
                    userCreationDto.getUsername(), userCreationDto.getEmail(), userCreationDto.getPhone());

            Set<Club> clubs = profileUtil.getClubsByIds(instructorCreationDto.getClubsIds());

            Instructor instructor = Instructor.builder()
                    .username(instructorCreationDto.getUsername())
                    .email(instructorCreationDto.getEmail())
                    .phone(instructorCreationDto.getPhone())
                    .fullName(instructorCreationDto.getFullName())
                    .password(passwordEncoder.encode(instructorCreationDto.getPassword()))
                    .role(instructorCreationDto.getRole())
                    .gender(instructorCreationDto.getGender())
                    .ageGroup(instructorCreationDto.getAgeGroup())
                    .clubs(clubs)
                    .startDate(instructorCreationDto.getStartDate())
                    .bio(instructorCreationDto.getBio())
                    .build();

            UserBase savedUser = userRepository.save(instructor);

            String confirmationCode = confirmationCodeHelper.generateConfirmationCodeAndSave(savedUser);
            emailSenderService.sendEmailOnNewAccountCreationAsync(
                    savedUser.getFullName(), confirmationCode, savedUser.getEmail());

            return savedUser;
        }

        throw new InvalidDtoType();
    }
}
