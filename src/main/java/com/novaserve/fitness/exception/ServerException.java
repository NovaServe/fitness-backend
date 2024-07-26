/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class ServerException extends RuntimeException {
    protected ExceptionMessage message;

    @Getter
    protected HttpStatus status;

    public ServerException(ExceptionMessage message, HttpStatus status) {
        super(message.getName());
        this.message = message;
        this.status = status;
    }

    public int getStatusCode() {
        return status.value();
    }
}
