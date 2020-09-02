package com.demo.orchestrator;

import com.demo.controller.exception.UserServiceException;
import com.demo.controller.exception.UserServiceExceptionType;
import com.demo.model.ResetPasswordRequest;
import com.demo.model.UserAccount;
import com.demo.repository.UserAccountRepository;
import com.demo.service.HashService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class ResetPasswordOrchestrator {
    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private HashService hashService;

    public void orchestrate(ResetPasswordRequest request) {
        String emailAddress = request.getEmailAddress();
        String forgotPasswordCode = request.getForgotPasswordCode();
        String newPassword = request.getNewPassword();

        Optional<UserAccount> optionalUserAccount = userAccountRepository.getUserAccountByEmailAddress(emailAddress);
        if (!optionalUserAccount.isPresent()) {
            throw new UserServiceException(UserServiceExceptionType.EMAIL_ADDRESS_DOES_NOT_EXIST_EXCEPTION);
        }

        UserAccount userAccount = optionalUserAccount.get();
        if (!forgotPasswordCode.equals(userAccount.getForgotPasswordCode())) {
            throw new UserServiceException(UserServiceExceptionType.FORGOT_PASSWORD_CODE_INCORRECT_EXCEPTION);
        }

        if (LocalDateTime.now().isAfter(userAccount.getForgotPasswordExpiration())) {
            throw new UserServiceException(UserServiceExceptionType.FORGOT_PASSWORD_EXPIRED_EXCEPTION);
        }

        String salt = hashService.generateRandomSalt();
        String hash = hashService.hash(newPassword, salt);
        userAccount.setPassword(hash);
        userAccount.setSalt(salt);
        userAccount.setForgotPasswordCode(null);
        userAccount.setForgotPasswordExpiration(null);
        userAccountRepository.save(userAccount);
    }
}
