package com.novaserve.fitness.exception;

public enum ErrorMessage {
    AUTHENTICATION_ERROR("Authentication error"),
    INVALID_TOKEN("Invalid token"),
    ALREADY_EXISTS("Already exists"),
    USERNAME_ALREADY_EXISTS("Username already exists"),
    EMAIL_ALREADY_EXISTS("Email already exists"),
    TITLE_DUPLICATE("Entity with this title/name already exists"),
    INCORRECT_TIME("The specified date or time is incorrect"),
    ROLE_NOT_FOUND("Role not found"),
    NOT_FOUND("Not found"),
    EMPTY_REQUEST("Empty request"),
    NO_UPDATES_REQUEST("You have sent the request with no updates"),
    TITLE_IS_NOT_DIFFERENT("Title is not different"),
    DESCRIPTION_IS_NOT_DIFFERENT("Description is not different"),
    USERNAME_IS_NOT_DIFFERENT("Username is not different"),
    EMAIL_IS_NOT_DIFFERENT("Email is not different"),
    FULL_NAME_IS_NOT_DIFFERENT("Full name is not different"),
    AGE_IS_NOT_DIFFERENT("Age is not different"),
    PASSWORD_IS_NOT_DIFFERENT("Password is not different"),
    FIELDS_VALUES_ARE_NOT_DIFFERENT(
            "Existent values of the following fields don't differ from the updated values you have sent: "),
    INTERNAL_SERVER_ERROR("Internal server error");

    private final String name;

    ErrorMessage(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
