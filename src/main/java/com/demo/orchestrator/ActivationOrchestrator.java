package com.demo.orchestrator;

import com.demo.controller.exception.UserRegistrationException;
import com.demo.controller.exception.UserRegistrationExceptionType;
import com.demo.model.EmailAddressStatus;
import com.demo.model.UserAccount;
import com.demo.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class ActivationOrchestrator {
    @Autowired
    private UserAccountRepository userAccountRepository;

    public void orchestrate(String emailAddress, String activationCode) {
        Optional<UserAccount> optionalUserAccount = userAccountRepository.getUserAccountByEmailAddress(emailAddress);
        if (!optionalUserAccount.isPresent()) {
            throw new UserRegistrationException(UserRegistrationExceptionType.EMAIL_ADDRESS_DOES_NOT_EXIST_EXCEPTION);
        }

        UserAccount userAccount = optionalUserAccount.get();
        if (EmailAddressStatus.ACTIVATED.equals(userAccount.getStatus())) {
            throw new UserRegistrationException(UserRegistrationExceptionType.EMAIL_ADDRESS_IS_ALREADY_ACTIVATED_EXCEPTION);
        }

        if (!activationCode.equals(userAccount.getActivationCode())) {
            throw new UserRegistrationException(UserRegistrationExceptionType.EMAIL_ADDRESS_ACTIVATION_CODE_INCORRECT_EXCEPTION);
        }

        if (LocalDateTime.now().isAfter(userAccount.getActivationExpiration())) {
            throw new UserRegistrationException(UserRegistrationExceptionType.EMAIL_ADDRESS_ACTIVATION_EXPIRED_EXCEPTION);
        }

        userAccount.setStatus(EmailAddressStatus.ACTIVATED);
        userAccount.setActivationCode(null);
        userAccount.setActivationExpiration(null);
        userAccountRepository.save(userAccount);
    }
}
