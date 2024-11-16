/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.exceptions;

public enum ExceptionMessage {
    INVALID_CREDENTIALS("Invalid credentials"),

    ALREADY_EXISTS("Already exists"),

    USER_ALREADY_EXISTS("User already exists"),

    USER_NOT_FOUND("User not found"),

    INVALID_TOKEN("Invalid token"),

    UNAUTHORIZED("Unauthorized"),

    ROLES_MISMATCH("Roles mismatch"),

    INVALID_DTO_TYPE("Invalid DTO type"),

    NO_STRATEGY_FOUND("No strategy found for role: "),

    USER_INACTIVE("User is inactive and cannot perform this action");

    private final String name;

    ExceptionMessage(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
