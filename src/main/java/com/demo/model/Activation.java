package com.demo.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Activation {
    private String code;
    private LocalDateTime expiration;
}
