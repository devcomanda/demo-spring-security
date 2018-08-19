package com.devcomanda.demospringsecurity.services;


import com.devcomanda.demospringsecurity.exceptions.CannotDeleteYouSelfException;
import com.devcomanda.demospringsecurity.exceptions.security.UserNotFoundException;
import com.devcomanda.demospringsecurity.factories.AuthoritiesFactory;
import com.devcomanda.demospringsecurity.factories.UsersFactory;
import com.devcomanda.demospringsecurity.model.User;
import com.devcomanda.demospringsecurity.model.projections.ReadableUser;
import com.devcomanda.demospringsecurity.model.projections.UpdatableUser;
import com.devcomanda.demospringsecurity.repositories.AuthorityRepository;
import com.devcomanda.demospringsecurity.repositories.UserRepository;
import com.devcomanda.demospringsecurity.services.impl.UserServiceImpl;
import com.devcomanda.demospringsecurity.web.api.requests.NewUserReq;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.devcomanda.demospringsecurity.config.constants.AuthoritiesConstants.ROLE_ADMIN;
import static com.devcomanda.demospringsecurity.config.constants.AuthoritiesConstants.ROLE_USER;
import static com.devcomanda.demospringsecurity.factories.UsersFactory.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
public class UserServiceTest {

    @Mock
    private AuthorityRepository authorityRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserService userService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        this.userService = new UserServiceImpl(
            this.userRepository,
            this.passwordEncoder,
            this.authorityRepository
        );
    }

    @Test
    public void shouldReturnUserAfterSuccessfulRegistration() {

        final NewUserReq userReq = createNewUserReq();

        final String encodedPassword = "has-password";
        when(this.passwordEncoder.encode(any())).thenReturn(encodedPassword);

        when(this.authorityRepository.findById(eq(ROLE_USER)))
            .thenReturn(Optional.of(AuthoritiesFactory.USER_AUTHORITY));

        doAnswer(AdditionalAnswers.returnsFirstArg()).when(this.userRepository)
            .save(any(User.class));

        final User user = this.userService.createUser(userReq, true);

        assertThat(user.getEmail()).isEqualTo(USER_EMAIL);
        assertThat(user.getPassword()).isEqualTo(encodedPassword);
        assertThat(user.getFirstName()).isEqualTo(USER_FIRSTNAME);
        assertThat(user.getLastName()).isEqualTo(USER_LASTNAME);

        assertThat(user.getAuthorities())
            .hasSize(1)
            .containsExactlyInAnyOrder(
                AuthoritiesFactory.USER_AUTHORITY
            );
    }

    @Test
    public void shouldReturnUpdatableUserAfterSuccessfulFindingById() {

        final UpdatableUser user = UsersFactory.createUpdatableUser();
        when(this.userRepository.findAsUpdatableUserById(eq(USER_ID)))
            .thenReturn(Optional.of(user));

        final Optional<UpdatableUser> actualUser = this.userService
            .loadUpdatableUserById(USER_ID);

        assertThat(actualUser).isPresent();
        assertThat(actualUser).isNotNull();
    }

    @Test
    public void shouldRemoveUserById() {

        final User user = createActivatedUserEntity();
        final long userId = 2L;
        user.setId(userId);

        when(this.userRepository.findOneById(eq(userId)))
            .thenReturn(Optional.of(user));

        this.userService.deleteUser(userId);

        verify(this.userRepository).delete(eq(user));
    }

    @Test(expected = UserNotFoundException.class)
    public void shouldThrowUserNotFoundExceptionWhenRemoveUserIfUserNotFound() {
        when(this.userRepository.findOneById(eq(USER_ID)))
            .thenReturn(Optional.empty());
        this.userService.deleteUser(USER_ID);
    }

    @Test(expected = CannotDeleteYouSelfException.class)
    @WithMockUser("system@localhost")
    @Ignore
    //Need to fix this case. We should extract SecurityUtils and mock it
    // that helps to do the test without spring
    public void shouldThrowCannotDeleteYourSelfExceptionWhenRemoveUserIfUserIsSame() {
        when(this.userRepository.findOneById(eq(USER_ID)))
            .thenReturn(Optional.of(createActivatedUserEntity()));
        this.userService.deleteUser(USER_ID);
    }

    @Test
    public void shouldReturnReadableUserById() {

        final ReadableUser user = UsersFactory.createReadableUser();

        when(this.userRepository.findOneReadableById(eq(USER_ID)))
            .thenReturn(Optional.of(user));

        final Optional<ReadableUser> actualUser = this.userService.loadUserById(USER_ID);

        assertThat(actualUser).isPresent();
    }

    @Test
    public void shouldReturnUserByEmail() {

        final User user = createActivatedUserEntity();
        when(this.userRepository.findOneByEmailIgnoreCase(USER_EMAIL))
            .thenReturn(Optional.of(user));

        final User actualUser = this.userService.loadUserByEmail(USER_EMAIL);

        assertThat(actualUser).isNotNull().isEqualTo(user);
    }

    @Test
    public void shouldReturnListUsersByRole() {
        final ReadableUser user = UsersFactory.createReadableUser();

        when(this.userRepository.findAsReadableByAuthority(eq(ROLE_ADMIN)))
            .thenReturn(Collections.singletonList(user));

        final List<ReadableUser> users = this.userService
            .loadUsersByAuthority(ROLE_ADMIN);

        assertThat(users)
            .isNotNull()
            .hasSize(1);

        final ReadableUser actualUser = users.get(0);

        assertThat(actualUser.getEmail()).isEqualTo(USER_EMAIL);
    }

    @Test
    public void shouldChangePasswordForUser() {

        final User user = createActivatedUserEntity();

        final String password = "password";
        final String encodedPassword = "encodedPassword";
        when(this.passwordEncoder.encode(eq(password))).thenReturn(encodedPassword);

        final ArgumentCaptor<User> captorUser = ArgumentCaptor.forClass(User.class);

        this.userService.changePassword(user, password);

        verify(this.userRepository)
            .save(captorUser.capture());

        final User capturedUser = captorUser.getValue();
        assertThat(capturedUser.getPassword()).isEqualTo(encodedPassword);
    }
}
