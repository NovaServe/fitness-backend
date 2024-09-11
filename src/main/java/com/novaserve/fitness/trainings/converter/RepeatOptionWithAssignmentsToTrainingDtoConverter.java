/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.trainings.converter;

import com.novaserve.fitness.trainings.dto.*;
import com.novaserve.fitness.trainings.service.impl.TrainingServiceImpl;
import java.util.stream.Collectors;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.springframework.beans.factory.annotation.Autowired;

public class RepeatOptionWithAssignmentsToTrainingDtoConverter
        implements Converter<TrainingServiceImpl.RepeatOptionWithAssignments, TrainingDto> {
    @Autowired
    ModelMapper modelMapper;

    @Override
    public TrainingDto convert(MappingContext<TrainingServiceImpl.RepeatOptionWithAssignments, TrainingDto> context) {
        TrainingServiceImpl.RepeatOptionWithAssignments source = context.getSource();
        if (source == null) {
            return null;
        }

        TrainingDto dto = modelMapper.map(source.getRepeatOption().getTraining(), TrainingDto.class);
        dto.setInstructor(modelMapper.map(source.getRepeatOption().getTraining().getInstructor(), InstructorDto.class));
        int freePlaces = source.getRepeatOption().getTraining().getTotalPlaces()
                - source.getAssignments().size();
        dto.setFreePlaces(freePlaces);

        dto.setRepeatOption(modelMapper.map(source.getRepeatOption(), RepeatOptionDto.class));
        dto.setAssignedToCustomer(source.getIsAssignedToCustomer());

        dto.setAssignments(source.getAssignments().stream()
                .map(assignment -> modelMapper.map(assignment, AssignmentCustomerDto.class))
                .collect(Collectors.toList()));

        dto.setCreatedBy(modelMapper.map(source.getRepeatOption().getTraining().getCreatedBy(), ByDto.class));
        dto.setLastModifiedBy(
                modelMapper.map(source.getRepeatOption().getTraining().getLastModifiedBy(), ByDto.class));
        return dto;
    }
}
