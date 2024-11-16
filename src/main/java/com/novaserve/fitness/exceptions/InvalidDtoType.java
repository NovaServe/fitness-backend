/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidDtoType extends IllegalArgumentException {
    public InvalidDtoType() {
        super(ExceptionMessage.INVALID_DTO_TYPE.getName());
    }

    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }
}
