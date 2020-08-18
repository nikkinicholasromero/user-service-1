package com.demo.controller.exception;

import com.demo.model.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class ErrorResponseBuilderTest {
    @InjectMocks
    private ErrorResponseBuilder target;

    @Mock
    private MessageSource messageSource;

    private List<String> errorCodes;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

        errorCodes = Arrays.asList("E1", "E2", "E3");

        when(messageSource.getMessage("E1.code", null, Locale.US))
                .thenReturn("E1 Error Code");
        when(messageSource.getMessage("E1.message", null, Locale.US))
                .thenReturn("E1 Error Message");

        when(messageSource.getMessage("E2.code", null, Locale.US))
                .thenReturn("E2 Error Code");
        when(messageSource.getMessage("E2.message", null, Locale.US))
                .thenReturn("E2 Error Message");

        when(messageSource.getMessage("E3.code", null, Locale.US))
                .thenReturn("E3 Error Code");
        when(messageSource.getMessage("E3.message", null, Locale.US))
                .thenReturn("E3 Error Message");
    }

    @Test
    public void build() {
        ErrorResponse actual = target.build(errorCodes);

        assertThat(actual).isNotNull();
        assertThat(actual.getErrors()).isNotEmpty();
        assertThat(actual.getErrors().size()).isEqualTo(3);
        assertThat(actual.getErrors().get(0)).isNotNull();
        assertThat(actual.getErrors().get(0).getCode()).isEqualTo("E1 Error Code");
        assertThat(actual.getErrors().get(0).getMessage()).isEqualTo("E1 Error Message");
        assertThat(actual.getErrors().get(1)).isNotNull();
        assertThat(actual.getErrors().get(1).getCode()).isEqualTo("E2 Error Code");
        assertThat(actual.getErrors().get(1).getMessage()).isEqualTo("E2 Error Message");
        assertThat(actual.getErrors().get(2)).isNotNull();
        assertThat(actual.getErrors().get(2).getCode()).isEqualTo("E3 Error Code");
        assertThat(actual.getErrors().get(2).getMessage()).isEqualTo("E3 Error Message");

        verify(messageSource, times(1))
                .getMessage("E1.code", null, Locale.US);
        verify(messageSource, times(1))
                .getMessage("E1.message", null, Locale.US);
        verify(messageSource, times(1))
                .getMessage("E2.code", null, Locale.US);
        verify(messageSource, times(1))
                .getMessage("E2.message", null, Locale.US);
        verify(messageSource, times(1))
                .getMessage("E3.code", null, Locale.US);
        verify(messageSource, times(1))
                .getMessage("E3.message", null, Locale.US);
    }
}
