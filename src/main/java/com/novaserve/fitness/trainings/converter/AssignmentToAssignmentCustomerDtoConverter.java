/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.trainings.converter;

import com.novaserve.fitness.trainings.dto.AssignmentCustomerDto;
import com.novaserve.fitness.trainings.model.Assignment;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

public class AssignmentToAssignmentCustomerDtoConverter implements Converter<Assignment, AssignmentCustomerDto> {
    @Override
    public AssignmentCustomerDto convert(MappingContext<Assignment, AssignmentCustomerDto> context) {
        Assignment source = context.getSource();
        if (source == null) {
            return null;
        }
        AssignmentCustomerDto dto = new AssignmentCustomerDto();
        dto.setAssignmentId(source.getId());
        dto.setRepeatSince(source.getRepeatSince());
        dto.setRepeatUntil(source.getRepeatUntil());
        dto.setRepeatTimes(source.getRepeatTimes());
        dto.setRecurring(source.isRecurring());
        dto.setCustomerId(source.getCustomer().getId());
        dto.setFullName(source.getCustomer().getFullName());
        dto.setUsername(source.getCustomer().getUsername());
        return dto;
    }
}
