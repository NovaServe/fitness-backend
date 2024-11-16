/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.trainings.dto.response;

import com.novaserve.fitness.profiles.model.Area;
import com.novaserve.fitness.trainings.model.Intensity;
import com.novaserve.fitness.trainings.model.Kind;
import com.novaserve.fitness.trainings.model.Level;
import com.novaserve.fitness.trainings.model.Type;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainingDto {
    private long id;

    private String title;

    private String description;

    private InstructorDto instructor;

    private List<Area> areas;

    private Intensity intensity;

    private Level level;

    private Kind kind;

    private Type type;

    private String location;

    private int totalPlaces;

    private int freePlaces;

    private RepeatOptionDto repeatOption;

    private boolean isAssignedToCustomer;

    private List<AssignmentCustomerDto> assignments;

    private LocalDateTime createdAt;

    private ByDto createdBy;

    private LocalDateTime lastModifiedAt;

    private ByDto lastModifiedBy;
}
