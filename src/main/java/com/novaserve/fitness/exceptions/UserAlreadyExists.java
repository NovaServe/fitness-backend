/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.exceptions;

import org.springframework.http.HttpStatus;

public class UserAlreadyExists extends RuntimeException {
    public UserAlreadyExists() {
        super(ExceptionMessage.USER_ALREADY_EXISTS.getName());
    }

    public HttpStatus getStatus() {
        return HttpStatus.CONFLICT;
    }

    public int getStatusCode() {
        return HttpStatus.CONFLICT.value();
    }
}
