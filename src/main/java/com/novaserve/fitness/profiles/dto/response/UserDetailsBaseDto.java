/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.profiles.dto.response;

import java.time.LocalDateTime;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class UserDetailsBaseDto {
    private Long id;

    private String username;

    private String fullName;

    private String email;

    private String phone;

    private String role;

    private String gender;

    private String ageGroup;

    private boolean isActive;

    private LocalDateTime createdAt;

    private String createdBy;

    private LocalDateTime lastModifiedAt;

    private String lastModifiedBy;
}
