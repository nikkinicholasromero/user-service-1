package com.demo.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ForgotPassword {
    private String code;
    private LocalDateTime expiration;
}
