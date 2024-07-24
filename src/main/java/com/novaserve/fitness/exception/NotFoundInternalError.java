/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.exception;

import org.springframework.http.HttpStatus;

public class NotFoundInternalError extends NotFound {

  public NotFoundInternalError(Class<?> resourceClass, String searchParam) {
    super(resourceClass, searchParam);
  }

  public NotFoundInternalError(Class<?> resourceClass, long searchParam) {
    super(resourceClass, searchParam);
  }

  @Override
  public String getMessage() {
    return super.getMessage();
  }

  public HttpStatus getStatus() {
    return HttpStatus.INTERNAL_SERVER_ERROR;
  }

  public int getStatusCode() {
    return HttpStatus.INTERNAL_SERVER_ERROR.value();
  }
}
