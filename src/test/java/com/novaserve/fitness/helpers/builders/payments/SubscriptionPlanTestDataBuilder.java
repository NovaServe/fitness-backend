/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.helpers.builders.payments;

import com.novaserve.fitness.helpers.Util;
import com.novaserve.fitness.helpers.builders.TestDataBuilder;
import com.novaserve.fitness.payments.model.ScheduleType;
import com.novaserve.fitness.payments.model.SubscriptionPlan;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SubscriptionPlanTestDataBuilder implements TestDataBuilder<SubscriptionPlan> {
    private Integer seed;

    private List<Consumer<SubscriptionPlan>> consumers;

    private final SubscriptionPlan instance;

    public SubscriptionPlanTestDataBuilder() {
        instance = SubscriptionPlan.builder().build();
    }

    @Override
    public SubscriptionPlanTestDataBuilder withSeed(int seed) {
        this.seed = seed;
        return this;
    }

    @Override
    public SubscriptionPlanTestDataBuilder with(Consumer<SubscriptionPlan> consumer) {
        if (consumers == null) {
            consumers = new ArrayList<>();
        }
        consumers.add(consumer);
        return this;
    }

    @Override
    public SubscriptionPlan build() {
        if (seed != null) {
            instance.setName(Util.generateTextWithSeed("Subscription Plan ", seed));
        }
        instance.setScheduleType(ScheduleType.FULL_TIME);
        instance.setActive(true);
        instance.setActiveSince(LocalDate.now().minusWeeks(1));

        if (consumers != null) {
            consumers.forEach(consumer -> consumer.accept(instance));
        }
        return instance;
    }
}
