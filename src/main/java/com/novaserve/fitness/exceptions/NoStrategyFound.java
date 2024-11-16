/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.exceptions;

import org.springframework.http.HttpStatus;

public class NoStrategyFound extends IllegalStateException {
    public NoStrategyFound(String message) {
        super(ExceptionMessage.NO_STRATEGY_FOUND.getName() + message);
    }

    public HttpStatus getStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public int getStatusCode() {
        return HttpStatus.INTERNAL_SERVER_ERROR.value();
    }
}
