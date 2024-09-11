/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.users.model;

import com.novaserve.fitness.trainings.model.Assignment;
import com.novaserve.fitness.trainings.model.Training;
import com.novaserve.fitness.users.model.enums.AgeGroup;
import com.novaserve.fitness.users.model.enums.Gender;
import com.novaserve.fitness.users.model.enums.Role;
import jakarta.persistence.*;
import java.util.Set;
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

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "phone", unique = true)
    private String phone;

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "age_group")
    private AgeGroup ageGroup;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "instructor")
    private Set<Training> instructorTrainings;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "customer")
    private Set<Assignment> assignments;

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
