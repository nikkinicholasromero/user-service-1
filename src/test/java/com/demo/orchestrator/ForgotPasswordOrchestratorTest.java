package com.demo.orchestrator;

import com.demo.controller.exception.ForgotPasswordException;
import com.demo.external.email.EmailService;
import com.demo.external.email.Mail;
import com.demo.model.ForgotPassword;
import com.demo.model.UserAccount;
import com.demo.model.templatevariables.ForgotPasswordTemplate;
import com.demo.repository.UserAccountRepository;
import com.demo.service.ForgotPasswordGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ForgotPasswordOrchestratorTest {
    @InjectMocks
    private ForgotPasswordOrchestrator target;

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private ForgotPasswordGenerator forgotPasswordGenerator;

    @Mock
    private EmailService emailService;

    @Captor
    private ArgumentCaptor<Mail> mailArgumentCaptor;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

        ReflectionTestUtils.setField(target, "sender", "someTest@sender.com");
        ReflectionTestUtils.setField(target, "subject", "Some Test Subject");
        ReflectionTestUtils.setField(target, "template", "some_template");
        ReflectionTestUtils.setField(target, "forgotPasswordUrl", "http://localhost:4200/resetPassword?emailAddress=%s&forgotPasswordCode=%s");
    }

    @Test
    public void orchestrate_whenEmailAddressDoesNotExist() {
        when(userAccountRepository.getUserAccountByEmailAddress(anyString())).thenReturn(Optional.empty());

        assertThrows(ForgotPasswordException.class,
                () -> target.orchestrate("some@email.com"));

        verify(userAccountRepository, times(1)).getUserAccountByEmailAddress("some@email.com");
        verifyNoInteractions(forgotPasswordGenerator);
        verify(userAccountRepository, never()).save(any());
        verifyNoInteractions(emailService);
    }
//

    @Test
    public void orchestrate_whenEmailAddressDoesExist() {
        UserAccount expected = new UserAccount();
        expected.setForgotPasswordCode("someCode");
        expected.setForgotPasswordExpiration(LocalDateTime.now());

        ForgotPassword forgotPassword = new ForgotPassword();
        forgotPassword.setCode(expected.getForgotPasswordCode());
        forgotPassword.setExpiration(expected.getForgotPasswordExpiration());
        UserAccount userAccount = new UserAccount();
        userAccount.setEmailAddress("some@email.com");

        when(userAccountRepository.getUserAccountByEmailAddress(anyString())).thenReturn(Optional.of(userAccount));
        when(forgotPasswordGenerator.generateForgotPassword()).thenReturn(forgotPassword);

        target.orchestrate("some@email.com");

        verify(userAccountRepository, times(1)).getUserAccountByEmailAddress("some@email.com");
        verify(forgotPasswordGenerator, times(1)).generateForgotPassword();
        verify(userAccountRepository, times(1)).save(userAccount);
        verify(emailService, times(1)).send(mailArgumentCaptor.capture());

        Mail actualMail = mailArgumentCaptor.getValue();
        assertThat(actualMail).isNotNull();
        assertThat(actualMail.getFrom()).isEqualTo("someTest@sender.com");
        assertThat(actualMail.getTo()).isEqualTo("some@email.com");
        assertThat(actualMail.getSubject()).isEqualTo("Some Test Subject");
        assertThat(actualMail.getTemplate()).isEqualTo("some_template");
        assertThat(actualMail.getTemplateVariables()).isNotNull();
        assertThat(actualMail.getTemplateVariables()).isInstanceOf(ForgotPasswordTemplate.class);
        assertThat(((ForgotPasswordTemplate) actualMail.getTemplateVariables()).getForgotPasswordLink()).isEqualTo("http://localhost:4200/resetPassword?emailAddress=some@email.com&forgotPasswordCode=someCode");
    }
}
