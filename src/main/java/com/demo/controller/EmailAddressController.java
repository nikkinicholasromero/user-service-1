package com.demo.controller;

import com.demo.service.EmailAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Email;

@CrossOrigin
@RestController
@RequestMapping("/emailAddress")
@Validated
public class EmailAddressController {
    @Autowired
    private EmailAddressService emailAddressService;

    @GetMapping("/{emailAddress:.+}")
    public ResponseEntity<String> getEmailAddressStatus(
            @PathVariable("emailAddress") @Email(message = "validation.email-address.format") String emailAddress) {
        return new ResponseEntity<>(emailAddressService.getEmailAddressStatus(emailAddress).name(), HttpStatus.OK);
    }
}
