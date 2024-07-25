/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.exception;

import lombok.Getter;

@Getter
public enum ExceptionMessage {
  INVALID_CREDENTIALS("Invalid credentials"),
  ROLES_MISMATCH("Roles mismatch");

  private final String name;

  ExceptionMessage(String name) {
    this.name = name;
  }
}
