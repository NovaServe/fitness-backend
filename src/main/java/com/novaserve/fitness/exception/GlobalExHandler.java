/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.exception;

import static java.util.Objects.nonNull;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExHandler extends ResponseEntityExceptionHandler {
  private static final Logger logger = LoggerFactory.getLogger(GlobalExHandler.class);

  @ExceptionHandler(ServerEx.class)
  public ResponseEntity<ExDto> handleApiException(ServerEx e, WebRequest req) {
    logger.error("{}, {}, {}", req.getDescription(true), e.getStatus(), e.getStackTrace());
    ExDto dto = new ExDto(e.getMessage());
    return new ResponseEntity<>(dto, e.getStatus());
  }

  @ExceptionHandler(NotFound.class)
  public ResponseEntity<?> handleResourceNotFound(NotFound e, WebRequest req) {
    logger.error("{}, {}, {}", req.getDescription(true), e.getStatusCode(), e.getStackTrace());
    return new ResponseEntity<>(e.getStatus());
  }

  @ExceptionHandler(NotFoundInternalErr.class)
  public ResponseEntity<ExDto> handleResourceNotFoundInternalError(
      NotFoundInternalErr e, WebRequest req) {
    logger.error("{}, {}, {}", req.getDescription(true), e.getStatusCode(), e.getStackTrace());
    return new ResponseEntity<>(e.getStatus());
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Map<String, String>> handleConstraintViolationException(
      ConstraintViolationException e) {
    Map<String, String> validationResult = new HashMap<>();
    for (ConstraintViolation<?> constraintViolation : e.getConstraintViolations()) {
      String constraintMessage = constraintViolation.getMessage();
      String paramName = constraintViolation.getPropertyPath().toString().split("\\.")[1];
      validationResult.put(paramName, constraintMessage);
    }
    return new ResponseEntity<>(validationResult, HttpStatus.BAD_REQUEST);
  }

  /**
   * @see FieldError
   * @see org.springframework.validation.ObjectError
   * @see SpringValidatorAdapter
   */
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException e, HttpHeaders headers, HttpStatusCode code, WebRequest req) {
    Map<String, String> errors = new HashMap<>();
    e.getBindingResult()
        .getAllErrors()
        .forEach(
            error -> {
              String fieldName = "Method argument not valid";
              if (error instanceof FieldError) {
                fieldName = ((FieldError) error).getField();
              } else {
                if (nonNull(error.getArguments()) && error.getArguments().length > 1) {
                  StringBuilder stringBuilder = new StringBuilder();
                  for (int i = 1; i < error.getArguments().length; i++) {
                    stringBuilder.append(error.getArguments()[i].toString());
                    stringBuilder.append(",");
                  }
                  stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                  fieldName = stringBuilder.toString();
                }
              }
              String message = error.getDefaultMessage();
              errors.put(fieldName, message);
              logger.error("{}: {} - {}", fieldName, message, HttpStatus.BAD_REQUEST.value());
            });
    return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
  }
}
