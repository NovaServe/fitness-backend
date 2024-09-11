/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.helpers.builders;

import com.novaserve.fitness.helpers.DbHelper;
import com.novaserve.fitness.trainings.model.Area;

public class AreaTestBuilder<T> {
    private int seed;
    private String name;
    private T callerInstance;

    public AreaTestBuilder() {}
    ;

    public AreaTestBuilder(T callerInstance) {
        this.callerInstance = callerInstance;
    }

    public AreaTestBuilder<T> seed(int seed) {
        this.seed = seed;
        return this;
    }

    public AreaTestBuilder<T> name(String name) {
        this.name = name;
        return this;
    }

    private Area instance() {
        return Area.builder().name(name == null ? "Area " + seed : name).build();
    }

    public T build() {
        if (callerInstance instanceof DbHelper) {
            ((DbHelper) callerInstance).setAreaInstance(instance());
            return callerInstance;
        } else {
            return (T) instance();
        }
    }
}
