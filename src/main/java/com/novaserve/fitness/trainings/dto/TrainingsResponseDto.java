/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.trainings.dto;

import java.util.Date;
import java.util.List;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainingsResponseDto {
    private Date startRange;

    private Date endRange;

    private List<DayDto> content;
}
