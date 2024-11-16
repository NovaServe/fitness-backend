/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.exceptions;

import org.springframework.http.HttpStatus;

public class UserInactiveException extends RuntimeException {
    public UserInactiveException() {
        super(ExceptionMessage.USER_INACTIVE.getName());
    }

    public HttpStatus getStatus() {
        return HttpStatus.FORBIDDEN;
    }

    public int getStatusCode() {
        return HttpStatus.FORBIDDEN.value();
    }
}
