/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.payments.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPlanDetailsBaseDto {
    private long id;

    private String name;
}
