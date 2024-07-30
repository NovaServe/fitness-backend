/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExceptionMessage {
    INVALID_CREDENTIALS("Invalid credentials"),

    INVALID_TOKEN("Invalid token"),

    UNAUTHORIZED("Unauthorized"),

    ROLES_MISMATCH("Roles mismatch");

    private final String name;
}
