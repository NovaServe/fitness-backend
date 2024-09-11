/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.trainings.service;

import java.time.LocalDate;
import java.util.Set;

public interface TrainingUtil {
    Set<LocalDate> parseExcludedDates(String excludedDates);
}
