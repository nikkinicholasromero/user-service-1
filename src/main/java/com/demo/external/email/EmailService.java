package com.demo.external.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
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
        HttpEntity<Mail> httpEntity = new HttpEntity<>(mail);
        restTemplate.put(host + endpoint, httpEntity);
    }
}
