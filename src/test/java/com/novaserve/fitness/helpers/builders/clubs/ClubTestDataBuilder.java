/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.helpers.builders.clubs;

import com.novaserve.fitness.helpers.Util;
import com.novaserve.fitness.helpers.builders.TestDataBuilder;
import com.novaserve.fitness.profiles.model.Club;
import com.novaserve.fitness.profiles.model.ClubAddress;
import com.novaserve.fitness.profiles.model.ClubSchedule;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ClubTestDataBuilder implements TestDataBuilder<Club> {
    private Integer seed;

    private Integer addressSeed;

    private Integer clubScheduleSeed;

    private List<Consumer<Club>> consumers;

    private final Club instance;

    public ClubTestDataBuilder() {
        instance = Club.builder().build();
    }

    @Override
    public ClubTestDataBuilder withSeed(int seed) {
        this.seed = seed;
        return this;
    }

    public ClubTestDataBuilder withAddressSeed(int addressSeed) {
        this.addressSeed = addressSeed;
        return this;
    }

    public ClubTestDataBuilder withClubScheduleSeed(int clubScheduleSeed) {
        this.clubScheduleSeed = clubScheduleSeed;
        return this;
    }

    @Override
    public ClubTestDataBuilder with(Consumer<Club> consumer) {
        if (consumers == null) {
            consumers = new ArrayList<>();
        }
        consumers.add(consumer);
        return this;
    }

    @Override
    public Club build() {
        if (seed != null) {
            instance.setName(Util.generateTextWithSeed("Club ", seed));
        }

        if (addressSeed != null) {
            ClubAddress clubAddress =
                    new ClubAddressTestDataBuilder().withSeed(addressSeed).build();
            instance.setAddress(clubAddress);
        }

        if (clubScheduleSeed != null) {
            ClubSchedule clubSchedule = new ClubScheduleTestDataBuilder()
                    .withSeed(clubScheduleSeed)
                    .withDefaultTime()
                    .build();
            instance.setSchedule(clubSchedule);
        }

        if (consumers != null) {
            consumers.forEach(consumer -> consumer.accept(instance));
        }
        return instance;
    }
}
