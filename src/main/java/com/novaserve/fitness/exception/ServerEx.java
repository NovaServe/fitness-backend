/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class ServerEx extends RuntimeException {
  protected ExMessage message;
  @Getter protected HttpStatus status;

  public ServerEx(ExMessage message, HttpStatus status) {
    super(message.getName());
    this.message = message;
    this.status = status;
  }

  public int getStatusCode() {
    return status.value();
  }
}
