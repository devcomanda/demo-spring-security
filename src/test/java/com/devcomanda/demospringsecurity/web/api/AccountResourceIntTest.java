package com.devcomanda.demospringsecurity.web.api;

import com.devcomanda.demospringsecurity.factories.UsersFactory;
import com.devcomanda.demospringsecurity.utils.TestUtils;
import com.devcomanda.demospringsecurity.web.api.requests.UpdateUserReq;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static com.devcomanda.demospringsecurity.factories.UsersFactory.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class AccountResourceIntTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @WithMockUser("system@localhost")
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:database/empty.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:database/users/oneUser.sql")
    public void shouldReturnExistingAccount() throws Exception {
        this.mvc.perform(get("/api/account")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.email").value(USER_EMAIL))
                .andExpect(jsonPath("$.firstName").value(USER_FIRSTNAME))
                .andExpect(jsonPath("$.lastName").value(USER_LASTNAME));
    }

    @Test
    @WithMockUser("user@localhost")
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:database/empty.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:database/users/oneUser.sql")
    public void shouldReturnInternalServerErrorWhenUnknownAccount() throws Exception {
        this.mvc.perform(get("/api/account")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser("system@localhost")
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:database/empty.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:database/users/oneUser.sql")
    public void shouldUpdateCurrentUserFields() throws Exception {
        final UpdateUserReq updateUserReq = UsersFactory.createUpdateUserReq();

        this.mvc.perform(
                post("/api/account")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(TestUtils.convertObjectToJsonBytes(updateUserReq))
        )
                .andExpect(status().isOk());
    }
}
