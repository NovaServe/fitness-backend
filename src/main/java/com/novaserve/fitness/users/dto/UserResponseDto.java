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

    private String email;

    private String fullName;

    private Long countryId;

    private Long timezoneId;

    private Integer age;
}
