/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.profiles.model;

import com.novaserve.fitness.trainings.model.Training;
import jakarta.persistence.*;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("INSTRUCTOR")
public class Instructor extends Employee {
    @Column(name = "bio", length = 500)
    private String bio;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "instructors_areas",
            joinColumns = @JoinColumn(name = "instructor_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "area_id", referencedColumnName = "id"))
    private Set<Area> areas;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "instructor")
    private Set<Training> trainings;
}
