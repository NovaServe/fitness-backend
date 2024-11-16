/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.trainings.model;

import com.novaserve.fitness.profiles.model.Customer;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
@Table(name = "assignments")
public class Assignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

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

    @CreatedDate
    @Temporal(value = TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @CreatedBy
    @Column(name = "created_by")
    private String createdBy;

    @LastModifiedDate
    @Temporal(value = TemporalType.TIMESTAMP)
    @Column(name = "last_modified_at")
    private LocalDateTime lastModifiedAt;

    @LastModifiedBy
    @Column(name = "last_modified_by")
    private String lastModifiedBy;
}
