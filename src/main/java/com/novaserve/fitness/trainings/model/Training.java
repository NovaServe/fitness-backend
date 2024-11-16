/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.trainings.model;

import com.novaserve.fitness.profiles.model.Area;
import com.novaserve.fitness.profiles.model.Instructor;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "trainings")
public class Training {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "description", length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "kind", nullable = false)
    private Kind kind;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private Type type;

    @Enumerated(EnumType.STRING)
    @Column(name = "intensity", nullable = false)
    private Intensity intensity;

    @Enumerated(EnumType.STRING)
    @Column(name = "level", nullable = false)
    private Level level;

    @Column(name = "location", nullable = false, length = 500)
    private String location;

    @Column(name = "total_places", nullable = false)
    private int totalPlaces;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id", nullable = false)
    private Instructor instructor;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "trainings_areas",
            joinColumns = @JoinColumn(name = "training_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "area_id", referencedColumnName = "id"))
    private Set<Area> areas;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "training")
    private Set<RepeatOption> repeatOptions;

    @CreatedDate
    @Temporal(value = TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @CreatedBy
    @Column(name = "created_by", nullable = false, updatable = false)
    private String createdBy;

    @LastModifiedDate
    @Temporal(value = TemporalType.TIMESTAMP)
    @Column(name = "last_modified_at")
    private LocalDateTime lastModifiedAt;

    @LastModifiedBy
    @Column(name = "last_modified_by")
    private String lastModifiedBy;
}
