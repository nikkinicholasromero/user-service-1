package com.demo.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "USER_ACCOUNTS")
public class UserAccount {
    @Id
    @Column(name = "ID")
    private String id;

    @Email(message = "validation.email-address.format")
    @NotBlank(message = "validation.email-address.required")
    @Column(name = "EMAIL_ADDRESS")
    private String emailAddress;

    @NotBlank(message = "validation.password.required")
    @Size(min = 8, message = "validation.password.length")
    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "SALT")
    private String salt;

    @Column(name = "ACTIVATION_CODE")
    private String activationCode;

    @Column(name = "ACTIVATION_EXPIRATION")
    private LocalDateTime activationExpiration;

    @Column(name = "FORGOT_PASSWORD_CODE")
    private String forgotPasswordCode;

    @Column(name = "FORGOT_PASSWORD_EXPIRATION")
    private LocalDateTime forgotPasswordExpiration;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private EmailAddressStatus status;

    @NotBlank(message = "validation.first-name.required")
    @Column(name = "FIRST_NAME")
    private String firstName;

    @NotBlank(message = "validation.last-name.required")
    @Column(name = "LAST_NAME")
    private String lastName;
}
