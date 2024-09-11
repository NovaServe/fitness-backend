/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.trainings.converter;

import com.novaserve.fitness.trainings.dto.response.RepeatOptionDto;
import com.novaserve.fitness.trainings.model.RepeatOption;
import com.novaserve.fitness.trainings.service.TrainingUtil;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.beans.factory.annotation.Autowired;

public class RepeatOptionToRepeatOptionDtoConverter implements Converter<RepeatOption, RepeatOptionDto> {
    @Autowired
    TrainingUtil trainingUtil;

    @Override
    public RepeatOptionDto convert(MappingContext<RepeatOption, RepeatOptionDto> context) {
        RepeatOption source = context.getSource();
        if (source == null) {
            return null;
        }
        RepeatOptionDto dto = new RepeatOptionDto();
        dto.setId(source.getId());
        dto.setDayOfWeek(source.getDayOfWeek());
        dto.setStartTime(source.getStartTime());
        dto.setEndTime(source.getEndTime());
        dto.setRecurring(source.isRecurring());
        dto.setRepeatSince(source.getRepeatSince());
        dto.setRepeatUntil(source.getRepeatUntil());
        dto.setRepeatTimes(source.getRepeatTimes());

        List<LocalDate> excludedDates = new ArrayList<>(trainingUtil.parseExcludedDates(source.getExcludedDates()));
        dto.setExcludedDates(excludedDates);
        dto.setActive(source.isActive());
        return dto;
    }
}
