package com.demo.controller;

import com.demo.orchestrator.ActivationOrchestrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Email;

@CrossOrigin
@RestController
@RequestMapping("/activation")
public class ActivationController {
    @Autowired
    private ActivationOrchestrator activationOrchestrator;

    @PutMapping("/{emailAddress:.+}")
    public void activate(
            @PathVariable("emailAddress") @Email(message = "validation.email-address.format") String emailAddress,
            @RequestParam("activationCode") String activationCode) {
        activationOrchestrator.orchestrate(emailAddress, activationCode);
    }
}
