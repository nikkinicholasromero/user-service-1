package com.demo.orchestrator;

import com.demo.controller.exception.UserServiceException;
import com.demo.controller.exception.UserServiceExceptionType;
import com.demo.model.UserAccount;
import com.demo.repository.UserAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ForgotPasswordCodeValidationOrchestratorTest {
    @InjectMocks
    private ForgotPasswordCodeValidationOrchestrator target;

    @Mock
    private UserAccountRepository userAccountRepository;

    @Captor
    private ArgumentCaptor<UserAccount> userAccountArgumentCaptor;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void orchestrate_whenEmailAddressDoesNotExist() {
        when(userAccountRepository.getUserAccountByEmailAddress(anyString())).thenReturn(Optional.ofNullable(null));

        UserServiceException e = assertThrows(UserServiceException.class,
                () -> target.orchestrate("some@email.com", "someCode"));
        assertThat(e.getType()).isEqualTo(UserServiceExceptionType.EMAIL_ADDRESS_DOES_NOT_EXIST_EXCEPTION);

        verify(userAccountRepository, times(1)).getUserAccountByEmailAddress("some@email.com");
    }

    @Test
    public void orchestrate_whenForgotPasswordCodeIsIncorrect() {
        UserAccount userAccount = new UserAccount();
        userAccount.setForgotPasswordCode("someCode");
        when(userAccountRepository.getUserAccountByEmailAddress(anyString())).thenReturn(Optional.of(userAccount));

        UserServiceException e = assertThrows(UserServiceException.class,
                () -> target.orchestrate("some@email.com", "someIncorrectCode"));
        assertThat(e.getType()).isEqualTo(UserServiceExceptionType.EMAIL_ADDRESS_FORGOT_PASSWORD_CODE_INCORRECT_EXCEPTION);

        verify(userAccountRepository, times(1)).getUserAccountByEmailAddress("some@email.com");
    }

    @Test
    public void orchestrate_whenForgotPasswordCodeIsAlreadyExpired() {
        UserAccount userAccount = new UserAccount();
        userAccount.setForgotPasswordCode("someCode");
        userAccount.setForgotPasswordExpiration(LocalDateTime.now().minusDays(1));
        when(userAccountRepository.getUserAccountByEmailAddress(anyString())).thenReturn(Optional.of(userAccount));

        UserServiceException e = assertThrows(UserServiceException.class,
                () -> target.orchestrate("some@email.com", "someCode"));
        assertThat(e.getType()).isEqualTo(UserServiceExceptionType.EMAIL_ADDRESS_FORGOT_PASSWORD_EXPIRED_EXCEPTION);

        verify(userAccountRepository, times(1)).getUserAccountByEmailAddress("some@email.com");
    }

    @Test
    public void orchestrate() {
        UserAccount userAccount = new UserAccount();
        userAccount.setForgotPasswordCode("someCode");
        userAccount.setForgotPasswordExpiration(LocalDateTime.now().plusHours(1));
        when(userAccountRepository.getUserAccountByEmailAddress(anyString())).thenReturn(Optional.of(userAccount));

        target.orchestrate("some@email.com", "someCode");

        verify(userAccountRepository, times(1)).getUserAccountByEmailAddress("some@email.com");
    }
}
