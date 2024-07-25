/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.users.repository;

import com.novaserve.fitness.users.model.Role;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
  Optional<Role> findByName(String name);
}
