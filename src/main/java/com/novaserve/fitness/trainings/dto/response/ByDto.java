/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.trainings.dto.response;

import com.novaserve.fitness.profiles.model.Role;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ByDto {
    private long id;

    private String fullName;

    private Role role;
}
