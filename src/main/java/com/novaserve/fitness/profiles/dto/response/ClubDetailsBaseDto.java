/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.profiles.dto.response;

import com.novaserve.fitness.profiles.model.ClubAddress;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClubDetailsBaseDto {
    private Long id;

    private String name;

    private ClubAddress address;
}
