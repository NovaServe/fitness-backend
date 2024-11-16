/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.profiles.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "clubs_schedules")
public class ClubSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "monday_start_at")
    private LocalTime mondayStartAt;

    @Column(name = "monday_end_at")
    private LocalTime mondayEndAt;

    @Column(name = "tuesday_start_at")
    private LocalTime tuesdayStartAt;

    @Column(name = "tuesday_end_at")
    private LocalTime tuesdayEndAt;

    @Column(name = "wednesday_start_at")
    private LocalTime wednesdayStartAt;

    @Column(name = "wednesday_end_at")
    private LocalTime wednesdayEndAt;

    @Column(name = "thursday_start_at")
    private LocalTime thursdayStartAt;

    @Column(name = "thursday_end_at")
    private LocalTime thursdayEndAt;

    @Column(name = "friday_start_at")
    private LocalTime fridayStartAt;

    @Column(name = "friday_end_at")
    private LocalTime fridayEndAt;

    @Column(name = "saturday_start_at")
    private LocalTime saturdayStartAt;

    @Column(name = "saturday_end_at")
    private LocalTime saturdayEndAt;

    @Column(name = "sunday_start_at")
    private LocalTime sundayStartAt;

    @Column(name = "sunday_end_at")
    private LocalTime sundayEndAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "schedule")
    Set<Club> clubs;

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
