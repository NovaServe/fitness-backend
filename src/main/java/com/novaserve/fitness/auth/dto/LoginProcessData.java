package com.novaserve.fitness.auth.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginProcessData {
    private String token;

    private String cookieExpires;

    private String role;

    private String fullName;
}
