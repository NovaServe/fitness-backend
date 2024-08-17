/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.trainings.dto;

import com.novaserve.fitness.users.model.Role;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatedByDto {
    private long id;

    private String fullName;

    private Role role;
}
