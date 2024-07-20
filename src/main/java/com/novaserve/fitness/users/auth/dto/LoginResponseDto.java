package com.novaserve.fitness.users.auth.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {
    private String role;

    private String fullName;
}
