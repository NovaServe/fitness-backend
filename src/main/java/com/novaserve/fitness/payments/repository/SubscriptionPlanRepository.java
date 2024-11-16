/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.payments.repository;

import com.novaserve.fitness.payments.model.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {}
