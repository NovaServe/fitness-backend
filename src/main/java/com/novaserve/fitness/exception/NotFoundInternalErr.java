/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.exception;

import org.springframework.http.HttpStatus;

public class NotFoundInternalErr extends NotFound {
  public NotFoundInternalErr(Class<?> resourceClass, String searchParam) {
    super(resourceClass, searchParam);
  }

  public NotFoundInternalErr(Class<?> resourceClass, long searchParam) {
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
