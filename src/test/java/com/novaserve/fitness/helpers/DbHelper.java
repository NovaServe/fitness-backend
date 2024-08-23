/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.helpers;

import com.novaserve.fitness.helpers.builders.AreaTestBuilder;
import com.novaserve.fitness.helpers.builders.RepeatOptionTestBuilder;
import com.novaserve.fitness.helpers.builders.TrainingTestBuilder;
import com.novaserve.fitness.trainings.model.*;
import com.novaserve.fitness.trainings.repository.AreaRepository;
import com.novaserve.fitness.trainings.repository.RepeatOptionRepository;
import com.novaserve.fitness.trainings.repository.TrainingRepository;
import com.novaserve.fitness.users.model.AgeGroup;
import com.novaserve.fitness.users.model.Gender;
import com.novaserve.fitness.users.model.Role;
import com.novaserve.fitness.users.model.User;
import com.novaserve.fitness.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired
    PasswordEncoder passwordEncoder;

    Area areaInstance;

    Training trainingInstance;

    RepeatOption repeatOptionInstance;

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
        userRepository.deleteAll();
    }

    public UserBuilder user() {
        return new UserBuilder(userRepository, passwordEncoder);
    }

    public static class UserBuilder {
        private int seed;
        private Role role;
        private Gender gender;
        private AgeGroup ageGroup;
        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;

        public UserBuilder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
            this.userRepository = userRepository;
            this.passwordEncoder = passwordEncoder;
        }

        public UserBuilder seed(int seed) {
            this.seed = seed;
            return this;
        }

        public UserBuilder role(Role role) {
            this.role = role;
            return this;
        }

        public UserBuilder gender(Gender gender) {
            this.gender = gender;
            return this;
        }

        public UserBuilder ageGroup(AgeGroup ageGroup) {
            this.ageGroup = ageGroup;
            return this;
        }

        public User get() {
            return userRepository.save(User.builder()
                    .username("username" + seed)
                    .fullName("User Full Name " + Util.getNumberName(seed))
                    .email("username" + seed + "@email.com")
                    .phone("+312300000" + seed)
                    .password(passwordEncoder.encode("Password" + seed + "!"))
                    .role(role == null ? Role.ROLE_ADMIN : role)
                    .gender(gender == null ? Gender.Female : gender)
                    .ageGroup(ageGroup == null ? AgeGroup.Adult : ageGroup)
                    .build());
        }
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
        if (Area.class.equals(clazz)) {
            Area saved = areaRepository.save(areaInstance);
            areaInstance = null;
            return clazz.cast(saved);
        }
        if (Training.class.equals(clazz)) {
            Training saved = trainingRepository.save(trainingInstance);
            trainingInstance = null;
            return clazz.cast(saved);
        }
        if (RepeatOption.class.equals(clazz)) {
            RepeatOption saved = repeatOptionRepository.save(repeatOptionInstance);
            repeatOptionInstance = null;
            return clazz.cast(saved);
        }
        throw new ClassCastException();
    }
}
