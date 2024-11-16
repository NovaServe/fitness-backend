/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.profiles.repository;

import com.novaserve.fitness.profiles.model.ClubSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClubScheduleRepository extends JpaRepository<ClubSchedule, Long> {}
