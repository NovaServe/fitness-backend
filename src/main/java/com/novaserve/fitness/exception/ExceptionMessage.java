/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.exception;

public enum ExceptionMessage {
    INVALID_CREDENTIALS("Invalid credentials"),

    ALREADY_EXISTS("Already exists"),

    INVALID_TOKEN("Invalid token"),

    UNAUTHORIZED("Unauthorized"),

    ROLES_MISMATCH("Roles mismatch"),
    ILLEGAL_DATE_RANGE("Illegal date range");

    private final String name;

    ExceptionMessage(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
