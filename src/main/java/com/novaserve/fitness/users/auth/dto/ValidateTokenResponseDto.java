package com.novaserve.fitness.users.auth.dto;

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
