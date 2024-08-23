/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.trainings.model;

import jakarta.persistence.*;
import java.sql.Time;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "repeat_options")
public class RepeatOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "training_id", nullable = false)
    private Training training;

    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(name = "start_time", nullable = false)
    private Time startTime;

    @Column(name = "end_time", nullable = false)
    private Time endTime;

    @Column(name = "repeat_since", nullable = false)
    private LocalDate repeatSince;

    @Column(name = "repeat_until")
    private LocalDate repeatUntil;

    @Column(name = "repeat_times")
    private Integer repeatTimes;

    @Column(name = "is_autoupdate")
    private Boolean isAutoupdate;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    /**
     * "YYYY-MM-DD;YYYY-MM-DD;..."
     */
    @Column(name = "excluded_dates")
    private String excludedDates;

    @Column(name = "deactivation_since")
    private LocalDate deactivationSince;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "repeatOption")
    private Set<Assignment> assignments;
}
