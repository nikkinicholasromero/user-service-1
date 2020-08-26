package com.demo.controller.exception;

public class ForgotPasswordException extends RuntimeException {
    public final static String CODE = "email-address.does-not-exist";
}
