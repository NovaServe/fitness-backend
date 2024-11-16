/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.payments.model;

import com.novaserve.fitness.profiles.model.Customer;
import jakarta.persistence.*;
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
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "amount", nullable = false)
    private float amount;

    @Column(name = "provider", nullable = false)
    private String provider;

    @Column(name = "payed_at", nullable = false)
    private LocalDateTime payedAt;

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
