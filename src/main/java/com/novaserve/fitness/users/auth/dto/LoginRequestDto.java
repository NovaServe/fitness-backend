package com.novaserve.fitness.users.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {
    @NotBlank
    private String usernameOrEmailOrPhone;

    @NotBlank
    private String password;
}
