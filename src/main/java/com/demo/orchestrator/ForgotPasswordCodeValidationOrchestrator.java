package com.demo.orchestrator;

import com.demo.controller.exception.UserServiceException;
import com.demo.controller.exception.UserServiceExceptionType;
import com.demo.model.UserAccount;
import com.demo.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class ForgotPasswordCodeValidationOrchestrator {
    @Autowired
    private UserAccountRepository userAccountRepository;

    public void orchestrate(String emailAddress, String forgotPasswordCode) {
        Optional<UserAccount> optionalUserAccount = userAccountRepository.getUserAccountByEmailAddress(emailAddress);
        if (!optionalUserAccount.isPresent()) {
            throw new UserServiceException(UserServiceExceptionType.EMAIL_ADDRESS_DOES_NOT_EXIST_EXCEPTION);
        }

        UserAccount userAccount = optionalUserAccount.get();
        if (!forgotPasswordCode.equals(userAccount.getForgotPasswordCode())) {
            throw new UserServiceException(UserServiceExceptionType.EMAIL_ADDRESS_FORGOT_PASSWORD_CODE_INCORRECT_EXCEPTION);
        }

        if (LocalDateTime.now().isAfter(userAccount.getForgotPasswordExpiration())) {
            throw new UserServiceException(UserServiceExceptionType.EMAIL_ADDRESS_FORGOT_PASSWORD_EXPIRED_EXCEPTION);
        }
    }
}
