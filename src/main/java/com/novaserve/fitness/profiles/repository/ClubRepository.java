/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.profiles.repository;

import com.novaserve.fitness.profiles.model.Club;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClubRepository extends JpaRepository<Club, Long> {}
