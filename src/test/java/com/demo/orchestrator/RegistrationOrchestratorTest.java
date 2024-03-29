package com.demo.orchestrator;

import com.demo.controller.exception.UserServiceException;
import com.demo.controller.exception.UserServiceExceptionType;
import com.demo.external.email.EmailService;
import com.demo.external.email.Mail;
import com.demo.model.Activation;
import com.demo.model.EmailAddressStatus;
import com.demo.model.UserAccount;
import com.demo.model.templatevariables.AccountActivation;
import com.demo.repository.UserAccountRepository;
import com.demo.service.ActivationGenerator;
import com.demo.service.EmailAddressService;
import com.demo.service.HashService;
import com.demo.service.UuidGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class RegistrationOrchestratorTest {
    @InjectMocks
    private RegistrationOrchestrator target;

    @Mock
    private EmailAddressService emailAddressService;

    @Mock
    private UuidGenerator uuidGenerator;

    @Mock
    private HashService hashService;

    @Mock
    private ActivationGenerator activationGenerator;

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private EmailService emailService;

    @Captor
    private ArgumentCaptor<UserAccount> userAccountArgumentCaptor;

    @Captor
    private ArgumentCaptor<Mail> mailArgumentCaptor;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

        ReflectionTestUtils.setField(target, "sender", "someTest@sender.com");
        ReflectionTestUtils.setField(target, "subject", "Some Test Subject");
        ReflectionTestUtils.setField(target, "template", "some_template");
        ReflectionTestUtils.setField(target, "activationUrl", "http://localhost:4200/accountActivation?emailAddress=%s&activationCode=%s");

        Activation activation = new Activation();
        activation.setCode("someActivationCode");
        activation.setExpiration(LocalDateTime.of(2020, 7, 18, 2, 27));

        when(hashService.generateRandomSalt()).thenReturn("someSalt");
        when(hashService.hash(anyString(), anyString())).thenReturn("someHash");
        when(activationGenerator.generateActivation()).thenReturn(activation);
        when(uuidGenerator.generateRandomUuid()).thenReturn("sommeUuid");
    }

    @Test
    public void orchestrate_whenEmailAddressIsAlreadyActivated() {
        when(emailAddressService.getEmailAddressStatus(anyString())).thenReturn(EmailAddressStatus.ACTIVATED);

        UserAccount userAccount = new UserAccount();
        userAccount.setEmailAddress("someEmail@address.com");

        UserServiceException e = assertThrows(UserServiceException.class, () -> target.orchestrate(userAccount));
        assertThat(e.getType()).isEqualTo(UserServiceExceptionType.EMAIL_ADDRESS_IS_ALREADY_TAKEN_EXCEPTION);

        verify(emailAddressService, times(1)).getEmailAddressStatus("someEmail@address.com");
    }

    @Test
    public void orchestrate_whenEmailAddressIsRegistered() {
        when(emailAddressService.getEmailAddressStatus(anyString())).thenReturn(EmailAddressStatus.REGISTERED);

        UserAccount userAccount = new UserAccount();
        userAccount.setEmailAddress("someEmail@address.com");
        userAccount.setPassword("userInputPassword");
        userAccount.setFirstName("Nikki Nicholas");
        userAccount.setLastName("Romero");

        target.orchestrate(userAccount);

        verify(userAccountRepository, times(1)).deleteByEmailAddress(userAccount.getEmailAddress());
        verify(hashService, times(1)).generateRandomSalt();
        verify(hashService, times(1)).hash("userInputPassword", "someSalt");
        verify(activationGenerator, times(1)).generateActivation();
        verify(uuidGenerator, times(1)).generateRandomUuid();
        verify(userAccountRepository, times(1)).save(userAccountArgumentCaptor.capture());

        UserAccount actualUserAccount = userAccountArgumentCaptor.getValue();
        assertThat(actualUserAccount).isNotNull();
        assertThat(actualUserAccount.getId()).isEqualTo("sommeUuid");
        assertThat(actualUserAccount.getEmailAddress()).isEqualTo("someEmail@address.com");
        assertThat(actualUserAccount.getPassword()).isEqualTo("someHash");
        assertThat(actualUserAccount.getSalt()).isEqualTo("someSalt");
        assertThat(actualUserAccount.getActivationCode()).isEqualTo("someActivationCode");
        assertThat(actualUserAccount.getActivationExpiration()).isEqualTo(LocalDateTime.of(2020, 7, 18, 2, 27));
        assertThat(actualUserAccount.getStatus()).isEqualTo(EmailAddressStatus.REGISTERED);
        assertThat(actualUserAccount.getFirstName()).isEqualTo("Nikki Nicholas");
        assertThat(actualUserAccount.getLastName()).isEqualTo("Romero");

        verify(emailService, times(1)).send(mailArgumentCaptor.capture());

        Mail actualMail = mailArgumentCaptor.getValue();
        assertThat(actualMail).isNotNull();
        assertThat(actualMail.getFrom()).isEqualTo("someTest@sender.com");
        assertThat(actualMail.getTo()).isEqualTo("someEmail@address.com");
        assertThat(actualMail.getSubject()).isEqualTo("Some Test Subject");
        assertThat(actualMail.getTemplate()).isEqualTo("some_template");
        assertThat(actualMail.getTemplateVariables()).isEqualTo(new AccountActivation("http://localhost:4200/accountActivation?emailAddress=someEmail@address.com&activationCode=someActivationCode"));
    }

    @Test
    public void orchestrate_whenEmailAddressIsAvailable() {
        when(emailAddressService.getEmailAddressStatus(anyString())).thenReturn(EmailAddressStatus.NOT_REGISTERED);

        UserAccount userAccount = new UserAccount();
        userAccount.setEmailAddress("someEmail@address.com");
        userAccount.setPassword("userInputPassword");
        userAccount.setFirstName("Nikki Nicholas");
        userAccount.setLastName("Romero");

        target.orchestrate(userAccount);

        verify(userAccountRepository, never()).deleteByEmailAddress(anyString());
        verify(hashService, times(1)).generateRandomSalt();
        verify(hashService, times(1)).hash("userInputPassword", "someSalt");
        verify(activationGenerator, times(1)).generateActivation();
        verify(uuidGenerator, times(1)).generateRandomUuid();
        verify(userAccountRepository, times(1)).save(userAccountArgumentCaptor.capture());

        UserAccount actualUserAccount = userAccountArgumentCaptor.getValue();
        assertThat(actualUserAccount).isNotNull();
        assertThat(actualUserAccount.getId()).isEqualTo("sommeUuid");
        assertThat(actualUserAccount.getEmailAddress()).isEqualTo("someEmail@address.com");
        assertThat(actualUserAccount.getPassword()).isEqualTo("someHash");
        assertThat(actualUserAccount.getSalt()).isEqualTo("someSalt");
        assertThat(actualUserAccount.getActivationCode()).isEqualTo("someActivationCode");
        assertThat(actualUserAccount.getActivationExpiration()).isEqualTo(LocalDateTime.of(2020, 7, 18, 2, 27));
        assertThat(actualUserAccount.getStatus()).isEqualTo(EmailAddressStatus.REGISTERED);
        assertThat(actualUserAccount.getFirstName()).isEqualTo("Nikki Nicholas");
        assertThat(actualUserAccount.getLastName()).isEqualTo("Romero");

        verify(emailService, times(1)).send(mailArgumentCaptor.capture());

        Mail actualMail = mailArgumentCaptor.getValue();
        assertThat(actualMail).isNotNull();
        assertThat(actualMail.getFrom()).isEqualTo("someTest@sender.com");
        assertThat(actualMail.getTo()).isEqualTo("someEmail@address.com");
        assertThat(actualMail.getSubject()).isEqualTo("Some Test Subject");
        assertThat(actualMail.getTemplate()).isEqualTo("some_template");
        assertThat(actualMail.getTemplateVariables()).isEqualTo(new AccountActivation("http://localhost:4200/accountActivation?emailAddress=someEmail@address.com&activationCode=someActivationCode"));
    }
}
