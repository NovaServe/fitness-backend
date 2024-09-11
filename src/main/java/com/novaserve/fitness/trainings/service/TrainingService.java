/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.trainings.service;

import com.novaserve.fitness.trainings.dto.TrainingsResponseDto;
import com.novaserve.fitness.trainings.model.Intensity;
import com.novaserve.fitness.trainings.model.Kind;
import com.novaserve.fitness.trainings.model.Level;
import com.novaserve.fitness.trainings.model.Type;
import java.time.LocalDate;
import java.util.List;

public interface TrainingService {
    TrainingsResponseDto getTrainings(
            LocalDate startRange,
            LocalDate endRange,
            List<String> areas,
            List<Long> instructors,
            List<Intensity> intensity,
            List<Level> levels,
            List<Type> types,
            List<Kind> kinds,
            Boolean availableOnly);
}
