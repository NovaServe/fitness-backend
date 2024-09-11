/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.trainings.service.impl;

import com.novaserve.fitness.auth.service.AuthUtil;
import com.novaserve.fitness.exception.ExceptionMessage;
import com.novaserve.fitness.exception.ServerException;
import com.novaserve.fitness.share.Util;
import com.novaserve.fitness.trainings.dto.DayDto;
import com.novaserve.fitness.trainings.dto.TrainingDto;
import com.novaserve.fitness.trainings.dto.TrainingsResponseDto;
import com.novaserve.fitness.trainings.model.*;
import com.novaserve.fitness.trainings.repository.TrainingCriteriaBuilder;
import com.novaserve.fitness.trainings.service.TrainingService;
import com.novaserve.fitness.trainings.service.TrainingUtil;
import com.novaserve.fitness.users.model.User;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class TrainingServiceImpl implements TrainingService {
    @Autowired
    TrainingCriteriaBuilder trainingCriteriaBuilder;

    @Autowired
    TrainingUtil trainingUtil;

    @Autowired
    AuthUtil authUtil;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public TrainingsResponseDto getTrainings(
            LocalDate startRange,
            LocalDate endRange,
            List<String> areas,
            List<Long> instructors,
            List<Intensity> intensity,
            List<Level> levels,
            List<Type> types,
            List<Kind> kinds,
            Boolean availableOnly) {

        List<Training> filteredTrainings = trainingCriteriaBuilder.getTrainings(
                startRange, endRange, areas, instructors, intensity, levels, types, kinds);

        List<DayDto> calendarDays = generateCalendarDays(startRange, endRange);

        User principal = authUtil.getUserFromAuth(
                        SecurityContextHolder.getContext().getAuthentication())
                .orElseThrow(() -> new ServerException(ExceptionMessage.UNAUTHORIZED, HttpStatus.UNAUTHORIZED));

        processTrainings(filteredTrainings, calendarDays, startRange, endRange, principal);
        TrainingsResponseDto trainingsResponseDto = new TrainingsResponseDto(startRange, endRange, calendarDays);
        return trainingsResponseDto;
    }

    /**
     * Generates calendar's every day frame from the given date range (without training dto).
     */
    private List<DayDto> generateCalendarDays(LocalDate startRange, LocalDate endRange) {
        List<DayDto> days = new ArrayList<>();
        LocalDate currentDate = startRange;
        while (!currentDate.isAfter(endRange)) {
            DayDto day = new DayDto();
            day.setDate(currentDate);
            day.setDayOfWeek(currentDate.getDayOfWeek());
            currentDate = currentDate.plusDays(1);
        }
        return days;
    }

    /**
     * Returns a list of the customers' assignments for a particular repeat option and event date.
     */
    private List<Assignment> getCustomersAssignments(
            RepeatOption repeatOption, LocalDate startRange, LocalDate endRange, LocalDate eventDate) {

        Set<LocalDate> repeatOptionExcludedDates = trainingUtil.parseExcludedDates(repeatOption.getExcludedDates());

        List<Assignment> assignments = new ArrayList<>();
        for (Assignment assignment : repeatOption.getAssignments()) {
            Set<LocalDate> assignmentExcludedDates = trainingUtil.parseExcludedDates(assignment.getExcludedDates());

            LocalDate startDate =
                    assignment.getRepeatSince().isAfter(startRange) ? assignment.getRepeatSince() : startRange;
            LocalDate endDate = assignment.getRepeatUntil() != null
                            && assignment.getRepeatUntil().isBefore(endRange)
                    ? assignment.getRepeatUntil()
                    : endRange;

            LocalDate dateCounter = startDate;
            while (!dateCounter.isAfter(endDate)) {
                if (repeatOptionExcludedDates.contains(dateCounter) || assignmentExcludedDates.contains(dateCounter)) {
                    dateCounter = dateCounter.plusDays(1);
                    continue;
                }

                if (dateCounter.equals(eventDate)) {
                    assignments.add(assignment);
                }
                dateCounter = dateCounter.plusDays(1);
            }
        }
        return assignments;
    }

    private Boolean isAssignedToCustomer(List<Assignment> assignments, User customer) {
        if (customer.isCustomer()) {
            boolean isAssignedToCustomer = assignments.stream()
                    .anyMatch(assignment -> assignment.getCustomer().equals(customer));
            return isAssignedToCustomer;
        }
        return null;
    }

    /**
     * Calculates concrete event dates for every repeat option associated with training.
     */
    private Map<LocalDate, List<RepeatOptionWithAssignments>> calculateRepeatOptionDatesFromRange(
            LocalDate startRange, LocalDate endRange, RepeatOption repeatOption, User customerPrincipal) {

        if (repeatOption.getRepeatUntil() != null
                && repeatOption.getRepeatUntil().isBefore(endRange)) {
            endRange = repeatOption.getRepeatUntil();
        }

        Set<LocalDate> excludedDates = null;
        if (repeatOption.getExcludedDates() != null) {
            excludedDates = trainingUtil.parseExcludedDates(repeatOption.getExcludedDates());
        }

        Map<LocalDate, List<RepeatOptionWithAssignments>> aggregatedRepeatOptionDates = new HashMap<>();

        LocalDate dateCounter = startRange;
        DayOfWeek targetWeekOfDay = repeatOption.getDayOfWeek();
        int repeatTimes = 0;
        if (!repeatOption.isRecurring()) {
            repeatTimes = 1;
        } else if (repeatOption.getRepeatTimes() != null) {
            repeatTimes = repeatOption.getRepeatTimes();
        }

        if (repeatTimes != 0) {
            int repeatCounter = 0;
            while (!dateCounter.isAfter(endRange) && repeatCounter < repeatTimes) {
                if (excludedDates != null && excludedDates.contains(dateCounter)) {
                    dateCounter = dateCounter.plusDays(1);
                    continue;
                }
                if (targetWeekOfDay.equals(dateCounter.getDayOfWeek())) {
                    List<Assignment> customersAssignments =
                            getCustomersAssignments(repeatOption, startRange, endRange, dateCounter);

                    Boolean isAssignedToCustomer = isAssignedToCustomer(customersAssignments, customerPrincipal);

                    RepeatOptionWithAssignments repeatOptionWithAssignments = new RepeatOptionWithAssignments(
                            repeatOption,
                            getCustomersAssignments(repeatOption, startRange, endRange, dateCounter),
                            isAssignedToCustomer);
                    aggregatedRepeatOptionDates.put(dateCounter, new ArrayList<>(List.of(repeatOptionWithAssignments)));
                    repeatCounter++;
                }
                dateCounter = dateCounter.plusDays(1);
            }
        } else {
            while (!dateCounter.isAfter(endRange)) {
                if (excludedDates != null && excludedDates.contains(dateCounter)) {
                    dateCounter = dateCounter.plusDays(1);
                    continue;
                }
                if (targetWeekOfDay.equals(dateCounter.getDayOfWeek())) {
                    List<Assignment> customersAssignments =
                            getCustomersAssignments(repeatOption, startRange, endRange, dateCounter);

                    Boolean isAssignedToCustomer = isAssignedToCustomer(customersAssignments, customerPrincipal);

                    RepeatOptionWithAssignments repeatOptionWithAssignments = new RepeatOptionWithAssignments(
                            repeatOption,
                            getCustomersAssignments(repeatOption, startRange, endRange, dateCounter),
                            isAssignedToCustomer);
                    aggregatedRepeatOptionDates.put(dateCounter, new ArrayList<>(List.of(repeatOptionWithAssignments)));
                }
                dateCounter = dateCounter.plusDays(1);
            }
        }

        return aggregatedRepeatOptionDates;
    }

    /**
     * Processes raw trainings data and ads trainings dto for every date.
     */
    private void processTrainings(
            List<Training> rawTrainings,
            List<DayDto> rawDays,
            LocalDate startRange,
            LocalDate endRange,
            User customerPrincipal) {

        Map<LocalDate, List<RepeatOptionWithAssignments>> aggregatedEventsDates = rawTrainings.stream()
                .flatMap(training -> training.getRepeatOptions().stream())
                .filter(RepeatOption::isActive)
                .flatMap(repeatOption ->
                        calculateRepeatOptionDatesFromRange(startRange, endRange, repeatOption, customerPrincipal)
                                .entrySet()
                                .stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (list1, list2) -> {
                    List<RepeatOptionWithAssignments> mergedList = new ArrayList<>(list1);
                    mergedList.addAll(list2);
                    return mergedList;
                }));

        aggregatedEventsDates.forEach((date, repeatOptionWithAssignments) -> {
            int dayDtoIndex = Util.findIndexByPredicate(
                    rawDays, dayDto -> dayDto.getDate().equals(date));
            DayDto dayDto = rawDays.get(dayDtoIndex);

            List<TrainingDto> trainingsDto = new ArrayList<>();
            for (RepeatOptionWithAssignments repeatOptionWithAssignment : repeatOptionWithAssignments) {
                TrainingDto trainingDto = modelMapper.map(repeatOptionWithAssignment, TrainingDto.class);
                trainingsDto.add(trainingDto);
            }
            trainingsDto.sort(
                    Comparator.comparing(training -> training.getRepeatOption().getStartTime()));
            dayDto.setTrainings(trainingsDto);
        });
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RepeatOptionWithAssignments {
        private RepeatOption repeatOption;

        private List<Assignment> assignments;

        private Boolean isAssignedToCustomer;
    }
}
