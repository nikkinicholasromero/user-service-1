package com.demo.external.email;

import com.demo.controller.exception.EmailServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EmailService {
    private final RestTemplate restTemplate;

    @Value("${email-service.send.host}")
    private String host;

    @Value("${email-service.send.endpoint}")
    private String endpoint;

    public EmailService(RestTemplateBuilder restTemplateBuilder) {
        restTemplate = restTemplateBuilder.build();
    }

    public void send(Mail mail) {
        try {
            HttpEntity<Mail> httpEntity = new HttpEntity<>(mail);
            ResponseEntity<Object> responseEntity = restTemplate.exchange(host + endpoint, HttpMethod.PUT, httpEntity, Object.class);
            if (HttpStatus.CREATED != responseEntity.getStatusCode()) {
                throw new EmailServiceException();
            }
        } catch (Exception e) {
            throw new EmailServiceException();
        }
    }
}
