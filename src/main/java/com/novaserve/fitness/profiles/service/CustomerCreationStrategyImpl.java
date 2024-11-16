/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.profiles.service;

import com.novaserve.fitness.emails.ConfirmationCodeHelper;
import com.novaserve.fitness.emails.EmailSenderService;
import com.novaserve.fitness.exceptions.InvalidDtoType;
import com.novaserve.fitness.payments.model.SubscriptionPlan;
import com.novaserve.fitness.payments.service.PaymentUtil;
import com.novaserve.fitness.profiles.dto.request.CustomerCreationDto;
import com.novaserve.fitness.profiles.dto.request.UserCreationBaseDto;
import com.novaserve.fitness.profiles.model.Customer;
import com.novaserve.fitness.profiles.model.UserBase;
import com.novaserve.fitness.profiles.repository.UserRepository;
import java.security.NoSuchAlgorithmException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerCreationStrategyImpl implements UserCreationStrategy {
    private final ProfileUtil profileUtil;

    private final PaymentUtil paymentUtil;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final EmailSenderService emailSenderService;

    private final ConfirmationCodeHelper confirmationCodeHelper;

    public CustomerCreationStrategyImpl(
            ProfileUtil profileUtil,
            PaymentUtil paymentUtil,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            EmailSenderService emailSenderService,
            ConfirmationCodeHelper confirmationCodeHelper) {
        this.profileUtil = profileUtil;
        this.paymentUtil = paymentUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailSenderService = emailSenderService;
        this.confirmationCodeHelper = confirmationCodeHelper;
    }

    @Override
    @Transactional
    public UserBase createUser(UserCreationBaseDto userCreationDto) throws NoSuchAlgorithmException {
        if (userCreationDto instanceof CustomerCreationDto creationDto) {
            profileUtil.throwUserAlreadyExistsIfSo(
                    userCreationDto.getUsername(), userCreationDto.getEmail(), userCreationDto.getPhone());

            SubscriptionPlan subscriptionPlan =
                    paymentUtil.getSubscriptionPlanByIdOrThrowNotFound(creationDto.getSubscriptionPlanId());

            Customer customer = Customer.builder()
                    .username(creationDto.getUsername())
                    .email(creationDto.getEmail())
                    .phone(creationDto.getPhone())
                    .fullName(creationDto.getFullName())
                    .password(passwordEncoder.encode(creationDto.getPassword()))
                    .role(creationDto.getRole())
                    .gender(creationDto.getGender())
                    .ageGroup(creationDto.getAgeGroup())
                    .subscriptionPlan(subscriptionPlan)
                    .build();

            UserBase savedUser = userRepository.save(customer);

            String confirmationCode = confirmationCodeHelper.generateConfirmationCodeAndSave(savedUser);
            emailSenderService.sendEmailOnNewAccountCreationAsync(
                    savedUser.getFullName(), confirmationCode, savedUser.getEmail());

            return savedUser;
        }

        throw new InvalidDtoType();
    }
}
