package com.demo.controller.exception;

import lombok.Getter;

@Getter
public class UserRegistrationException extends RuntimeException {
    private final UserRegistrationExceptionType type;

    public UserRegistrationException(UserRegistrationExceptionType type) {
        this.type = type;
    }
}
