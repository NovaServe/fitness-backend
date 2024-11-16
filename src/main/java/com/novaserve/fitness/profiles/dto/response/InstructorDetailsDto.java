/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.profiles.dto.response;

import com.novaserve.fitness.trainings.dto.response.TrainingDetailsBaseDto;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class InstructorDetailsDto extends EmployeeDetailsDto {
    private List<AreaDetailsDto> areas;

    private List<TrainingDetailsBaseDto> trainings;
}
