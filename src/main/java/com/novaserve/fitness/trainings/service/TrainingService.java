/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.trainings.service;

import com.novaserve.fitness.trainings.dto.response.TrainingsResponseDto;
import com.novaserve.fitness.trainings.model.enums.Intensity;
import com.novaserve.fitness.trainings.model.enums.Kind;
import com.novaserve.fitness.trainings.model.enums.Level;
import com.novaserve.fitness.trainings.model.enums.Type;
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
