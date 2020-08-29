package com.demo.controller.exception;

import lombok.Getter;

@Getter
public class UserServiceException extends RuntimeException {
    private final UserServiceExceptionType type;

    public UserServiceException(UserServiceExceptionType type) {
        this.type = type;
    }
}
