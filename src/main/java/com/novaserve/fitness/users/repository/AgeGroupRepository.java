/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.users.repository;

import com.novaserve.fitness.users.model.AgeGroup;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgeGroupRepository extends JpaRepository<AgeGroup, Long> {
    Optional<AgeGroup> findByName(String name);
}
