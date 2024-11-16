/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.profiles.model;

import com.novaserve.fitness.payments.model.SubscriptionPlan;
import jakarta.persistence.*;
import java.time.LocalDateTime;
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
@Table(name = "clubs")
public class Club {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "city", column = @Column(name = "city", nullable = false, length = 50)),
        @AttributeOverride(name = "address", column = @Column(name = "address", nullable = false, length = 255))
    })
    private ClubAddress address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_schedule_id", nullable = false)
    ClubSchedule schedule;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "club")
    Set<SubscriptionPlan> subscriptionPlans;

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
