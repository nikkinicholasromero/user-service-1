package com.demo.repository;

import com.demo.model.UserAccount;
import com.demo.model.projection.EmailAddressStatusView;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserAccountRepository extends CrudRepository<UserAccount, Integer> {
    Optional<EmailAddressStatusView> getEmailAddressStatusByEmailAddress(String emailAddress);
    Optional<UserAccount> getUserAccountByEmailAddress(String emailAddress);
    void deleteByEmailAddress(String emailAddress);
}
