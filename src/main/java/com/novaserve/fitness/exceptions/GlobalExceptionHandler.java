/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.exceptions;

import jakarta.validation.ConstraintViolationException;
import java.util.Arrays;
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
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ServerException.class)
    public ResponseEntity<ExceptionDto> handleServerException(ServerException ex, WebRequest webRequest) {
        logger.error("{}, {}, {}", webRequest.getDescription(true), ex.getStatus(), ex.getStackTrace());
        ExceptionDto exceptionDto = new ExceptionDto(ex.getMessage());
        return new ResponseEntity<>(exceptionDto, ex.getStatus());
    }

    @ExceptionHandler(NotFound.class)
    public ResponseEntity<?> handleNotFound(NotFound ex, WebRequest webRequest) {
        logger.error("{}, {}, {}", webRequest.getDescription(true), ex.getStatusCode(), ex.getStackTrace());
        ExceptionDto exceptionDto = new ExceptionDto(ex.getMessage());
        return new ResponseEntity<>(exceptionDto, ex.getStatus());
    }

    @ExceptionHandler(NotFoundInternalError.class)
    public ResponseEntity<ExceptionDto> handleNotFoundInternalError(NotFoundInternalError ex, WebRequest webRequest) {
        logger.error("{}, {}, {}", webRequest.getDescription(true), ex.getStatusCode(), ex.getStackTrace());
        ExceptionDto exceptionDto = new ExceptionDto(ex.getMessage());
        return new ResponseEntity<>(exceptionDto, ex.getStatus());
    }

    @ExceptionHandler(UserAlreadyExists.class)
    public ResponseEntity<?> handleUserAlreadyExists(UserAlreadyExists ex, WebRequest webRequest) {
        logger.error("{}, {}, {}", webRequest.getDescription(true), ex.getStatusCode(), ex.getStackTrace());
        ExceptionDto exceptionDto = new ExceptionDto(ex.getMessage());
        return new ResponseEntity<>(exceptionDto, ex.getStatus());
    }

    @ExceptionHandler(InvalidDtoType.class)
    public ResponseEntity<?> handleInvalidDtoType(InvalidDtoType ex, WebRequest webRequest) {
        logger.error("{}, {}, {}", webRequest.getDescription(true), ex.getStatusCode(), ex.getStackTrace());
        ExceptionDto exceptionDto = new ExceptionDto(ex.getMessage());
        return new ResponseEntity<>(exceptionDto, ex.getStatus());
    }

    @ExceptionHandler(NoStrategyFound.class)
    public ResponseEntity<?> handleNoStrategyFound(NoStrategyFound ex, WebRequest webRequest) {
        logger.error("{}, {}, {}", webRequest.getDescription(true), ex.getStatusCode(), ex.getStackTrace());
        ExceptionDto exceptionDto = new ExceptionDto(ex.getMessage());
        return new ResponseEntity<>(exceptionDto, ex.getStatus());
    }

    @ExceptionHandler(UserInactiveException.class)
    public ResponseEntity<?> handleUserInactiveException(UserInactiveException ex, WebRequest webRequest) {
        logger.error("{}, {}, {}", webRequest.getDescription(true), ex.getStatusCode(), ex.getStackTrace());
        ExceptionDto exceptionDto = new ExceptionDto(ex.getMessage());
        return new ResponseEntity<>(exceptionDto, ex.getStatus());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolationException(
            ConstraintViolationException constraintViolationException) {
        Map<String, String> validation = new HashMap<>();

        constraintViolationException.getConstraintViolations().forEach((elt) -> {
            String constraintMessage = elt.getMessage();
            String paramName = elt.getPropertyPath().toString().split("\\.")[1];
            validation.put(paramName, constraintMessage);
        });

        return new ResponseEntity<>(validation, HttpStatus.BAD_REQUEST);
    }

    /**
     * @see FieldError
     * @see org.springframework.validation.ObjectError
     * @see SpringValidatorAdapter
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException methodArgumentNotValidException,
            HttpHeaders httpHeaders,
            HttpStatusCode httpStatusCode,
            WebRequest webRequest) {
        Map<String, String> errors = new HashMap<>();

        methodArgumentNotValidException.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = "Method argument not valid";
            if (error instanceof FieldError) {
                fieldName = ((FieldError) error).getField();
            } else {
                if (error.getArguments() != null && error.getArguments().length > 1) {
                    StringBuilder stringBuilder = new StringBuilder();
                    Arrays.stream(error.getArguments()).forEach(elt -> {
                        stringBuilder.append(elt.toString());
                        stringBuilder.append(",");
                    });
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
