package com.devcomanda.demospringsecurity.repositories;


import com.devcomanda.demospringsecurity.config.constants.AuthoritiesConstants;
import com.devcomanda.demospringsecurity.factories.UsersFactory;
import com.devcomanda.demospringsecurity.model.User;
import com.devcomanda.demospringsecurity.model.projections.ReadableUser;
import com.devcomanda.demospringsecurity.model.projections.UpdatableUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static com.devcomanda.demospringsecurity.factories.UsersFactory.USER_ID;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
@RunWith(SpringRunner.class)
@DataJpaTest
public class UserRepositoryIntTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            scripts = {"classpath:database/empty.sql", "classpath:database/users/oneUser.sql"})
    public void shouldReturnUserById() {

        final Optional<User> user = this.userRepository.findById(USER_ID);

        assertThat(user)
                .isPresent()
                .containsInstanceOf(User.class);

        final User actualUser = user.get();
        assertThat(actualUser.getEmail()).isEqualToIgnoringCase("system@localhost");
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:database/empty.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:database/initialUsers.sql")
    public void shouldReturnReadableUserByEmail() {

        final Optional<ReadableUser> user =
                this.userRepository.findAsReadableByEmailIgnoreCase(UsersFactory.USER_EMAIL);

        assertThat(user)
                .isPresent()
                .containsInstanceOf(ReadableUser.class);

        final ReadableUser actualUser = user.get();
        assertThat(actualUser.getEmail()).isEqualToIgnoringCase(UsersFactory.USER_EMAIL);
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:database/empty.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:database/initialUsers.sql")
    public void shouldReturnListReadableUsersByRole() {
        final List<ReadableUser> users = this.userRepository.findAsReadableByAuthority(AuthoritiesConstants.ROLE_ADMIN);
        assertThat(users)
                .isNotNull()
                .hasSize(1);

        final ReadableUser user = users.get(0);
        assertThat(user.getEmail()).isEqualTo(UsersFactory.USER_EMAIL);
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:database/empty.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:database/initialUsers.sql")
    public void shouldReturnEmptyListReadableUsersByRoleIfUserNotFound() {
        final String invalidRole = "invalidRole";

        final List<ReadableUser> users = this.userRepository
                .findAsReadableByAuthority(invalidRole);

        assertThat(users)
                .isNotNull()
                .hasSize(0);
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:database/empty.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:database/initialUsers.sql")
    public void shouldReturnUpdatableUser() {

        final Optional<UpdatableUser> user = this.userRepository.findAsUpdatableUserById(USER_ID);

        assertThat(user).isPresent();
        assertThat(user.get().getFirstName()).isEqualTo(UsersFactory.USER_FIRSTNAME);
        assertThat(user.get().getLastName()).isEqualTo(UsersFactory.USER_LASTNAME);
        assertThat(user.get().getEmail()).isEqualTo(UsersFactory.USER_EMAIL);
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:database/empty.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:database/initialUsers.sql")
    public void shouldReturnReadableUserById() {

        final Optional<ReadableUser> user =
                this.userRepository.findOneReadableById(USER_ID);

        assertThat(user).isPresent().containsInstanceOf(ReadableUser.class);

        final ReadableUser actualUser = user.get();

        assertThat(actualUser.getEmail()).isEqualToIgnoringCase(UsersFactory.USER_EMAIL);
    }
}
