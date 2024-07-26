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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false, unique = false)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gender_id", nullable = false, unique = false)
    private Gender gender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "age_group_id", nullable = false, unique = false)
    private AgeGroup ageGroup;

    public String getRoleName() {
        return role != null ? role.getName() : null;
    }

    public String getGenderName() {
        return gender != null ? gender.getName() : null;
    }

    public String getAgeGroupName() {
        return ageGroup != null ? ageGroup.getName() : null;
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
