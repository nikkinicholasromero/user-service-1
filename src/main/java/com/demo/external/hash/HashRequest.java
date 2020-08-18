package com.demo.external.hash;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HashRequest {
    private final String clear;
    private final String salt;
}
