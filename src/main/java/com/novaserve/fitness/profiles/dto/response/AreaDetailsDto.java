/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.profiles.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AreaDetailsDto {
    private Long id;

    private String name;
}
