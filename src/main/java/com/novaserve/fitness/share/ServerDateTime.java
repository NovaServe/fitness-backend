/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.share;

public enum ServerDateTime {
    SERVER_TIMEZONE("Europe/Kyiv"),
    DATE_FORMAT("yyyy-MM-dd");

    private String value;

    ServerDateTime(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
