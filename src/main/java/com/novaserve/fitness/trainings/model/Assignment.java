/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.trainings.model;

import com.novaserve.fitness.users.model.User;
import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "assignments")
public class Assignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repeat_option_id", nullable = false)
    private RepeatOption repeatOption;

    @Column(name = "is_recurring", nullable = false)
    private boolean isRecurring;

    @Column(name = "repeat_since", nullable = false, updatable = false)
    private LocalDate repeatSince;

    @Column(name = "repeat_until")
    private LocalDate repeatUntil;

    @Column(name = "repeat_times")
    private Integer repeatTimes;

    // "YYYY-MM-DD;YYYY-MM-DD;..."
    @Column(name = "excluded_dates")
    private String excludedDates;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;
}
