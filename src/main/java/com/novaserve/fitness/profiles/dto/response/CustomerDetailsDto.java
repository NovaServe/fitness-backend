/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.profiles.dto.response;

import com.novaserve.fitness.payments.dto.SubscriptionPlanDetailsBaseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class CustomerDetailsDto extends UserDetailsBaseDto {
    private SubscriptionPlanDetailsBaseDto subscriptionPlan;

    private Boolean isSubscriptionPaid;
}
