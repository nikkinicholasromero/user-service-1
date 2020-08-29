package com.demo.orchestrator;

import com.demo.controller.exception.UserServiceException;
import com.demo.controller.exception.UserServiceExceptionType;
import com.demo.external.email.EmailService;
import com.demo.external.email.Mail;
import com.demo.model.ForgotPassword;
import com.demo.model.UserAccount;
import com.demo.model.templatevariables.ForgotPasswordTemplate;
import com.demo.repository.UserAccountRepository;
import com.demo.service.ForgotPasswordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ForgotPasswordOrchestrator {
    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private ForgotPasswordGenerator forgotPasswordGenerator;

    @Autowired
    private EmailService emailService;

    @Value("${forgot-password.email.sender}")
    private String sender;

    @Value("${forgot-password.email.subject}")
    private String subject;

    @Value("${forgot-password.email.template}")
    private String template;

    @Value("${forgot-password.url}")
    private String forgotPasswordUrl;

    public void orchestrate(String emailAddress) {
        Optional<UserAccount> optionalUserAccount = userAccountRepository.getUserAccountByEmailAddress(emailAddress);
        if (!optionalUserAccount.isPresent()) {
            throw new UserServiceException(UserServiceExceptionType.EMAIL_ADDRESS_DOES_NOT_EXIST_EXCEPTION);
        }

        UserAccount userAccount = optionalUserAccount.get();
        ForgotPassword forgotPassword = forgotPasswordGenerator.generateForgotPassword();
        userAccount.setForgotPasswordCode(forgotPassword.getCode());
        userAccount.setForgotPasswordExpiration(forgotPassword.getExpiration());

        userAccountRepository.save(userAccount);

        sendForgotPasswordEmail(userAccount);
    }

    private void sendForgotPasswordEmail(UserAccount userAccount) {
        Mail mail = new Mail();
        mail.setFrom(sender);
        mail.setTo(userAccount.getEmailAddress());
        mail.setSubject(subject);
        mail.setTemplate(template);
        mail.setTemplateVariables(new ForgotPasswordTemplate(String.format(forgotPasswordUrl, userAccount.getEmailAddress(), userAccount.getForgotPasswordCode())));
        emailService.send(mail);
    }
}
