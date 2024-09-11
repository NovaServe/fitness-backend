/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.helpers;

import com.novaserve.fitness.helpers.builders.AreaTestBuilder;
import com.novaserve.fitness.helpers.builders.RepeatOptionTestBuilder;
import com.novaserve.fitness.helpers.builders.TrainingTestBuilder;
import com.novaserve.fitness.helpers.builders.UserTestBuilder;
import com.novaserve.fitness.trainings.model.*;
import com.novaserve.fitness.trainings.repository.AreaRepository;
import com.novaserve.fitness.trainings.repository.RepeatOptionRepository;
import com.novaserve.fitness.trainings.repository.TrainingRepository;
import com.novaserve.fitness.users.model.User;
import com.novaserve.fitness.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.transaction.annotation.Transactional;

@TestComponent
public class DbHelper {
    @Autowired
    UserRepository userRepository;

    @Autowired
    AreaRepository areaRepository;

    @Autowired
    TrainingRepository trainingRepository;

    @Autowired
    RepeatOptionRepository repeatOptionRepository;

    User userInstance;
    Area areaInstance;
    Training trainingInstance;
    RepeatOption repeatOptionInstance;

    public void setUserInstance(User userInstance) {
        this.userInstance = userInstance;
    }

    public void setAreaInstance(Area areaInstance) {
        this.areaInstance = areaInstance;
    }

    public void setTrainingInstance(Training trainingInstance) {
        this.trainingInstance = trainingInstance;
    }

    public void setRepeatOptionInstance(RepeatOption repeatOptionInstance) {
        this.repeatOptionInstance = repeatOptionInstance;
    }

    @Transactional
    public void deleteAll() {
        areaRepository.deleteAll();
        repeatOptionRepository.deleteAll();
        trainingRepository.deleteAll();
        userRepository.deleteAll();
    }

    public UserTestBuilder<DbHelper> user() {
        return new UserTestBuilder<DbHelper>(this);
    }

    public User getUser(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public AreaTestBuilder<DbHelper> area() {
        return new AreaTestBuilder<DbHelper>(this);
    }

    public TrainingTestBuilder<DbHelper> training() {
        return new TrainingTestBuilder<DbHelper>(this);
    }

    public RepeatOptionTestBuilder<DbHelper> repeatOption() {
        return new RepeatOptionTestBuilder<DbHelper>(this);
    }

    public <T> T save(Class<T> clazz) {
        return switch (clazz.getSimpleName()) {
            case "User" -> {
                userInstance.setId(null);
                User saved = userRepository.save(userInstance);
                userInstance = null;
                yield clazz.cast(saved);
            }
            case "Area" -> {
                Area saved = areaRepository.save(areaInstance);
                areaInstance = null;
                yield clazz.cast(saved);
            }
            case "Training" -> {
                Training saved = trainingRepository.save(trainingInstance);
                trainingInstance = null;
                yield clazz.cast(saved);
            }
            case "RepeatOption" -> {
                RepeatOption saved = repeatOptionRepository.save(repeatOptionInstance);
                repeatOptionInstance = null;
                yield clazz.cast(saved);
            }
            default -> throw new ClassCastException();
        };
    }
}
