/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.trainings.controller;

import com.novaserve.fitness.trainings.dto.response.TrainingsResponseDto;
import com.novaserve.fitness.trainings.model.enums.Intensity;
import com.novaserve.fitness.trainings.model.enums.Kind;
import com.novaserve.fitness.trainings.model.enums.Level;
import com.novaserve.fitness.trainings.model.enums.Type;
import com.novaserve.fitness.trainings.service.TrainingService;
import io.swagger.v3.oas.annotations.Operation;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.basePath}/${api.version}/trainings")
public class TrainingController {
    private final TrainingService trainingService;

    public TrainingController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @Operation(summary = "Get trainings")
    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_INSTRUCTOR', 'ROLE_CUSTOMER')")
    public ResponseEntity<TrainingsResponseDto> getTrainings(
            @RequestParam LocalDate startRange,
            @RequestParam LocalDate endRange,
            @RequestParam(required = false) List<String> areas,
            @RequestParam(required = false) List<Long> instructors,
            @RequestParam(required = false) List<Intensity> intensity,
            @RequestParam(required = false) List<Level> levels,
            @RequestParam(required = false) List<Type> types,
            @RequestParam(required = false) List<Kind> kinds,
            @RequestParam(required = false) Boolean availableOnly) {
        TrainingsResponseDto trainingsResponseDto = trainingService.getTrainings(
                startRange, endRange, areas, instructors, intensity, levels, types, kinds, availableOnly);
        return ResponseEntity.ok(trainingsResponseDto);
    }
}
