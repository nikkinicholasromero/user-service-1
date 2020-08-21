package com.demo.controller.exception;

import lombok.Getter;

@Getter
public enum UserRegistrationExceptionType {
    EMAIL_ADDRESS_ACTIVATION_CODE_INCORRECT_EXCEPTION("email-address.activation-code-incorrect"),
    EMAIL_ADDRESS_ACTIVATION_EXPIRED_EXCEPTION("email-address.activation-expired"),
    EMAIL_ADDRESS_DOES_NOT_EXIST_EXCEPTION("email-address.does-not-exist"),
    EMAIL_ADDRESS_IS_ALREADY_TAKEN_EXCEPTION("email-address.already-taken"),
    EMAIL_ADDRESS_IS_NOT_DUE_FOR_ACTIVATION_EXCEPTION("email-address.not-activation-due");

    private final String code;

    UserRegistrationExceptionType(String code) {
        this.code = code;
    }
}
