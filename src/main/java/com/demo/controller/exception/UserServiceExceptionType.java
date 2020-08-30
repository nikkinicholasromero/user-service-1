package com.demo.controller.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum UserServiceExceptionType {
    EMAIL_ADDRESS_ACTIVATION_CODE_INCORRECT_EXCEPTION("email-address.activation-code-incorrect", HttpStatus.BAD_REQUEST),
    EMAIL_ADDRESS_ACTIVATION_EXPIRED_EXCEPTION("email-address.activation-expired", HttpStatus.BAD_REQUEST),
    EMAIL_ADDRESS_DOES_NOT_EXIST_EXCEPTION("email-address.does-not-exist", HttpStatus.BAD_REQUEST),
    EMAIL_ADDRESS_IS_ALREADY_TAKEN_EXCEPTION("email-address.already-taken", HttpStatus.BAD_REQUEST),
    EMAIL_ADDRESS_IS_ALREADY_ACTIVATED_EXCEPTION("email-address.already-activated", HttpStatus.BAD_REQUEST),
    EMAIL_SERVICE_IS_UNAVAILABLE("email-service.unavailable", HttpStatus.SERVICE_UNAVAILABLE),
    EMAIL_ADDRESS_FORGOT_PASSWORD_CODE_INCORRECT_EXCEPTION("email-address.forgot-password-code-incorrect", HttpStatus.BAD_REQUEST),
    EMAIL_ADDRESS_FORGOT_PASSWORD_EXPIRED_EXCEPTION("email-address.forgot-password-expired", HttpStatus.BAD_REQUEST);

    private final String code;
    private final HttpStatus httpStatus;

    UserServiceExceptionType(String code, HttpStatus httpStatus) {
        this.code = code;
        this.httpStatus = httpStatus;
    }
}
