package com.demo.controller;

import com.demo.orchestrator.RegistrationOrchestrator;
import com.demo.model.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/registration")
public class RegistrationController {
    @Autowired
    private RegistrationOrchestrator orchestrator;

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestBody @Valid UserAccount userAccount) {
        orchestrator.orchestrate(userAccount);
    }
}
