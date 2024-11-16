/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.profiles.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class EmployeeCreationDto extends UserCreationBaseDto {
    @NotNull
    private Set<Long> clubsIds;

    @NotNull
    private LocalDate startDate;
}
