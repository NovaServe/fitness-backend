/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.trainings.dto;

import com.novaserve.fitness.trainings.model.Intensity;
import com.novaserve.fitness.trainings.model.Kind;
import com.novaserve.fitness.trainings.model.Level;
import com.novaserve.fitness.trainings.model.Type;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SingleTrainingDto {
    private long id;

    private Time start;

    private Time end;

    private String title;

    private String description;

    private InstructorDto instructor;

    private List<String> areas;

    private Intensity intensity;

    private Level level;

    private Kind kind;

    private Type type;

    private String location;

    private int totalPlaces;

    private int freePlaces;

    private boolean isBookedByMe;

    private LocalDateTime createdAt;

    private CreatedByDto createdBy;
}
