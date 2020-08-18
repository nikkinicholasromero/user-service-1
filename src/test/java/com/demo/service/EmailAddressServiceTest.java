package com.demo.service;

import com.demo.model.EmailAddressStatus;
import com.demo.model.projection.EmailAddressStatusView;
import com.demo.repository.UserAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class EmailAddressServiceTest {
    @InjectMocks
    private EmailAddressService target;

    @Mock
    private UserAccountRepository userAccountRepository;

    private Optional<EmailAddressStatusView> optionalEmailAddressStatusView;

    @Mock
    private EmailAddressStatusView emailAddressStatusView;

    @BeforeEach
    private void setup() {
        MockitoAnnotations.initMocks(this);

        when(emailAddressStatusView.getStatus()).thenReturn(EmailAddressStatus.ACTIVATED);
    }

    @Test
    public void getEmailAddressStatus_userAccountExists_thenReturnEmailAddressStatus() {
        optionalEmailAddressStatusView = Optional.of(emailAddressStatusView);
        when(userAccountRepository.getEmailAddressStatusByEmailAddress(anyString()))
                .thenReturn(optionalEmailAddressStatusView);

        EmailAddressStatus status = target.getEmailAddressStatus("some@email.com");

        assertThat(status).isEqualTo(EmailAddressStatus.ACTIVATED);

        verify(userAccountRepository, times(1)).getEmailAddressStatusByEmailAddress("some@email.com");
    }

    @Test
    public void getEmailAddressStatus_userAccountDoesNotExist_thenReturnNotRegistered() {
        optionalEmailAddressStatusView = Optional.ofNullable(null);
        when(userAccountRepository.getEmailAddressStatusByEmailAddress(anyString()))
                .thenReturn(optionalEmailAddressStatusView);

        EmailAddressStatus status = target.getEmailAddressStatus("some@email.com");

        assertThat(status).isEqualTo(EmailAddressStatus.NOT_REGISTERED);

        verify(userAccountRepository, times(1)).getEmailAddressStatusByEmailAddress("some@email.com");
    }
}
