/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.helpers.builders.profiles;

import com.novaserve.fitness.helpers.Util;
import com.novaserve.fitness.helpers.builders.TestDataBuilder;
import com.novaserve.fitness.profiles.model.ClubAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ClubAddressTestDataBuilder implements TestDataBuilder<ClubAddress> {
    private Integer seed;

    private List<Consumer<ClubAddress>> consumers;

    private final ClubAddress instance;

    public ClubAddressTestDataBuilder() {
        instance = ClubAddress.builder().build();
    }

    @Override
    public ClubAddressTestDataBuilder withSeed(int seed) {
        this.seed = seed;
        return this;
    }

    @Override
    public ClubAddressTestDataBuilder with(Consumer<ClubAddress> consumer) {
        if (consumers == null) {
            consumers = new ArrayList<>();
        }
        consumers.add(consumer);
        return this;
    }

    @Override
    public ClubAddress build() {
        if (seed != null) {
            instance.setCity(Util.generateTextWithSeed("City ", seed));
            instance.setAddress(Util.generateTextWithSeed("Address ", seed));
        }
        if (consumers != null) {
            consumers.forEach(consumer -> consumer.accept(instance));
        }
        return instance;
    }
}
