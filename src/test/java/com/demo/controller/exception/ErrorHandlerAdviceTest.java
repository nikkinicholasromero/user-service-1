package com.demo.controller.exception;

import com.demo.model.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

public class ErrorHandlerAdviceTest {
    @InjectMocks
    private ErrorHandlerAdvice target;

    @Mock
    private ErrorResponseBuilder errorResponseBuilder;

    @Mock
    private ErrorResponse errorResponse;

    @Captor
    private ArgumentCaptor<List<String>> errorCodesArgumentCaptor;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

        when(errorResponseBuilder.build(anyList())).thenReturn(errorResponse);
    }

    @Test
    public void handleMethodArgumentNotValidException() {
        MethodArgumentNotValidException ex = Mockito.mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = Mockito.mock(BindingResult.class);
        FieldError fieldError1 = Mockito.mock(FieldError.class);
        FieldError fieldError2 = Mockito.mock(FieldError.class);
        FieldError fieldError3 = Mockito.mock(FieldError.class);
        List<FieldError> fieldErrors = Arrays.asList(fieldError1, fieldError2, fieldError3);

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);
        when(fieldError1.getDefaultMessage()).thenReturn("E1");
        when(fieldError2.getDefaultMessage()).thenReturn("E2");
        when(fieldError3.getDefaultMessage()).thenReturn("E3");

        ResponseEntity<ErrorResponse> actual = target.handleException(ex);

        assertThat(actual).isNotNull();
        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(actual.getBody()).isEqualTo(errorResponse);

        verify(errorResponseBuilder, times(1))
                .build(errorCodesArgumentCaptor.capture());
        List<String> errorCodes = errorCodesArgumentCaptor.getValue();
        assertThat(errorCodes).isNotEmpty();
        assertThat(errorCodes.size()).isEqualTo(3);
        assertThat(errorCodes.get(0)).isEqualTo("E1");
        assertThat(errorCodes.get(1)).isEqualTo("E2");
        assertThat(errorCodes.get(2)).isEqualTo("E3");
    }

    @Test
    public void handleConstraintViolationException() {
        ConstraintViolationException ex = Mockito.mock(ConstraintViolationException.class);

        ConstraintViolation<?> constraintViolation1 = Mockito.mock(ConstraintViolation.class);
        ConstraintViolation<?> constraintViolation2 = Mockito.mock(ConstraintViolation.class);
        ConstraintViolation<?> constraintViolation3 = Mockito.mock(ConstraintViolation.class);
        when(constraintViolation1.getMessage()).thenReturn("E1");
        when(constraintViolation2.getMessage()).thenReturn("E2");
        when(constraintViolation3.getMessage()).thenReturn("E3");
        Set<ConstraintViolation<?>> constraintViolations = new HashSet<>(
                Arrays.asList(constraintViolation1, constraintViolation2, constraintViolation3));

        when(ex.getConstraintViolations()).thenReturn(constraintViolations);

        ResponseEntity<ErrorResponse> actual = target.handleException(ex);

        assertThat(actual).isNotNull();
        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(actual.getBody()).isEqualTo(errorResponse);

        verify(errorResponseBuilder, times(1))
                .build(errorCodesArgumentCaptor.capture());
        List<String> errorCodes = errorCodesArgumentCaptor.getValue();
        assertThat(errorCodes).isNotEmpty();
        assertThat(errorCodes.size()).isEqualTo(3);
        assertThat(errorCodes.indexOf("E1")).isNotNegative();
        assertThat(errorCodes.indexOf("E2")).isNotNegative();
        assertThat(errorCodes.indexOf("E3")).isNotNegative();
    }

    @Test
    public void handleUserRegistrationException() {
        UserServiceExceptionType type = UserServiceExceptionType.EMAIL_ADDRESS_ACTIVATION_CODE_INCORRECT_EXCEPTION;

        UserServiceException ex = Mockito.mock(UserServiceException.class);
        when(ex.getType()).thenReturn(type);

        ResponseEntity<ErrorResponse> actual = target.handleException(ex);

        assertThat(actual).isNotNull();
        assertThat(actual.getStatusCode()).isEqualTo(type.getHttpStatus());
        assertThat(actual.getBody()).isEqualTo(errorResponse);

        verify(errorResponseBuilder, times(1)).build(errorCodesArgumentCaptor.capture());
        List<String> errorCodes = errorCodesArgumentCaptor.getValue();
        assertThat(errorCodes).isNotEmpty();
        assertThat(errorCodes.size()).isEqualTo(1);
        assertThat(errorCodes.indexOf(type.getCode())).isNotNegative();
    }
}
