/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.helpers;

import com.novaserve.fitness.emails.model.ConfirmationCode;
import com.novaserve.fitness.emails.repository.ConfirmationCodeRepository;
import com.novaserve.fitness.payments.model.SubscriptionPlan;
import com.novaserve.fitness.payments.repository.SubscriptionPlanRepository;
import com.novaserve.fitness.profiles.model.*;
import com.novaserve.fitness.profiles.model.Club;
import com.novaserve.fitness.profiles.model.ClubSchedule;
import com.novaserve.fitness.profiles.repository.ClubRepository;
import com.novaserve.fitness.profiles.repository.ClubScheduleRepository;
import com.novaserve.fitness.profiles.repository.UserRepository;
import com.novaserve.fitness.trainings.model.Assignment;
import com.novaserve.fitness.trainings.model.RepeatOption;
import com.novaserve.fitness.trainings.model.Training;
import com.novaserve.fitness.trainings.repository.AreaRepository;
import com.novaserve.fitness.trainings.repository.AssignmentRepository;
import com.novaserve.fitness.trainings.repository.RepeatOptionRepository;
import com.novaserve.fitness.trainings.repository.TrainingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.transaction.annotation.Transactional;

@TestComponent
public class DbHelper {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConfirmationCodeRepository confirmationCodeRepository;

    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private ClubScheduleRepository clubScheduleRepository;

    @Autowired
    private SubscriptionPlanRepository subscriptionPlanRepository;

    @Autowired
    private TrainingRepository trainingRepository;

    @Autowired
    private RepeatOptionRepository repeatOptionRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    public <T> T save(Object o) {
        return switch (o.getClass().getSimpleName()) {
            case "Area" -> (T) areaRepository.save((Area) o);
            case "Club" -> (T) clubRepository.save((Club) o);
            case "ClubSchedule" -> (T) clubScheduleRepository.save((ClubSchedule) o);
            case "SuperAdmin" -> (T) userRepository.save((SuperAdmin) o);
            case "Admin" -> (T) userRepository.save((Admin) o);
            case "Instructor" -> (T) userRepository.save((Instructor) o);
            case "Customer" -> (T) userRepository.save((Customer) o);
            case "ConfirmationCode" -> (T) confirmationCodeRepository.save((ConfirmationCode) o);
            case "SubscriptionPlan" -> (T) subscriptionPlanRepository.save((SubscriptionPlan) o);
            case "Training" -> (T) trainingRepository.save((Training) o);
            case "RepeatOption" -> (T) repeatOptionRepository.save((RepeatOption) o);
            case "Assignment" -> (T) assignmentRepository.save((Assignment) o);
            default -> throw new IllegalStateException(
                    "Unexpected value: " + o.getClass().getSimpleName());
        };
    }

    @Transactional
    public void deleteAll() {
        areaRepository.deleteAll();
        subscriptionPlanRepository.deleteAll();
        clubRepository.deleteAll();
        clubScheduleRepository.deleteAll();
        repeatOptionRepository.deleteAll();
        assignmentRepository.deleteAll();
        trainingRepository.deleteAll();
        confirmationCodeRepository.deleteAll();
        userRepository.deleteAll();
    }

    public UserBase getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
}
