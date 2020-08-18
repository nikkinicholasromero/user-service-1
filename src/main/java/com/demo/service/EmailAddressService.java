package com.demo.service;

import com.demo.model.EmailAddressStatus;
import com.demo.model.projection.EmailAddressStatusView;
import com.demo.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmailAddressService {
    @Autowired
    private UserAccountRepository userAccountRepository;

    public EmailAddressStatus getEmailAddressStatus(String emailAddress) {
        Optional<EmailAddressStatusView> emailAddressStatus = userAccountRepository.getEmailAddressStatusByEmailAddress(emailAddress);
        if (emailAddressStatus.isPresent()) {
            return emailAddressStatus.get().getStatus();
        } else {
            return EmailAddressStatus.NOT_REGISTERED;
        }
    }
}
