package com.demo.controller;

import com.demo.orchestrator.ForgotPasswordCodeValidationOrchestrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Email;

@CrossOrigin
@RestController
@RequestMapping("/forgotPassword")
public class ForgotPasswordCodeValidationController {
    @Autowired
    private ForgotPasswordCodeValidationOrchestrator orchestrator;

    @GetMapping("/{emailAddress:.+}")
    public void validateForgotPasswordCode(
            @PathVariable("emailAddress") @Email(message = "validation.email-address.format") String emailAddress,
            @RequestParam("forgotPasswordCode") String forgotPasswordCode) {
        orchestrator.orchestrate(emailAddress, forgotPasswordCode);
    }
}
