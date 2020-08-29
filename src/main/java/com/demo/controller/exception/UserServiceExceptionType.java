package com.demo.controller.exception;

import lombok.Getter;

@Getter
public enum UserServiceExceptionType {
    EMAIL_ADDRESS_ACTIVATION_CODE_INCORRECT_EXCEPTION("email-address.activation-code-incorrect"),
    EMAIL_ADDRESS_ACTIVATION_EXPIRED_EXCEPTION("email-address.activation-expired"),
    EMAIL_ADDRESS_DOES_NOT_EXIST_EXCEPTION("email-address.does-not-exist"),
    EMAIL_ADDRESS_IS_ALREADY_TAKEN_EXCEPTION("email-address.already-taken"),
    EMAIL_ADDRESS_IS_ALREADY_ACTIVATED_EXCEPTION("email-address.already-activated"),
    EMAIL_SERVICE_IS_UNAVAILABLE("email-service.unavailable");

    private final String code;

    UserServiceExceptionType(String code) {
        this.code = code;
    }
}
