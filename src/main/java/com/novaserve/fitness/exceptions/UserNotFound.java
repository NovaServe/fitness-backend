/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.exceptions;

import org.springframework.http.HttpStatus;

public class UserNotFound extends RuntimeException {
    public UserNotFound() {
        super(ExceptionMessage.USER_NOT_FOUND.getName());
    }

    public HttpStatus getStatus() {
        return HttpStatus.NOT_FOUND;
    }

    public int getStatusCode() {
        return HttpStatus.NOT_FOUND.value();
    }
}
