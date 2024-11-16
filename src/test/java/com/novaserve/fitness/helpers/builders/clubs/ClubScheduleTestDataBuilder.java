/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.helpers.builders.clubs;

import com.novaserve.fitness.helpers.Util;
import com.novaserve.fitness.helpers.builders.TestDataBuilder;
import com.novaserve.fitness.profiles.model.ClubSchedule;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ClubScheduleTestDataBuilder implements TestDataBuilder<ClubSchedule> {
    private Integer seed;

    private Boolean isDefaultTime;

    private List<Consumer<ClubSchedule>> consumers;

    private final ClubSchedule instance;

    public ClubScheduleTestDataBuilder() {
        instance = ClubSchedule.builder().build();
    }

    @Override
    public ClubScheduleTestDataBuilder withSeed(int seed) {
        this.seed = seed;
        return this;
    }

    public ClubScheduleTestDataBuilder withDefaultTime() {
        this.isDefaultTime = true;
        return this;
    }

    @Override
    public ClubScheduleTestDataBuilder with(Consumer<ClubSchedule> consumer) {
        if (consumers == null) {
            consumers = new ArrayList<>();
        }
        consumers.add(consumer);
        return this;
    }

    @Override
    public ClubSchedule build() {
        if (seed != null) {
            instance.setName(Util.generateTextWithSeed("Club Schedule ", seed));
        }

        if (isDefaultTime != null) {
            LocalTime startTime = LocalTime.of(8, 0);
            LocalTime endTime = LocalTime.of(21, 0);

            instance.setMondayStartAt(startTime);
            instance.setMondayEndAt(endTime);
            instance.setTuesdayStartAt(startTime);
            instance.setTuesdayEndAt(endTime);
            instance.setWednesdayStartAt(startTime);
            instance.setWednesdayEndAt(endTime);
            instance.setTuesdayStartAt(startTime);
            instance.setTuesdayEndAt(endTime);
            instance.setThursdayStartAt(startTime);
            instance.setThursdayEndAt(endTime);
            instance.setFridayStartAt(startTime);
            instance.setFridayEndAt(endTime);
            instance.setSaturdayStartAt(startTime);
            instance.setSaturdayEndAt(endTime);
            instance.setSundayStartAt(startTime);
            instance.setSundayEndAt(endTime);
        }

        if (consumers != null) {
            consumers.forEach(consumer -> consumer.accept(instance));
        }
        return instance;
    }
}
