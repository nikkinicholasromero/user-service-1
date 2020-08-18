package com.demo.controller;

import com.demo.model.UserAccount;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("mock")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class RegistrationControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void registerUserAccount() throws Exception {
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
    }
}
