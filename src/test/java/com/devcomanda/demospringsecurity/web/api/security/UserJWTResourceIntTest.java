package com.devcomanda.demospringsecurity.web.api.security;

import com.devcomanda.demospringsecurity.utils.TestUtils;
import com.devcomanda.demospringsecurity.web.api.requests.LoginReq;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:database/empty.sql")
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:database/initialUsers.sql")
public class UserJWTResourceIntTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void shouldReturnTokenAfterSuccessfulAuthorize() throws Exception {
        final LoginReq login = new LoginReq();
        login.setEmail("system@localhost");
        login.setPassword("user");

        this.mvc.perform(post("/api/security/public/authenticate")
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(TestUtils.convertObjectToJsonBytes(login)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id_token").isString())
            .andExpect(jsonPath("$.id_token").isNotEmpty())
            .andExpect(header().string("Authorization", not(nullValue())))
            .andExpect(header().string("Authorization", not(isEmptyString())));
    }

    @Test
    public void shouldReturnTokenAfterSuccessfulAuthorizeWithRememberMe() throws Exception {
        final LoginReq login = new LoginReq();
        login.setEmail("system@localhost");
        login.setPassword("user");
        login.setRememberMe(true);

        this.mvc.perform(post("/api/security/public/authenticate")
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(TestUtils.convertObjectToJsonBytes(login)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id_token").isString())
            .andExpect(jsonPath("$.id_token").isNotEmpty())
            .andExpect(header().string("Authorization", not(nullValue())))
            .andExpect(header().string("Authorization", not(isEmptyString())));
    }

    @Test
    public void shouldReturnUnathorizatedStatusAfterAuthorizeFails() throws Exception {
        final LoginReq login = new LoginReq();
        login.setEmail("wrong-user");
        login.setPassword("wrong password");

        this.mvc.perform(post("/api/security/authenticate")
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(TestUtils.convertObjectToJsonBytes(login)))
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.id_token").doesNotExist())
            .andExpect(header().doesNotExist("Authorization"));
    }

    @Test
    @WithMockUser("admin")
    public void shouldReturnAuthenticatedUser() throws Exception {
        this.mvc.perform(
            get("/api/security/public/authenticate")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(content().string("admin"));
    }

    @Test
    public void shouldReturnUnauthorizedWhenNonAuthenticatedUser() throws Exception {
        this.mvc.perform(get("/api/security/public/authenticate")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(""));
    }
}
