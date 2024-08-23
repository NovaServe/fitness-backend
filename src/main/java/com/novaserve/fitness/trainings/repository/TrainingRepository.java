/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.trainings.repository;

import com.novaserve.fitness.trainings.model.*;
import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TrainingRepository extends JpaRepository<Training, Long> {
    @Query("SELECT DISTINCT t FROM Training t "
            + "JOIN t.repeatOptions ro "
            + "LEFT JOIN t.areas a "
            + "WHERE (ro.isActive) "
            + "AND (ro.repeatSince >= :startRange) "
            + "AND (ro.repeatUntil IS NULL OR ro.repeatUntil <= :endRange) "
            + "AND (:areas IS NULL OR a IN :areas) "
            + "AND (:instructors IS NULL OR t.instructor IN :instructors) "
            + "AND (:intensity IS NULL OR t.intensity IN :intensity) "
            + "AND (:levels IS NULL OR t.level IN :levels) "
            + "AND (:types IS NULL OR t.type IN :types) "
            + "AND (:kinds IS NULL OR t.kind IN :kinds)")
    List<Training> getTrainings(
            Date startRange,
            Date endRange,
            List<String> areas,
            List<Long> instructors,
            List<Intensity> intensity,
            List<Level> levels,
            List<Type> types,
            List<Kind> kinds);
}
