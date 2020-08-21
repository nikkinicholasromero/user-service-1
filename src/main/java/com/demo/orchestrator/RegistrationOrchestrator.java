package com.demo.orchestrator;

import com.demo.controller.exception.UserRegistrationException;
import com.demo.controller.exception.UserRegistrationExceptionType;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RegistrationOrchestrator {
    @Autowired
    private EmailAddressService emailAddressService;

    @Autowired
    private HashService hashService;

    @Autowired
    private ActivationGenerator activationGenerator;

    @Autowired
    private UuidGenerator uuidGenerator;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private EmailService emailService;

    @Value("${activation.email.sender}")
    private String activationEmailSender;

    @Value("${activation.email.subject}")
    private String activationEmailSubject;

    @Value("${activation.email.template}")
    private String activationEmailTemplate;

    @Value("${activation.link}")
    private String activationLink;

    public void orchestrate(UserAccount userAccount) {
        EmailAddressStatus status = emailAddressService.getEmailAddressStatus(userAccount.getEmailAddress());
        if (EmailAddressStatus.ACTIVATED.equals(status)) {
            throw new UserRegistrationException(UserRegistrationExceptionType.EMAIL_ADDRESS_IS_ALREADY_TAKEN_EXCEPTION);
        }

        Activation activation = createAccount(userAccount);
        sendActivationEmail(userAccount, activation);
    }

    private Activation createAccount(UserAccount userAccount) {
        String salt = hashService.generateRandomSalt();
        String hash = hashService.hash(userAccount.getPassword(), salt);
        userAccount.setPassword(hash);
        userAccount.setSalt(salt);

        Activation activation = activationGenerator.generateActivation();
        userAccount.setActivationCode(activation.getCode());
        userAccount.setActivationExpiration(activation.getExpiration());

        userAccount.setId(uuidGenerator.generateRandomUuid());
        userAccount.setStatus(EmailAddressStatus.REGISTERED);
        userAccountRepository.save(userAccount);
        return activation;
    }

    private void sendActivationEmail(UserAccount userAccount, Activation activation) {
        Mail mail = new Mail();
        mail.setFrom(activationEmailSender);
        mail.setTo(userAccount.getEmailAddress());
        mail.setSubject(activationEmailSubject);
        mail.setTemplate(activationEmailTemplate);
        mail.setTemplateVariables(new AccountActivation(activationLink + activation.getCode()));
        emailService.send(mail);
    }
}
