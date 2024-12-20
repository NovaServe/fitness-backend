/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.auth.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidateTokenResponseDto {
    private String role;

    private String fullName;
}
