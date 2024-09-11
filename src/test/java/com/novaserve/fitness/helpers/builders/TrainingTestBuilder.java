/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.helpers.builders;

import com.novaserve.fitness.helpers.DbHelper;
import com.novaserve.fitness.trainings.model.*;
import com.novaserve.fitness.trainings.model.enums.Intensity;
import com.novaserve.fitness.trainings.model.enums.Kind;
import com.novaserve.fitness.trainings.model.enums.Level;
import com.novaserve.fitness.trainings.model.enums.Type;
import com.novaserve.fitness.users.model.User;
import java.util.Set;

public class TrainingTestBuilder<T> {
    private int seed;
    private Kind kind;
    private Type type;
    private Intensity intensity;
    private Level level;
    private String location;
    private Integer totalPlaces;
    private User instructor;
    private Set<Area> areas;
    private T callerInstance;

    public TrainingTestBuilder() {}

    public TrainingTestBuilder(T callerInstance) {
        this.callerInstance = callerInstance;
    }

    public TrainingTestBuilder<T> seed(int seed) {
        this.seed = seed;
        return this;
    }

    public TrainingTestBuilder<T> kind(Kind kind) {
        this.kind = kind;
        return this;
    }

    public TrainingTestBuilder<T> type(Type type) {
        this.type = type;
        return this;
    }

    public TrainingTestBuilder<T> intensity(Intensity intensity) {
        this.intensity = intensity;
        return this;
    }

    public TrainingTestBuilder<T> level(Level level) {
        this.level = level;
        return this;
    }

    public TrainingTestBuilder<T> location(String location) {
        this.location = location;
        return this;
    }

    public TrainingTestBuilder<T> totalPlaces(Integer totalPlaces) {
        this.totalPlaces = totalPlaces;
        return this;
    }

    public TrainingTestBuilder<T> instructor(User instructor) {
        this.instructor = instructor;
        return this;
    }

    public TrainingTestBuilder<T> areas(Area... areas) {
        this.areas = Set.of(areas);
        return this;
    }

    private Training instance() {
        return Training.builder()
                .title("Title " + seed)
                .description("Description " + seed)
                .kind(kind == null ? Kind.Group : kind)
                .type(type == null ? Type.In_person : type)
                .intensity(intensity == null ? Intensity.Moderate : intensity)
                .level(level == null ? Level.Intermediate : level)
                .location(location == null ? "Location " + seed : location)
                .totalPlaces(totalPlaces == null ? 20 : totalPlaces)
                .instructor(instructor)
                .areas(areas)
                .build();
    }

    public T build() {
        if (callerInstance instanceof DbHelper) {
            ((DbHelper) callerInstance).setTrainingInstance(instance());
            return callerInstance;
        } else {
            return (T) instance();
        }
    }
}
