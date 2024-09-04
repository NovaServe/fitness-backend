/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.trainings.repository;

import com.novaserve.fitness.trainings.model.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TrainingCriteriaBuilder {
    @Autowired
    EntityManager entityManager;

    public List<Training> getTrainings(
            LocalDate startRange,
            LocalDate endRange,
            List<String> areas,
            List<Long> instructors,
            List<Intensity> intensity,
            List<Level> levels,
            List<Type> types,
            List<Kind> kinds) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Training> cq = cb.createQuery(Training.class);
        Root<Training> training = cq.from(Training.class);
        Join<Training, RepeatOption> repeatOption = training.join("repeatOptions");
        Join<Training, Area> areaJoin = training.join("areas", JoinType.LEFT);

        Predicate predicate = cb.conjunction();

        predicate = cb.and(
                predicate,
                cb.equal(repeatOption.get("isActive"), true),
                cb.greaterThanOrEqualTo(repeatOption.get("repeatSince"), startRange));

        if (endRange != null) {
            predicate = cb.and(
                    predicate,
                    cb.or(
                            cb.isNull(repeatOption.get("repeatUntil")),
                            cb.lessThanOrEqualTo(repeatOption.get("repeatUntil"), endRange)));
        }

        if (areas != null && !areas.isEmpty()) {
            predicate = cb.and(predicate, areaJoin.get("name").in(areas));
        }

        if (instructors != null && !instructors.isEmpty()) {
            predicate = cb.and(predicate, training.get("instructor").get("id").in(instructors));
        }

        if (intensity != null && !intensity.isEmpty()) {
            predicate = cb.and(predicate, training.get("intensity").in(intensity));
        }

        if (levels != null && !levels.isEmpty()) {
            predicate = cb.and(predicate, training.get("level").in(levels));
        }

        if (types != null && !types.isEmpty()) {
            predicate = cb.and(predicate, training.get("type").in(types));
        }

        if (kinds != null && !kinds.isEmpty()) {
            predicate = cb.and(predicate, training.get("kind").in(kinds));
        }

        cq.where(predicate).distinct(true);

        TypedQuery<Training> query = entityManager.createQuery(cq);
        return query.getResultList();
    }
}
