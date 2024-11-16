/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.trainings.repository;

import com.novaserve.fitness.trainings.model.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {}
