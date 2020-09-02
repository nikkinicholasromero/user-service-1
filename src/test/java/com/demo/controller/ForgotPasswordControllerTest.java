package com.demo.controller;

import com.demo.controller.exception.ErrorHandlerAdvice;
import com.demo.orchestrator.ForgotPasswordOrchestrator;
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

public class ForgotPasswordControllerTest {
    private MockMvc mockMvc;

    @InjectMocks
    private ForgotPasswordController target;

    @Mock
    private ForgotPasswordOrchestrator orchestrator;

    @Mock
    private ErrorHandlerAdvice errorHandlerAdvice;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(target)
                .setControllerAdvice(errorHandlerAdvice)
                .build();
    }

    @Test
    public void sendForgotPasswordLink() throws Exception {
        mockMvc.perform(put("/forgotPassword/some@email.com"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(orchestrator, times(1)).orchestrate("some@email.com");
    }
}
