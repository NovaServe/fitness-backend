/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.share;

public enum CustomDateTime {
    SERVER_TIMEZONE("Europe/Kyiv"),
    DATE_FORMAT("yyyy-MM-dd");

    private String value;

    CustomDateTime(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
