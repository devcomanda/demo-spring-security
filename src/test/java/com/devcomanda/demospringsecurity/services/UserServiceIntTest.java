package com.devcomanda.demospringsecurity.services;

import com.devcomanda.demospringsecurity.model.User;
import com.devcomanda.demospringsecurity.model.projections.ReadableUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import static com.devcomanda.demospringsecurity.factories.UsersFactory.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class UserServiceIntTest {

    @Autowired
    private UserService userService;

    @Test
    @WithMockUser("system@localhost")
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:database/empty.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:database/initialUsers.sql")
    public void shouldReturnCurrentUserWithUpdatedFields() {

        final String updatedFirstName = "updatedFirstName";
        final String updatedLastName = "updatedLastName";

        final User updatedUser = this.userService.updateUser(updatedFirstName, updatedLastName);

        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getFirstName()).isEqualTo(updatedFirstName);
        assertThat(updatedUser.getLastName()).isEqualTo(updatedLastName);

    }

    @Test
    @WithMockUser("system@localhost")
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:database/empty.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:database/initialUsers.sql")
    public void shouldReturnReadableUserWithAuthorities() {

        final ReadableUser actualUser = this.userService.loadReadableByEmail(
                USER_EMAIL
        ).orElseThrow(RuntimeException::new);

        assertThat(actualUser.getEmail()).isEqualTo(USER_EMAIL);
        assertThat(actualUser.getFirstName()).isEqualTo(USER_FIRSTNAME);
        assertThat(actualUser.getLastName()).isEqualTo(USER_LASTNAME);
    }
}
