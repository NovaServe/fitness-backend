/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.users.repository;

import com.novaserve.fitness.users.model.Gender;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenderRepository extends JpaRepository<Gender, Long> {
  Optional<Gender> findByName(String name);
}
