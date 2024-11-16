/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.helpers.builders.profiles;

import com.novaserve.fitness.helpers.Util;
import com.novaserve.fitness.helpers.builders.TestDataBuilder;
import com.novaserve.fitness.profiles.model.Area;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AreaTestDataBuilder implements TestDataBuilder<Area> {
    private Integer seed;

    private List<Consumer<Area>> consumers;

    private final Area instance;

    public AreaTestDataBuilder() {
        instance = Area.builder().build();
    }

    @Override
    public AreaTestDataBuilder withSeed(int seed) {
        this.seed = seed;
        return this;
    }

    @Override
    public AreaTestDataBuilder with(Consumer<Area> consumer) {
        if (consumers == null) {
            consumers = new ArrayList<>();
        }
        this.consumers.add(consumer);
        return this;
    }

    @Override
    public Area build() {
        if (seed != null) {
            instance.setName("Area " + Util.getNumberName(seed));
        }
        if (consumers != null) {
            consumers.forEach(consumer -> consumer.accept(instance));
        }
        return instance;
    }
}
