/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.trainings.dto.response;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DayDto {
    private LocalDate date;

    private DayOfWeek dayOfWeek;

    private List<TrainingDto> trainings;
}
