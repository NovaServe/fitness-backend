/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.profiles.dto.response;

import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public abstract class EmployeeDetailsDto extends UserDetailsBaseDto {
    private LocalDate startDate;

    private LocalDate endDate;

    private List<ClubDetailsBaseDto> clubs;
}
