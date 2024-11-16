/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.emails.model;

import com.novaserve.fitness.profiles.model.UserBase;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "confirmation_codes")
@EntityListeners(AuditingEntityListener.class)
public class ConfirmationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code")
    private String code;

    @CreatedDate
    @Temporal(value = TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToOne
    @JoinColumn(name = "user_id")
    private UserBase user;
}
