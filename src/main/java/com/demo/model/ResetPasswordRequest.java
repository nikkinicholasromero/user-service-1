package com.demo.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class ResetPasswordRequest {
    @Email(message = "validation.email-address.format")
    @NotBlank(message = "validation.email-address.required")
    private String emailAddress;

    @NotBlank(message = "validation.forgot-password-code.required")
    private String forgotPasswordCode;

    @NotBlank(message = "validation.password.required")
    @Size(min = 8, message = "validation.password.length")
    private String newPassword;
}
