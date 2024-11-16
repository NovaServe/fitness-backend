/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.profiles.model;

import com.novaserve.fitness.payments.model.Payment;
import com.novaserve.fitness.payments.model.SubscriptionPlan;
import com.novaserve.fitness.trainings.model.Assignment;
import jakarta.persistence.*;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("CUSTOMER")
public class Customer extends UserBase {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_plan_id")
    SubscriptionPlan subscriptionPlan;

    @Column(name = "is_subscription_paid")
    private Boolean isSubscriptionPaid;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "customer")
    private Set<Payment> payments;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "customer")
    private Set<Assignment> assignments;
}
