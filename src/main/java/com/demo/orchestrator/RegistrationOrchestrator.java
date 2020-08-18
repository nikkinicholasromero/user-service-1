package com.demo.orchestrator;

import com.demo.controller.exception.UserRegistrationException;
import com.demo.controller.exception.UserRegistrationExceptionType;
import com.demo.external.email.EmailService;
import com.demo.external.email.Mail;
import com.demo.model.Activation;
import com.demo.model.EmailAddressStatus;
import com.demo.model.UserAccount;
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

    @Value("${activation.email.body}")
    private String activationEmailBody;

    public void orchestrate(UserAccount userAccount) {
        EmailAddressStatus status = emailAddressService.getEmailAddressStatus(userAccount.getEmailAddress());
        if (EmailAddressStatus.REGISTERED.equals(status)) {
            throw new UserRegistrationException(UserRegistrationExceptionType.EMAIL_ADDRESS_IS_DUE_FOR_ACTIVATION_EXCEPTION);
        } else if (EmailAddressStatus.ACTIVATED.equals(status)) {
            throw new UserRegistrationException(UserRegistrationExceptionType.EMAIL_ADDRESS_IS_ALREADY_TAKEN_EXCEPTION);
        }

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

        Mail mail = new Mail();
        mail.setFrom(activationEmailSender);
        mail.setTo(userAccount.getEmailAddress());
        mail.setSubject(activationEmailSubject);
        mail.setBody(String.format(activationEmailBody, userAccount.getActivationCode()));
        emailService.send(mail);
    }
}
