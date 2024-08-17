/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.trainings.dto;

import java.time.DayOfWeek;
import java.util.Date;
import java.util.List;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DayDto {
    private Date date;

    private DayOfWeek dayOfWeek;

    private List<SingleTrainingDto> trainings;
}
