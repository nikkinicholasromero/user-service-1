package com.demo.controller;

import com.demo.controller.exception.ErrorHandlerAdvice;
import com.demo.model.EmailAddressStatus;
import com.demo.service.EmailAddressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class EmailAddressControllerTest {
    private MockMvc mockMvc;

    @InjectMocks
    private EmailAddressController target;

    @Mock
    private EmailAddressService emailAddressService;

    @Mock
    private ErrorHandlerAdvice errorHandlerAdvice;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(target)
                .setControllerAdvice(errorHandlerAdvice)
                .build();

        when(emailAddressService.getEmailAddressStatus(anyString())).thenReturn(EmailAddressStatus.ACTIVATED);
    }

    @Test
    public void getEmailAddressStatus_validEmailAddress() throws Exception {
        mockMvc.perform(get("/emailAddress/valid@email.com"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("ACTIVATED")));

        verify(emailAddressService, times(1)).getEmailAddressStatus("valid@email.com");
    }
}
