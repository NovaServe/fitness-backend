/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.exception;

import lombok.Getter;

@Getter
public enum ExMessage {
  INVALID_CREDENTIALS("Invalid credentials"),
  ROLES_MISMATCH("Roles mismatch");

  private final String name;

  ExMessage(String name) {
    this.name = name;
  }
}
