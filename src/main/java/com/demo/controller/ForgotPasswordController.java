package com.demo.controller;

import com.demo.orchestrator.ForgotPasswordOrchestrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Email;

@CrossOrigin
@RestController
@RequestMapping("/forgotPassword")
public class ForgotPasswordController {
    @Autowired
    private ForgotPasswordOrchestrator forgotPasswordOrchestrator;

    @PutMapping("/{emailAddress:.+}")
    public void sendForgotPasswordLink(
            @PathVariable("emailAddress") @Email(message = "validation.email-address.format") String emailAddress) {
        forgotPasswordOrchestrator.orchestrate(emailAddress);
    }
}
