package com.demo.controller.exception;

import com.demo.model.ErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class ErrorHandlerAdvice {
    @Autowired
    private ErrorResponseBuilder errorResponseBuilder;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleException(MethodArgumentNotValidException e) {
        List<String> errorCodes = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        return buildResponseEntity(errorCodes, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleException(ConstraintViolationException e) {
        List<String> errorCodes = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());

        return buildResponseEntity(errorCodes, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserServiceException.class)
    public ResponseEntity<ErrorResponse> handleException(UserServiceException e) {
        UserServiceExceptionType type = e.getType();
        return buildResponseEntity(Collections.singletonList(type.getCode()), type.getHttpStatus());
    }

    private ResponseEntity<ErrorResponse> buildResponseEntity(List<String> errorsString, HttpStatus httpStatus) {
        ErrorResponse errorResponse = errorResponseBuilder.build(errorsString);
        return new ResponseEntity<>(errorResponse, httpStatus);
    }
}
