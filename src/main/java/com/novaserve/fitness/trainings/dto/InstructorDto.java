/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.trainings.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstructorDto {
    private int id;

    private String fullName;
}
