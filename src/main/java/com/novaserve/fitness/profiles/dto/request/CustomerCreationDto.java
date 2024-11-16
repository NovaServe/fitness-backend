/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.profiles.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class CustomerCreationDto extends UserCreationBaseDto {
    @NotNull
    Long subscriptionPlanId;
}
