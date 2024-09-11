/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.trainings.service.impl;

import com.novaserve.fitness.share.CustomDateTime;
import com.novaserve.fitness.trainings.service.TrainingUtil;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class TrainingUtilImpl implements TrainingUtil {
    @Override
    public Set<LocalDate> parseExcludedDates(String excludedDates) {
        String[] excludedDatesArr = excludedDates.split(";");
        Set<LocalDate> dates = new HashSet<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CustomDateTime.DATE_FORMAT.name());
        for (String dateString : excludedDatesArr) {
            dates.add(LocalDate.parse(dateString, formatter));
        }
        return dates;
    }
}
