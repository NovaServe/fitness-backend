/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.users.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    private Long id;

    private String username;

    private String fullName;

    private String email;

    private String phone;

    private String role;

    private String gender;

    private String ageGroup;
}
