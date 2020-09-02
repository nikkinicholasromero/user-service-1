package com.demo.controller;

import com.demo.controller.exception.ErrorHandlerAdvice;
import com.demo.model.ResetPasswordRequest;
import com.demo.orchestrator.ResetPasswordOrchestrator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ResetPasswordControllerTest {
    private MockMvc mockMvc;

    @InjectMocks
    private ResetPasswordController target;

    @Mock
    private ResetPasswordOrchestrator orchestrator;

    @Mock
    private ErrorHandlerAdvice errorHandlerAdvice;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders.standaloneSetup(target)
                .setControllerAdvice(errorHandlerAdvice)
                .build();
    }

    @Test
    public void resetPassword() throws Exception {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setEmailAddress("someEmail@address.com");
        request.setForgotPasswordCode("someForgotPasswordCode");
        request.setNewPassword("someNewPassword");

        mockMvc.perform(post("/password")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk());

        verify(orchestrator, times(1)).orchestrate(eq(request));
    }
}
