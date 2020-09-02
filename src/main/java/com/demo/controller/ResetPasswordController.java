package com.demo.controller;

import com.demo.model.ResetPasswordRequest;
import com.demo.orchestrator.ResetPasswordOrchestrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/password")
public class ResetPasswordController {
    @Autowired
    private ResetPasswordOrchestrator orchestrator;

    @PostMapping("")
    public void resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        orchestrator.orchestrate(request);
    }
}
