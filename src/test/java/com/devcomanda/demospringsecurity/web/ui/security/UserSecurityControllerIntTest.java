package com.devcomanda.demospringsecurity.web.ui.security;


import com.devcomanda.demospringsecurity.factories.UsersFactory;
import com.devcomanda.demospringsecurity.model.User;
import com.devcomanda.demospringsecurity.repositories.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserSecurityControllerIntTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:database/empty.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:database/users/oneUserWithoutActivation.sql")
    public void shouldActivateUser() throws Exception {

        this.mvc.perform(
            get("/security/activate/{key}", UsersFactory.USER_ACTIVATION_KEY)
        )
            .andExpect(status().isOk());

        final User user = this.userRepository.findOneByEmailIgnoreCase(
            UsersFactory.USER_EMAIL
        ).orElse(null);

        assertThat(user).isNotNull();
        assertThat(user.isActivated()).isTrue();
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:database/empty.sql")
    public void shouldReturnErrorPageWhenKeyWrong() throws Exception {
        final String wrongActivationKey = "wrongKey";
        this.mvc.perform(
            get("/security/activate/{key}", wrongActivationKey)
        )
            .andExpect(status().isOk())
            .andExpect(view().name("security/errorActivation"));
    }
}
