package com.devcomanda.demospringsecurity.web.api.security;

import com.devcomanda.demospringsecurity.factories.UsersFactory;
import com.devcomanda.demospringsecurity.model.User;
import com.devcomanda.demospringsecurity.repositories.UserRepository;
import com.devcomanda.demospringsecurity.utils.TestUtils;
import com.devcomanda.demospringsecurity.web.api.requests.NewUserReq;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class UserSecurityResourceIntTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mvc;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:database/empty.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:database/authorities/twoAuthorities.sql")
    public void shouldRegisterUser() throws Exception {

        final NewUserReq userReq = UsersFactory.createNewUserReq();

        this.mvc.perform(
            post("/api/security/public/register")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(TestUtils.convertObjectToJsonBytes(userReq)))
            .andExpect(status().isCreated());

        final Optional<User> user = this.userRepository.findOneByEmailIgnoreCase(UsersFactory.USER_EMAIL);
        assertThat(user.isPresent()).isTrue();
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:database/empty.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:database/initialUsers.sql")
    public void shouldReturnBadReqWhenRegisterUserWithDuplicateEmail() throws Exception {

        final NewUserReq userReq = UsersFactory.createNewUserReq();

        this.mvc.perform(
            post("/api/security/public/register")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(TestUtils.convertObjectToJsonBytes(userReq))
        )
            .andExpect(status().isBadRequest());
    }
}
