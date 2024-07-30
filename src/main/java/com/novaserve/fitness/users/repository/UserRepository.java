/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.users.repository;

import com.novaserve.fitness.users.model.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsernameOrEmail(String username, String email);

    Optional<User> findByUsernameOrEmailOrPhone(String username, String email, String phone);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query(
            "select u from User u where (role is null or u.role.name = :role) and (fullName is null or u.fullName = :fullName)")
    Page<User> getUsers(String role, String fullName, Pageable pageable);
}
