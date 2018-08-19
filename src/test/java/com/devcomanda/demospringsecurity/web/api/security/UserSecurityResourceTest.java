package com.devcomanda.demospringsecurity.web.api.security;

import com.devcomanda.demospringsecurity.exceptions.security.UserNotFoundException;
import com.devcomanda.demospringsecurity.factories.UsersFactory;
import com.devcomanda.demospringsecurity.services.UserSecurityService;
import com.devcomanda.demospringsecurity.utils.TestUtils;
import com.devcomanda.demospringsecurity.web.api.requests.NewUserReq;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Transactional
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:database/empty.sql")
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:database/initialUsers.sql")
public class UserSecurityResourceTest {

    @MockBean
    private UserSecurityService securityService;

    @Autowired
    private MockMvc mvc;

    @Test
    public void shouldReturnBadRequestErrorWhenRegisterProcessThrowsException() throws Exception {
        final NewUserReq req = UsersFactory.createNewUserReq();

        when(this.securityService.register(any())).thenThrow(RuntimeException.class);

        this.mvc.perform(
            post("/api/security/public/register")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(TestUtils.convertObjectToJsonBytes(req)))
            .andExpect(status().isBadRequest());

    }

    @Test
    public void shouldReturnBadRequestIfUserIsNotValid() throws Exception {
        final NewUserReq req = UsersFactory.createNewUserReq();

        req.setEmail("invalid");
        req.setFirstName("");
        req.setLastName("");

        this.mvc.perform(
            post("/api/security/public/register")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(TestUtils.convertObjectToJsonBytes(req))
        )
            .andExpect(status().isBadRequest());

    }


    @Test
    @WithMockUser("system@localhost")
    public void shouldSendReminderPasswordEmailForAuthUser() throws Exception {
        final String email = "valid@email.com";

        this.mvc.perform(
            get("/api/security/public/remind-password")
                .param("email", email)
        )
            .andExpect(status().isOk());
    }

    @Test
    public void shouldSendReminderPasswordEmailIfUserNotAuth() throws Exception {
        final String email = "valid@email.com";

        this.mvc.perform(
            get("/api/security/public/remind-password")
                .param("email", email)
        )
            .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnErrorIfUserNotFoundWhenWhenPasswordReminderProcess() throws Exception {

        final String email = "invalid@email.com";
        doThrow(new UserNotFoundException())
                .when(this.securityService).remindPassword(eq(email));

        this.mvc.perform(
            get("/api/security/public/remind-password")
                .param("email", email)
        )
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Something went wrong :(\n Message:\n User not found"));
    }
}
