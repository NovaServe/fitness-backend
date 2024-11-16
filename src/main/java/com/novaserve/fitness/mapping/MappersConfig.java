/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.mapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.novaserve.fitness.trainings.converter.AssignmentToAssignmentCustomerDtoConverter;
import com.novaserve.fitness.trainings.converter.RepeatOptionToRepeatOptionDtoConverter;
import com.novaserve.fitness.trainings.converter.RepeatOptionWithAssignmentsToTrainingDtoConverter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MappersConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addConverter(new RepeatOptionToRepeatOptionDtoConverter());
        modelMapper.addConverter(new AssignmentToAssignmentCustomerDtoConverter());
        modelMapper.addConverter(new RepeatOptionWithAssignmentsToTrainingDtoConverter());
        return modelMapper;
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        return objectMapper;
    }
}
