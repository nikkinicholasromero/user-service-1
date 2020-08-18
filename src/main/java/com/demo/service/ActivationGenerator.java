package com.demo.service;

import com.demo.model.Activation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ActivationGenerator {
    @Autowired
    private UuidGenerator uuidGenerator;

    @Value("${activation.hours-valid}")
    private int hoursValid;

    public Activation generateActivation() {
        Activation activation = new Activation();
        activation.setCode(uuidGenerator.generateRandomUuid());
        activation.setExpiration(LocalDateTime.now().plusHours(hoursValid));
        return activation;
    }
}
