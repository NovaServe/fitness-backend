/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.exception;

import org.springframework.http.HttpStatus;

public class NotFound extends RuntimeException {
    public NotFound(Class<?> resourceClass, String searchParam) {
        super(String.format("Resource %s with search param %s not found", resourceClass.getName(), searchParam));
    }

    public NotFound(Class<?> resourceClass, long searchParam) {
        super(String.format("Resource %s with search param %d not found", resourceClass.getName(), searchParam));
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }

    public HttpStatus getStatus() {
        return HttpStatus.NOT_FOUND;
    }

    public int getStatusCode() {
        return HttpStatus.NOT_FOUND.value();
    }
}
