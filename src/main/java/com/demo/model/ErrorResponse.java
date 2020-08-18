package com.demo.model;

import lombok.Data;

import java.util.List;

@Data
public class ErrorResponse {
    private List<ErrorDetails> errors;
}
