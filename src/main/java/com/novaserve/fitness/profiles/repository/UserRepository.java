/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.profiles.repository;

import com.novaserve.fitness.profiles.model.Role;
import com.novaserve.fitness.profiles.model.UserBase;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<UserBase, Long> {
    Optional<UserBase> findByUsername(String username);

    Optional<UserBase> findByEmail(String email);

    Optional<UserBase> findByUsernameOrEmail(String username, String email);

    Optional<UserBase> findByUsernameOrEmailOrPhone(String username, String email, String phone);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.role IN :roles AND (:fullName IS NULL OR u.fullName ILIKE %:fullName%)")
    Page<UserBase> getUsers(Set<Role> roles, String fullName, Pageable pageable);
}
