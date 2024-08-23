/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.trainings.service;

import com.novaserve.fitness.trainings.dto.TrainingsResponseDto;
import com.novaserve.fitness.trainings.model.*;
import com.novaserve.fitness.trainings.repository.TrainingRepository;
import com.novaserve.fitness.users.model.Role;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrainingServiceImpl implements TrainingService {
    @Autowired
    TrainingRepository trainingRepository;

    @Override
    public TrainingsResponseDto getTrainingsFactory(Role role, long userId) {
        return null;
    }

    @Override
    public TrainingsResponseDto getTrainings(
            Date startRange,
            Date endRange,
            List<String> areas,
            List<Long> instructors,
            List<Intensity> intensity,
            List<Level> levels,
            List<Type> types,
            List<Kind> kinds,
            Boolean withFreePlacesOnly) {
        areas = (areas != null && areas.isEmpty()) ? null : areas;
        instructors = (instructors != null && instructors.isEmpty()) ? null : instructors;
        intensity = (intensity != null && intensity.isEmpty()) ? null : intensity;
        levels = (levels != null && levels.isEmpty()) ? null : levels;
        types = (types != null && types.isEmpty()) ? null : types;
        kinds = (kinds != null && kinds.isEmpty()) ? null : kinds;

        List<Training> trainings = trainingRepository.getTrainings(
                startRange, endRange, areas, instructors, intensity, levels, types, kinds);

        return null;
    }
}
