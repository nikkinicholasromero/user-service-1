package com.demo.orchestrator;

import com.demo.controller.exception.UserServiceException;
import com.demo.controller.exception.UserServiceExceptionType;
import com.demo.model.ResetPasswordRequest;
import com.demo.model.UserAccount;
import com.demo.repository.UserAccountRepository;
import com.demo.service.HashService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class ResetPasswordOrchestratorTest {
    @InjectMocks
    private ResetPasswordOrchestrator target;

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private HashService hashService;

    private ResetPasswordRequest request;

    private UserAccount userAccount;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

        request = new ResetPasswordRequest();
        request.setEmailAddress("someEmail@address.com");
        request.setForgotPasswordCode("someForgotPasswordCode");
        request.setNewPassword("someNewPassword");

        userAccount = new UserAccount();
    }

    @Test
    public void orchestrate_whenUserAccountDoesNotExist_thenThrowUserServiceException() {
        when(userAccountRepository.getUserAccountByEmailAddress("someEmail@address.com")).thenReturn(Optional.empty());

        UserServiceException e = assertThrows(UserServiceException.class, () -> target.orchestrate(request));
        assertThat(e.getType()).isEqualTo(UserServiceExceptionType.EMAIL_ADDRESS_DOES_NOT_EXIST_EXCEPTION);

        verify(userAccountRepository).getUserAccountByEmailAddress("someEmail@address.com");
        verifyNoInteractions(hashService);
        verifyNoMoreInteractions(userAccountRepository);
    }

    @Test
    public void orchestrate_whenForgotPasswordCodeIsDifferent_thenThrowUserServiceException() {
        userAccount.setForgotPasswordCode("differentCode");

        when(userAccountRepository.getUserAccountByEmailAddress("someEmail@address.com")).thenReturn(Optional.of(userAccount));

        UserServiceException e = assertThrows(UserServiceException.class, () -> target.orchestrate(request));
        assertThat(e.getType()).isEqualTo(UserServiceExceptionType.FORGOT_PASSWORD_CODE_INCORRECT_EXCEPTION);

        verify(userAccountRepository).getUserAccountByEmailAddress("someEmail@address.com");
        verifyNoInteractions(hashService);
        verifyNoMoreInteractions(userAccountRepository);
    }

    @Test
    public void orchestrate_whenForgotPasswordCodeIsAlreadyExpired_thenThrowUserServiceException() {
        userAccount.setForgotPasswordCode("someForgotPasswordCode");
        userAccount.setForgotPasswordExpiration(LocalDateTime.of(2000, 1, 1, 1, 1));

        when(userAccountRepository.getUserAccountByEmailAddress("someEmail@address.com")).thenReturn(Optional.of(userAccount));

        UserServiceException e = assertThrows(UserServiceException.class, () -> target.orchestrate(request));
        assertThat(e.getType()).isEqualTo(UserServiceExceptionType.FORGOT_PASSWORD_EXPIRED_EXCEPTION);

        verify(userAccountRepository).getUserAccountByEmailAddress("someEmail@address.com");
        verifyNoInteractions(hashService);
        verifyNoMoreInteractions(userAccountRepository);
    }

    @Test
    public void orchestrate() {
        userAccount.setForgotPasswordCode("someForgotPasswordCode");
        userAccount.setForgotPasswordExpiration(LocalDateTime.of(2100, 1, 1, 1, 1));

        when(userAccountRepository.getUserAccountByEmailAddress("someEmail@address.com")).thenReturn(Optional.of(userAccount));
        when(hashService.generateRandomSalt()).thenReturn("someRandomSalt");
        when(hashService.hash(anyString(), anyString())).thenReturn("someHash");

        target.orchestrate(request);

        verify(userAccountRepository).getUserAccountByEmailAddress("someEmail@address.com");
        verify(hashService).hash("someNewPassword", "someRandomSalt");
        verify(userAccountRepository).save(userAccount);

        assertThat(userAccount).isNotNull();
        assertThat(userAccount.getPassword()).isEqualTo("someHash");
        assertThat(userAccount.getSalt()).isEqualTo("someRandomSalt");
        assertThat(userAccount.getForgotPasswordCode()).isNull();
        assertThat(userAccount.getForgotPasswordExpiration()).isNull();
    }
}
