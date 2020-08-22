package com.demo.external.email;

import com.demo.controller.exception.EmailServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RunWith(SpringRunner.class)
@RestClientTest(EmailService.class)
public class EmailServiceTest {
    @Autowired
    private EmailService target;

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void send_whenRequestIsSuccessful() throws JsonProcessingException {
        Mail mail = new Mail();
        mail.setFrom("from@email.com");
        mail.setTo("to@email.com");
        mail.setSubject("Test Subject");
        mail.setBody("Test body");

        server.expect(requestTo("http://localhost:8080/mail"))
                .andExpect(method(HttpMethod.PUT))
                .andExpect(content().string(objectMapper.writeValueAsString(mail)))
                .andRespond(withCreatedEntity(any()));

        target.send(mail);
    }

    @Test
    public void send_whenStatusIsNot201_thenThrowEmailSenderException() throws JsonProcessingException {
        Mail mail = new Mail();
        mail.setFrom("from@email.com");
        mail.setTo("to@email.com");
        mail.setSubject("Test Subject");
        mail.setBody("Test body");

        server.expect(requestTo("http://localhost:8080/mail"))
                .andExpect(method(HttpMethod.PUT))
                .andExpect(content().string(objectMapper.writeValueAsString(mail)))
                .andRespond(withSuccess());

        assertThrows(EmailServiceException.class, () -> target.send(mail));
    }

    @Test
    public void send_whenStatusIs500_thenThrowEmailSenderException() throws JsonProcessingException {
        Mail mail = new Mail();
        mail.setFrom("from@email.com");
        mail.setTo("to@email.com");
        mail.setSubject("Test Subject");
        mail.setBody("Test body");

        server.expect(requestTo("http://localhost:8080/mail"))
                .andExpect(method(HttpMethod.PUT))
                .andExpect(content().string(objectMapper.writeValueAsString(mail)))
                .andRespond(withServerError());

        assertThrows(EmailServiceException.class, () -> target.send(mail));
    }
}
