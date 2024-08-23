/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.trainings.model;

import com.novaserve.fitness.users.model.User;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

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
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repeat_option_id", nullable = false)
    private RepeatOption repeatOption;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "joined_at", nullable = false, updatable = false)
    private ZonedDateTime joinedAt;

    @Column(name = "is_recurring")
    private Boolean isRecurring;

    @Column(name = "repeat_until")
    private LocalDate repeatUntil;

    @Column(name = "repeat_times")
    private Integer repeatTimes;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    // "YYYY-MM-DD;YYYY-MM-DD;..."
    @Column(name = "excluded_dates")
    private String excludedDates;

    @Column(name = "deactivated_at", updatable = false)
    private ZonedDateTime deactivatedAt;
}
