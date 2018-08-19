package com.devcomanda.demospringsecurity.services.impl;

import com.devcomanda.demospringsecurity.config.constants.AuthoritiesConstants;
import com.devcomanda.demospringsecurity.exceptions.CannotDeleteYouSelfException;
import com.devcomanda.demospringsecurity.exceptions.security.EmailAlreadyUsedException;
import com.devcomanda.demospringsecurity.exceptions.security.InvalidPasswordException;
import com.devcomanda.demospringsecurity.exceptions.security.UserNotFoundException;
import com.devcomanda.demospringsecurity.model.User;
import com.devcomanda.demospringsecurity.model.projections.ReadableUser;
import com.devcomanda.demospringsecurity.model.projections.UpdatableUser;
import com.devcomanda.demospringsecurity.repositories.AuthorityRepository;
import com.devcomanda.demospringsecurity.repositories.UserRepository;
import com.devcomanda.demospringsecurity.services.UserService;
import com.devcomanda.demospringsecurity.utils.RandomUtil;
import com.devcomanda.demospringsecurity.utils.SecurityUtils;
import com.devcomanda.demospringsecurity.web.api.requests.NewUserReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthorityRepository authorityRepository;

    @Autowired
    public UserServiceImpl(
            final UserRepository userRepository,
            final PasswordEncoder passwordEncoder,
            final AuthorityRepository authorityRepository
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authorityRepository = authorityRepository;
    }

    @Override
    public User createUser(final NewUserReq req, boolean isActivated) {

        if (!SecurityUtils.checkPasswordLength(req.getPassword())) {
            throw new InvalidPasswordException();
        }

        this.userRepository.findOneByEmailIgnoreCase(req.getEmail())
                .ifPresent(u -> {
                    throw new EmailAlreadyUsedException();
                });

        final User user = new User();

        user.setEmail(req.getEmail());
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setActivated(false);
        user.setActivationKey(RandomUtil.generateActivationKey());

        this.authorityRepository.findById(AuthoritiesConstants.ROLE_USER)
                .ifPresent(user::addAuthority);

        if (req.getPassword() == null || req.getPassword().isEmpty()) {
            final String rawPassword = RandomUtil.generatePassword();
            final String encryptedPassword = this.passwordEncoder.encode(rawPassword);
            user.setPassword(encryptedPassword);
        } else {
            final String encryptedPassword = this.passwordEncoder.encode(req.getPassword());
            user.setPassword(encryptedPassword);
        }


        if (isActivated) {
            user.setActivated(true);
        } else {
            user.setActivated(false);
            user.setActivationKey(RandomUtil.generateActivationKey());
        }

        return this.userRepository.save(user);
    }

    @Override
    public User updateUser(final String firstName, final String lastName) {
        final User user =
                this.userRepository.findOneByEmailIgnoreCase(SecurityUtils.getCurrentUserLogin())
                        .map(u -> {
                            u.setFirstName(firstName);
                            u.setLastName(lastName);
                            return u;
                        })
                        .orElseThrow(UserNotFoundException::new);
        return this.userRepository.save(user);
    }

    @Override
    public void deleteUser(final Long userId) {
        final User user = this.userRepository.findOneById(userId)
                .orElseThrow(UserNotFoundException::new);

        final String currentEmail = SecurityUtils.getCurrentUserLogin();

        if (user.getEmail().equalsIgnoreCase(currentEmail)) {
            throw new CannotDeleteYouSelfException();
        }

        this.userRepository.delete(user);
    }

    @Override
    public User updateUser(final UpdatableUser updatedUser) {
        final User user = this.userRepository
                .findById(updatedUser.getId())
                .map(u -> {
                    u.setFirstName(updatedUser.getFirstName());
                    u.setLastName(updatedUser.getLastName());
                    u.setEmail(updatedUser.getEmail());
                    return u;
                }).orElseThrow(UserNotFoundException::new);
        return this.userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UpdatableUser> loadUpdatableUserById(final Long userId) {
        return this.userRepository.findAsUpdatableUserById(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ReadableUser> loadUserById(final Long userId) {
        return this.userRepository.findOneReadableById(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public User loadUserByEmail(final String email) {
        return this.userRepository.findOneByEmailIgnoreCase(email)
                .orElseThrow(UserNotFoundException::new);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReadableUser> loadUsersByAuthority(final String authority) {
        return this.userRepository.findAsReadableByAuthority(authority);
    }

    @Override
    public void changePassword(final User user, final String newPassword) {
        final String encryptedPassword = this.passwordEncoder.encode(newPassword);
        user.setPassword(encryptedPassword);
        this.userRepository.save(user);
    }

    @Override
    public Optional<ReadableUser> loadReadableByEmail(String email) {
       return this.userRepository.findAsReadableByEmailIgnoreCase(email);
    }
}
