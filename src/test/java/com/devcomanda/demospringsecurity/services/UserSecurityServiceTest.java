package com.devcomanda.demospringsecurity.services;


import com.devcomanda.demospringsecurity.exceptions.CannotActivateYouSelfException;
import com.devcomanda.demospringsecurity.exceptions.security.InvalidActivationKeyException;
import com.devcomanda.demospringsecurity.exceptions.security.UserNotActivatedException;
import com.devcomanda.demospringsecurity.exceptions.security.UserNotFoundException;
import com.devcomanda.demospringsecurity.factories.UsersFactory;
import com.devcomanda.demospringsecurity.model.User;
import com.devcomanda.demospringsecurity.repositories.UserRepository;
import com.devcomanda.demospringsecurity.services.impl.MailServiceImpl;
import com.devcomanda.demospringsecurity.services.impl.UserSecurityServiceImpl;
import com.devcomanda.demospringsecurity.web.api.requests.NewUserReq;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;

import static com.devcomanda.demospringsecurity.factories.UsersFactory.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
public class UserSecurityServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private MailServiceImpl mailService;

    private UserSecurityService securityService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.securityService = new UserSecurityServiceImpl(
            this.userRepository,
            this.userService,
            this.mailService
        );
    }

    @Test
    public void shouldRemoveNotActivatedUsers() {
        final User user = createActivatedUserEntity();
        when(this.userRepository.findAllByActivatedIsFalseAndCreatedDateBefore(any(Instant.class)))
            .thenReturn(Collections.singletonList(user));

        this.securityService.removeNotActivatedUsersBefore(Instant.now());

        verify(this.userRepository).delete(eq(user));
    }

    @Test
    public void shouldReturnUserAfterSuccessfulRegistrationNewUser() {

        final User user = UsersFactory.createNotActivatedUserEntity();
        final String encodedPassword = "has-password";
        user.setPassword(encodedPassword);

        final boolean isActivated = false;
        when(this.userService.createUser(any(NewUserReq.class), eq(isActivated))).thenReturn(user);

        final NewUserReq req = UsersFactory.createNewUserReq();

        final User actualUser = this.securityService.register(req);

        assertThat(actualUser).isNotNull();
        assertThat(actualUser.getEmail()).isEqualTo(USER_EMAIL);
        assertThat(actualUser.getPassword()).isEqualTo(encodedPassword);

        assertThat(actualUser.isActivated()).isFalse();
        assertThat(actualUser.getActivationKey()).isEqualTo(USER_ACTIVATION_KEY);

        verify(this.mailService).sendActivationEmail(eq(user));

    }

    @Test
    public void shouldGenerateNewPassWordAndSendItToUser() {
        final User user = createActivatedUserEntity();
        final String email = "valid@email.com";

        when(this.userRepository.findOneByEmailIgnoreCase(eq(email))).thenReturn(Optional.of(user));

        this.securityService.remindPassword(email);

        verify(this.mailService).sendPasswordReminderEmail(eq(user), any(String.class));
        verify(this.userService).changePassword(eq(user), any(String.class));

    }

    @Test(expected = UserNotFoundException.class)
    public void shouldThrowUserMotFoundExceptionWhenRemindPasswordAndUserNotFound() {
        final String email = "invalid@email.com";
        when(this.userRepository.findOneByEmailIgnoreCase(eq(email))).thenReturn(Optional.empty());
        this.securityService.remindPassword(email);
    }

    @Test(expected = UserNotActivatedException.class)
    public void shouldThrowUserNotActivatedExceptionIfUserNotActivate() {
        final String email = "invalid@email.com";

        when(this.userRepository.findOneByEmailIgnoreCase(eq(email)))
            .thenReturn(Optional.of(UsersFactory.createNotActivatedUserEntity()));

        this.securityService.remindPassword(email);
    }

    @Test
    public void shouldActivateUserIfActivationKeyIsPresent() {

        when(this.userRepository.findOneByActivationKey(eq(USER_ACTIVATION_KEY)))
            .thenReturn(Optional.of(UsersFactory.createNotActivatedUserEntity()));

        final User actualUser = this.securityService.activate(USER_ACTIVATION_KEY);

        assertThat(actualUser).isNotNull();
        assertThat(actualUser.isActivated()).isTrue();
        assertThat(actualUser.getActivationKey()).isNull();
    }

    @Test(expected = InvalidActivationKeyException.class)
    public void shouldThrowExceptionIfActivationNotFound() {

        when(this.userRepository.findOneByActivationKey(eq(USER_ACTIVATION_KEY)))
            .thenReturn(Optional.empty());

        this.securityService.activate(USER_ACTIVATION_KEY);
    }

    @Test
    public void shouldActivateUserByIdIfUserNotActivated() {

        when(this.userRepository.findOneById(eq(USER_ID)))
            .thenReturn(Optional.of(UsersFactory.createNotActivatedUserEntity()));

        final User user = this.securityService.activate(USER_ID);

        assertThat(user).isNotNull();
        assertThat(user.isActivated()).isTrue();
        assertThat(user.getActivationKey()).isNull();
    }

    @Test
    public void shouldDeactivateUserByIdIfUserActivated() {

        when(this.userRepository.findOneById(eq(USER_ID)))
            .thenReturn(Optional.of(createActivatedUserEntity()));

        final User user = this.securityService.activate(USER_ID);

        assertThat(user).isNotNull();
        assertThat(user.isActivated()).isFalse();
        assertThat(user.getActivationKey()).isNotEmpty();
    }

    @Test(expected = UserNotFoundException.class)
    public void shouldThrowUserNotFoundExceptionWhenUserNotFoundWhenActivateUser() {

        when(this.userRepository.findOneById(eq(USER_ID)))
            .thenReturn(Optional.empty());

        this.securityService.activate(USER_ID);
    }

    @Test(expected = CannotActivateYouSelfException.class)
    @WithMockUser("system@localhost")
    @Ignore
    //Need to fix this case. We should extract SecurityUtils and mock it
    // that helps to do the test without spring
    public void shouldThrowCannotDeleteYourSelfExceptionWhenRemoveUserIfUserIsSame() {

        when(this.userRepository.findOneById(eq(USER_ID)))
            .thenReturn(Optional.of(createActivatedUserEntity()));

        this.securityService.activate(USER_ID);
    }

    //TODO need to add tests to all other methods
}
