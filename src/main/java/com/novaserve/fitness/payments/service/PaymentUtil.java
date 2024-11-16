/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.payments.service;

import com.novaserve.fitness.exceptions.NotFound;
import com.novaserve.fitness.payments.model.SubscriptionPlan;
import com.novaserve.fitness.payments.repository.SubscriptionPlanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentUtil {
    private final SubscriptionPlanRepository subscriptionPlanRepository;

    public PaymentUtil(SubscriptionPlanRepository subscriptionPlanRepository) {
        this.subscriptionPlanRepository = subscriptionPlanRepository;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public SubscriptionPlan getSubscriptionPlanByIdOrThrowNotFound(long id) {
        SubscriptionPlan subscriptionPlan =
                subscriptionPlanRepository.findById(id).orElseThrow(() -> new NotFound(SubscriptionPlan.class, id));
        return subscriptionPlan;
    }
}
