/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.helpers;

import com.novaserve.fitness.helpers.builders.AreaTestBuilder;
import com.novaserve.fitness.helpers.builders.RepeatOptionTestBuilder;
import com.novaserve.fitness.helpers.builders.TrainingTestBuilder;
import com.novaserve.fitness.helpers.builders.UserTestBuilder;
import com.novaserve.fitness.trainings.model.Area;
import com.novaserve.fitness.trainings.model.RepeatOption;
import com.novaserve.fitness.trainings.model.Training;
import com.novaserve.fitness.users.model.User;

public class MockHelper {
    public UserTestBuilder<User> user() {
        return new UserTestBuilder<User>();
    }

    public AreaTestBuilder<Area> area() {
        return new AreaTestBuilder<Area>();
    }

    public TrainingTestBuilder<Training> training() {
        return new TrainingTestBuilder<Training>();
    }

    public RepeatOptionTestBuilder<RepeatOption> repeatOption() {
        return new RepeatOptionTestBuilder<RepeatOption>();
    }
}
