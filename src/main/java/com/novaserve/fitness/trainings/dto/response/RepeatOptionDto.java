/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.trainings.dto.response;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepeatOptionDto {
    private long id;

    private DayOfWeek dayOfWeek;

    private LocalTime startTime;

    private LocalTime endTime;

    private boolean isRecurring;

    private LocalDate repeatSince;

    private LocalDate repeatUntil;

    private Integer repeatTimes;

    private List<LocalDate> excludedDates;

    private boolean isActive;
}
