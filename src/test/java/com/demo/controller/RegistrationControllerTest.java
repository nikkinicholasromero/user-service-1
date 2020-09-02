package com.demo.controller;

import com.demo.controller.exception.ErrorHandlerAdvice;
import com.demo.model.UserAccount;
import com.demo.orchestrator.RegistrationOrchestrator;
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

public class RegistrationControllerTest {
    private MockMvc mockMvc;

    @InjectMocks
    private RegistrationController target;

    @Mock
    private RegistrationOrchestrator orchestrator;

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
    public void register() throws Exception {
        UserAccount userAccount = new UserAccount();
        userAccount.setEmailAddress("some@email.com");
        userAccount.setPassword("somePassword");
        userAccount.setFirstName("someFirstName");
        userAccount.setLastName("someLastName");

        mockMvc.perform(post("/registration")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(userAccount)))
                .andDo(print())
                .andExpect(status().isCreated());

        verify(orchestrator, times(1)).orchestrate(eq(userAccount));
    }
}
