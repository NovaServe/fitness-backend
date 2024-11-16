/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.auth.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {
    private String token;

    private String role;

    private String fullName;
}
