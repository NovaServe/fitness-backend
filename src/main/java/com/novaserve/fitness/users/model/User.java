/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.users.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "full_name", nullable = false, unique = false)
    private String fullName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "phone", nullable = true, unique = true)
    private String phone;

    @Column(name = "password", nullable = false, unique = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, unique = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = true, unique = false)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "age_group", nullable = true, unique = false)
    private AgeGroup ageGroup;

    public String getRoleName() {
        return role != null ? role.name() : null;
    }

    public boolean isSuperadmin() {
        return "ROLE_SUPERADMIN".equals(getRoleName());
    }

    public boolean isAdmin() {
        return "ROLE_ADMIN".equals(getRoleName());
    }

    public boolean isCustomer() {
        return "ROLE_CUSTOMER".equals(getRoleName());
    }

    public boolean isInstructor() {
        return "ROLE_INSTRUCTOR".equals(getRoleName());
    }
}
