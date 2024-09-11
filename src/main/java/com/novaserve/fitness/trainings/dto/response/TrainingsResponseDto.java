/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.trainings.dto.response;

import java.time.LocalDate;
import java.util.List;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainingsResponseDto {
    private LocalDate startRange;

    private LocalDate endRange;

    private List<DayDto> days;
}
