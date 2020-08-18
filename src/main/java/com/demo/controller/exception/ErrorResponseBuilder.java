package com.demo.controller.exception;

import com.demo.model.ErrorDetails;
import com.demo.model.ErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Component
public class ErrorResponseBuilder {
    @Autowired
    private MessageSource messageSource;

    public ErrorResponse build(List<String> errorCodes) {
        List<ErrorDetails> errorDetails = errorCodes.stream()
                .map(errorCode -> {
                    ErrorDetails error = new ErrorDetails();
                    error.setCode(messageSource.getMessage(errorCode + ".code", null, Locale.US));
                    error.setMessage(messageSource.getMessage(errorCode + ".message", null, Locale.US));
                    return error;
                }).collect(Collectors.toList());

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrors(errorDetails);
        return errorResponse;
    }
}
