package com.demo.service;

import com.demo.model.ForgotPassword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ForgotPasswordGenerator {
    @Autowired
    private UuidGenerator uuidGenerator;

    @Value("${forgot-password.hours-valid}")
    private int hoursValid;

    public ForgotPassword generateForgotPassword() {
        ForgotPassword forgotPassword = new ForgotPassword();
        forgotPassword.setCode(uuidGenerator.generateRandomUuid());
        forgotPassword.setExpiration(LocalDateTime.now().plusHours(hoursValid));
        return forgotPassword;
    }
}
