package com.devcomanda.demospringsecurity.services.impl;


import com.devcomanda.demospringsecurity.exceptions.CannotActivateYouSelfException;
import com.devcomanda.demospringsecurity.exceptions.security.InvalidActivationKeyException;
import com.devcomanda.demospringsecurity.exceptions.security.UserNotActivatedException;
import com.devcomanda.demospringsecurity.exceptions.security.UserNotFoundException;
import com.devcomanda.demospringsecurity.model.User;
import com.devcomanda.demospringsecurity.repositories.UserRepository;
import com.devcomanda.demospringsecurity.services.UserSecurityService;
import com.devcomanda.demospringsecurity.services.UserService;
import com.devcomanda.demospringsecurity.utils.RandomUtil;
import com.devcomanda.demospringsecurity.utils.SecurityUtils;
import com.devcomanda.demospringsecurity.web.api.requests.NewUserReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
@Service
@Transactional
public class UserSecurityServiceImpl implements UserSecurityService {

    private final UserRepository userRepository;

    private final UserService userService;

    private final MailServiceImpl mailServiceImpl;

    @Autowired
    public UserSecurityServiceImpl(
        final UserRepository userRepository,
        final UserService userService,
        final MailServiceImpl mailServiceImpl
    ) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.mailServiceImpl = mailServiceImpl;
    }

    @Override
    public User register(final NewUserReq req) {
        final User user = this.userService.createUser(req, false);
        this.mailServiceImpl.sendActivationEmail(user);
        return user;
    }

    @Override
    public User activate(final String key) {
        return this.userRepository.findOneByActivationKey(key)
            .map(user -> {
                // activate given user for the registration key.
                user.setActivated(true);
                user.setActivationKey(null);
                return user;
            }).orElseThrow(InvalidActivationKeyException::new);
    }

    @Override
    public void removeNotActivatedUsersBefore(final Instant date) {
        this.userRepository.findAllByActivatedIsFalseAndCreatedDateBefore(date)
            .forEach(this.userRepository::delete);
    }

    @Override
    public void remindPassword(final String email) {
        final User user = this.userRepository.findOneByEmailIgnoreCase(email).orElseThrow(UserNotFoundException::new);

        if (!user.isActivated()) {
            throw new UserNotActivatedException(
                String.format("We cannot change password, because account with %s is not activated", email)
            );
        }

        final String newPassword = RandomUtil.generatePassword();

        this.userService.changePassword(user, newPassword);
        this.mailServiceImpl.sendPasswordReminderEmail(user, newPassword);
    }

    @Override
    public User activate(final Long userId) {

        final String currentEmail = SecurityUtils.getCurrentUserLogin();

        return this.userRepository.findOneById(userId)
            .map(user -> {

                if (user.getEmail().equalsIgnoreCase(currentEmail)) {
                    throw new CannotActivateYouSelfException();
                }

                if (user.isActivated()) {
                    user.setActivated(false);
                    user.setActivationKey(RandomUtil.generateActivationKey());

                } else {
                    user.setActivated(true);
                    user.setActivationKey(null);
                }
                return user;
            }).orElseThrow(UserNotFoundException::new);
    }
}
