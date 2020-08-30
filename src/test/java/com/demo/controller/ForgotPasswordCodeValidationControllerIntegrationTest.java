package com.demo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("mock")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ForgotPasswordCodeValidationControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void validateForgotPasswordCode() throws Exception {
        mockMvc.perform(get("/forgotPassword/registered@email.com?forgotPasswordCode=registeredForgotPasswordCode"))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
