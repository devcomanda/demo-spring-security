package com.devcomanda.demospringsecurity.web.api;


import com.devcomanda.demospringsecurity.factories.UsersFactory;
import com.devcomanda.demospringsecurity.model.User;
import com.devcomanda.demospringsecurity.model.projections.ReadableUser;
import com.devcomanda.demospringsecurity.services.UserService;
import com.devcomanda.demospringsecurity.utils.TestUtils;
import com.devcomanda.demospringsecurity.web.api.requests.UpdateUserReq;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static com.devcomanda.demospringsecurity.factories.UsersFactory.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
@RunWith(SpringRunner.class)
@WebMvcTest(AccountResource.class)
public class AccountResourceTest {

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mvc;

    @Test
    @WithMockUser("system@localhost")
    public void shouldReturnExistingAccount() throws Exception {
        final ReadableUser user = createReadableUser();
        when(this.userService.loadReadableByEmail(eq(USER_EMAIL)))
                .thenReturn(Optional.of(user));

        this.mvc.perform(get("/api/account")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.email").value(USER_EMAIL))
            .andExpect(jsonPath("$.firstName").value(USER_FIRSTNAME))
            .andExpect(jsonPath("$.lastName").value(USER_LASTNAME))
            .andDo(print());
    }

    @Test
    @WithMockUser("system@localhost")
    public void shouldReturnInternalServerErrorWhenUnknownAccount() throws Exception {

        when(this.userService.loadReadableByEmail(eq(USER_EMAIL)))
                .thenReturn(Optional.empty());

        this.mvc.perform(get("/api/account")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser("system@localhost")
    public void shouldUpdateCurrentUserFields() throws Exception {
        final User user = UsersFactory.createActivatedUserEntity();
        user.setFirstName(UPDATED_USER_FIRSTNAME);
        user.setLastName(UPDATED_USER_LASTNAME);

        when(this.userService.updateUser(eq(UPDATED_USER_FIRSTNAME), eq(UPDATED_USER_LASTNAME)))
            .thenReturn(user);

        final UpdateUserReq updateUserReq = UsersFactory.createUpdateUserReq();

        this.mvc.perform(
            post("/api/account")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(TestUtils.convertObjectToJsonBytes(updateUserReq))

                //It's fake setting because in production env we disable it
                .with(csrf())
        )
            .andExpect(status().isOk());
    }
}
