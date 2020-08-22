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
import org.springframework.transaction.annotation.Transactional;

@Component
public class RegistrationOrchestrator {
    @Autowired
    private EmailAddressService emailAddressService;

    @Autowired
    private UuidGenerator uuidGenerator;

    @Autowired
    private HashService hashService;

    @Autowired
    private ActivationGenerator activationGenerator;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private EmailService emailService;

    @Value("${activation.email.sender}")
    private String sender;

    @Value("${activation.email.subject}")
    private String subject;

    @Value("${activation.email.template}")
    private String template;

    @Value("${activation.link}")
    private String activationLink;

    @Transactional
    public void orchestrate(UserAccount userAccount) {
        EmailAddressStatus status = emailAddressService.getEmailAddressStatus(userAccount.getEmailAddress());
        if (EmailAddressStatus.ACTIVATED.equals(status)) {
            throw new UserRegistrationException(UserRegistrationExceptionType.EMAIL_ADDRESS_IS_ALREADY_TAKEN_EXCEPTION);
        }

        if (EmailAddressStatus.REGISTERED.equals(status)) {
            userAccountRepository.deleteByEmailAddress(userAccount.getEmailAddress());
        }

        Activation activation = saveAccount(userAccount);
        sendActivationEmail(userAccount, activation);
    }

    private Activation saveAccount(UserAccount userAccount) {
        userAccount.setId(uuidGenerator.generateRandomUuid());
        userAccount.setStatus(EmailAddressStatus.REGISTERED);

        String salt = hashService.generateRandomSalt();
        String hash = hashService.hash(userAccount.getPassword(), salt);
        userAccount.setPassword(hash);
        userAccount.setSalt(salt);

        Activation activation = activationGenerator.generateActivation();
        userAccount.setActivationCode(activation.getCode());
        userAccount.setActivationExpiration(activation.getExpiration());

        userAccountRepository.save(userAccount);

        return activation;
    }

    private void sendActivationEmail(UserAccount userAccount, Activation activation) {
        Mail mail = new Mail();
        mail.setFrom(sender);
        mail.setTo(userAccount.getEmailAddress());
        mail.setSubject(subject);
        mail.setTemplate(template);
        mail.setTemplateVariables(new AccountActivation(activationLink + activation.getCode()));
        emailService.send(mail);
    }
}
