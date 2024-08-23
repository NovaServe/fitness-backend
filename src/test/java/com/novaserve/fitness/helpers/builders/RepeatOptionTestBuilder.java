/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.helpers.builders;

import com.novaserve.fitness.helpers.DbHelper;
import com.novaserve.fitness.trainings.model.RepeatOption;
import com.novaserve.fitness.trainings.model.Training;
import java.sql.Time;
import java.time.DayOfWeek;
import java.time.LocalDate;

public class RepeatOptionTestBuilder<T> {
    private Training training;
    private DayOfWeek dayOfWeek;
    private Time startTime;
    private Time endTime;
    private LocalDate repeatSince;
    private LocalDate repeatUntil;
    private Integer repeatTimes;
    private Boolean isAutoupdate;
    private boolean isActive;
    private String excludedDates;
    private LocalDate deactivatedSince;
    private T callerOrInstance;

    public RepeatOptionTestBuilder() {}

    public RepeatOptionTestBuilder(T callerOrInstance) {
        this.callerOrInstance = callerOrInstance;
    }

    public RepeatOptionTestBuilder<T> training(Training training) {
        this.training = training;
        return this;
    }

    public RepeatOptionTestBuilder<T> dayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
        return this;
    }

    /**
     * @param startTime "hh:mm:ss"
     */
    public RepeatOptionTestBuilder<T> startTime(String startTime) {
        this.startTime = Time.valueOf(startTime);
        return this;
    }

    /**
     * @param endTime "hh:mm:ss"
     */
    public RepeatOptionTestBuilder<T> endTime(String endTime) {
        this.endTime = Time.valueOf(endTime);
        return this;
    }

    /**
     * @param repeatSince "YYYY-MM-DD"
     */
    public RepeatOptionTestBuilder<T> repeatSince(String repeatSince) {
        this.repeatSince = LocalDate.parse(repeatSince);
        return this;
    }

    /**
     * @param repeatUntil "YYYY-MM-DD"
     */
    public RepeatOptionTestBuilder<T> repeatUntil(String repeatUntil) {
        this.repeatUntil = LocalDate.parse(repeatUntil);
        return this;
    }

    public RepeatOptionTestBuilder<T> repeatTimes(Integer repeatTimes) {
        this.repeatTimes = repeatTimes;
        return this;
    }

    public RepeatOptionTestBuilder<T> autoupdate(boolean autoupdate) {
        isAutoupdate = autoupdate;
        return this;
    }

    public RepeatOptionTestBuilder<T> active(boolean active) {
        isActive = active;
        return this;
    }

    /**
     * @param excludedDates "YYYY-MM-DD;YYYY-MM-DD;..."
     */
    public RepeatOptionTestBuilder<T> excludedDates(String excludedDates) {
        this.excludedDates = excludedDates;
        return this;
    }

    /**
     * @param deactivatedSince "YYYY-MM-DD"
     */
    public RepeatOptionTestBuilder<T> deactivatedSince(String deactivatedSince) {
        this.deactivatedSince = LocalDate.parse(deactivatedSince);
        return this;
    }

    private RepeatOption instance() {
        return RepeatOption.builder()
                .training(training)
                .dayOfWeek(dayOfWeek == null ? DayOfWeek.MONDAY : dayOfWeek)
                .startTime(startTime == null ? Time.valueOf("10:00:00") : startTime)
                .endTime(startTime == null ? Time.valueOf("11:30:00") : endTime)
                .repeatSince(repeatSince == null ? LocalDate.now().minusDays(1) : repeatSince)
                .repeatUntil(repeatUntil)
                .repeatTimes(repeatTimes)
                .isAutoupdate(isAutoupdate == null || isAutoupdate)
                .excludedDates(excludedDates)
                .deactivationSince(deactivatedSince)
                .build();
    }

    public T build() {
        if (callerOrInstance instanceof DbHelper) {
            ((DbHelper) callerOrInstance).setRepeatOptionInstance(instance());
            return callerOrInstance;
        } else {
            return (T) instance();
        }
    }
}
