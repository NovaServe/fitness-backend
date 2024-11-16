/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.helpers.builders;

import java.util.function.Consumer;

public interface TestDataBuilder<T> {
    TestDataBuilder<T> withSeed(int seed);

    TestDataBuilder<T> with(Consumer<T> consumer);

    T build();
}
