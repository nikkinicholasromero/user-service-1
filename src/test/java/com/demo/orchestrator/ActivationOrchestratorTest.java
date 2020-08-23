package com.demo.orchestrator;

import com.demo.controller.exception.UserRegistrationException;
import com.demo.controller.exception.UserRegistrationExceptionType;
import com.demo.model.EmailAddressStatus;
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

public class ActivationOrchestratorTest {
    @InjectMocks
    private ActivationOrchestrator target;

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

        UserRegistrationException e = assertThrows(UserRegistrationException.class,
                () -> target.orchestrate("some@email.com", "someCode"));
        assertThat(e.getType()).isEqualTo(UserRegistrationExceptionType.EMAIL_ADDRESS_DOES_NOT_EXIST_EXCEPTION);

        verify(userAccountRepository, times(1)).getUserAccountByEmailAddress("some@email.com");
    }

    @Test
    public void orchestrate_whenEmailAddressIsAlreadyActivated() {
        UserAccount userAccount = new UserAccount();
        userAccount.setStatus(EmailAddressStatus.ACTIVATED);
        when(userAccountRepository.getUserAccountByEmailAddress(anyString())).thenReturn(Optional.of(userAccount));

        UserRegistrationException e = assertThrows(UserRegistrationException.class,
                () -> target.orchestrate("some@email.com", "someCode"));
        assertThat(e.getType()).isEqualTo(UserRegistrationExceptionType.EMAIL_ADDRESS_IS_ALREADY_ACTIVATED_EXCEPTION);

        verify(userAccountRepository, times(1)).getUserAccountByEmailAddress("some@email.com");
    }

    @Test
    public void orchestrate_whenActivationCodeIsIncorrect() {
        UserAccount userAccount = new UserAccount();
        userAccount.setStatus(EmailAddressStatus.REGISTERED);
        userAccount.setActivationCode("someCode");
        when(userAccountRepository.getUserAccountByEmailAddress(anyString())).thenReturn(Optional.of(userAccount));

        UserRegistrationException e = assertThrows(UserRegistrationException.class,
                () -> target.orchestrate("some@email.com", "someIncorrectCode"));
        assertThat(e.getType()).isEqualTo(UserRegistrationExceptionType.EMAIL_ADDRESS_ACTIVATION_CODE_INCORRECT_EXCEPTION);

        verify(userAccountRepository, times(1)).getUserAccountByEmailAddress("some@email.com");
    }

    @Test
    public void orchestrate_whenActivationCodeIsAlreadyExpired() {
        UserAccount userAccount = new UserAccount();
        userAccount.setStatus(EmailAddressStatus.REGISTERED);
        userAccount.setActivationCode("someCode");
        userAccount.setActivationExpiration(LocalDateTime.now().minusDays(1));
        when(userAccountRepository.getUserAccountByEmailAddress(anyString())).thenReturn(Optional.of(userAccount));

        UserRegistrationException e = assertThrows(UserRegistrationException.class,
                () -> target.orchestrate("some@email.com", "someCode"));
        assertThat(e.getType()).isEqualTo(UserRegistrationExceptionType.EMAIL_ADDRESS_ACTIVATION_EXPIRED_EXCEPTION);

        verify(userAccountRepository, times(1)).getUserAccountByEmailAddress("some@email.com");
    }

    @Test
    public void orchestrate() {
        UserAccount userAccount = new UserAccount();
        userAccount.setId("someId");
        userAccount.setEmailAddress("some@email.com");
        userAccount.setPassword("somePassword");
        userAccount.setSalt("someSalt");
        userAccount.setActivationCode("someCode");
        userAccount.setActivationExpiration(LocalDateTime.now().plusHours(1));
        userAccount.setStatus(EmailAddressStatus.REGISTERED);
        userAccount.setFirstName("someFirstName");
        userAccount.setLastName("someLastName");
        when(userAccountRepository.getUserAccountByEmailAddress(anyString())).thenReturn(Optional.of(userAccount));

        target.orchestrate("some@email.com", "someCode");

        verify(userAccountRepository, times(1)).getUserAccountByEmailAddress("some@email.com");
        verify(userAccountRepository, times(1)).save(userAccountArgumentCaptor.capture());
        UserAccount actualUserAccount = userAccountArgumentCaptor.getValue();
        assertThat(actualUserAccount).isNotNull();
        assertThat(actualUserAccount.getId()).isEqualTo("someId");
        assertThat(actualUserAccount.getEmailAddress()).isEqualTo("some@email.com");
        assertThat(actualUserAccount.getPassword()).isEqualTo("somePassword");
        assertThat(actualUserAccount.getSalt()).isEqualTo("someSalt");
        assertThat(actualUserAccount.getActivationCode()).isNull();
        assertThat(actualUserAccount.getActivationExpiration()).isNull();
        assertThat(actualUserAccount.getStatus()).isEqualTo(EmailAddressStatus.ACTIVATED);
        assertThat(actualUserAccount.getFirstName()).isEqualTo("someFirstName");
        assertThat(actualUserAccount.getLastName()).isEqualTo("someLastName");
    }
}
