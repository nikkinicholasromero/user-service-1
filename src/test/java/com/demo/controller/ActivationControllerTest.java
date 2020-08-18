package com.demo.controller;

import com.demo.controller.exception.ErrorHandlerAdvice;
import com.demo.orchestrator.ActivationOrchestrator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ActivationControllerTest {
    private MockMvc mockMvc;

    @InjectMocks
    private ActivationController target;

    @Mock
    private ActivationOrchestrator activationOrchestrator;

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
    public void activate() throws Exception {
        mockMvc.perform(put("/activation/some@email.com?activationCode=abc123"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(activationOrchestrator, times(1)).orchestrate("some@email.com", "abc123");
    }
}
