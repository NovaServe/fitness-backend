/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.helpers.builders;

import com.novaserve.fitness.helpers.DbHelper;
import com.novaserve.fitness.trainings.model.Area;

public class AreaTestBuilder<T> {
    private int seed;
    private String name;
    private T callerOrInstance;

    public AreaTestBuilder() {}
    ;

    public AreaTestBuilder(T callerOrInstance) {
        this.callerOrInstance = callerOrInstance;
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
        if (callerOrInstance instanceof DbHelper) {
            ((DbHelper) callerOrInstance).setAreaInstance(instance());
            return callerOrInstance;
        } else {
            return (T) instance();
        }
    }
}
