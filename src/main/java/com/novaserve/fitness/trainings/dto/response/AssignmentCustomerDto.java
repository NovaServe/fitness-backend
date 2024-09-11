/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.trainings.dto.response;

import java.time.LocalDate;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentCustomerDto {
    private long assignmentId;

    private LocalDate repeatSince;

    private LocalDate repeatUntil;

    private Integer repeatTimes;

    private boolean isRecurring;

    private long customerId;

    private String fullName;

    private String username;
}
