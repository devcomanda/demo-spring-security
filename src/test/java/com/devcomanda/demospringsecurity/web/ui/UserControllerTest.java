package com.devcomanda.demospringsecurity.web.ui;

import com.devcomanda.demospringsecurity.config.constants.AuthoritiesConstants;
import com.devcomanda.demospringsecurity.factories.UsersFactory;
import com.devcomanda.demospringsecurity.model.User;
import com.devcomanda.demospringsecurity.model.projections.ReadableUser;
import com.devcomanda.demospringsecurity.model.projections.UpdatableUser;
import com.devcomanda.demospringsecurity.services.UserSecurityService;
import com.devcomanda.demospringsecurity.services.UserService;
import com.devcomanda.demospringsecurity.web.api.requests.NewUserReq;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static com.devcomanda.demospringsecurity.factories.UsersFactory.createReadableUser;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserSecurityService securityService;

    @Test
    @WithMockUser("system@localhost")
    public void shouldDisplayListUsersByRole() throws Exception {

        final ReadableUser user =createReadableUser();

        when(this.userService.loadUsersByAuthority(eq(AuthoritiesConstants.ROLE_ADMIN))).thenReturn(Collections.singletonList(user));

        this.mvc.perform(
            get("/admin/users")
                .param("authority", AuthoritiesConstants.ROLE_ADMIN)
        )
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("users"))
            .andExpect(model().attribute("users", hasItem(user)))
            .andExpect(view().name("users/listUsers"));
    }

    @Test
    @WithMockUser("system@localhost")
    public void shouldDisplayListUsersByRoleIfRoleLowerCase() throws Exception {

        final ReadableUser user = createReadableUser();

        when(this.userService.loadUsersByAuthority(eq(AuthoritiesConstants.ROLE_ADMIN))).thenReturn(Collections.singletonList(user));

        this.mvc.perform(
            get("/admin/users")
                .param("authority", "role_admin")
        )
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("users"))
            .andExpect(model().attribute("users", hasItem(user)))
            .andExpect(view().name("users/listUsers"));
    }

    @Test
    @WithMockUser("system@localhost")
    public void shouldDisplayListUsersByRoleIfRoleNotSet() throws Exception {

        final ReadableUser user = createReadableUser();

        when(this.userService.loadUsersByAuthority(eq(AuthoritiesConstants.ROLE_USER)))
            .thenReturn(Collections.singletonList(user));

        this.mvc.perform(
            get("/admin/users")
        )
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("users"))
            .andExpect(model().attribute("users", hasItem(user)))
            .andExpect(view().name("users/listUsers"));
    }

    @Test
    public void shouldReturnUnauthErrorIfUserNotAuth() throws Exception {
        this.mvc.perform(get("/admin/users"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser("system@localhost")
    public void shouldDisplayFormForCreateNewUserForAuthUser() throws Exception {
        this.mvc.perform(get("/admin/users/new"))
            .andExpect(status().isOk())
            .andExpect(view().name("users/formCreateUser"))
            .andExpect(model().attributeExists("newUser"));

    }

    @Test
    @WithMockUser("system@localhost")
    public void shouldDisplayDetailsUserPageAfterSuccessfulCreationUser() throws Exception {

        final NewUserReq req = UsersFactory.createNewUserReq();
        final User user = UsersFactory.createActivatedUserEntity();
        final boolean isActivate = true;

        when(this.userService.createUser(any(NewUserReq.class), eq(isActivate))).thenReturn(user);

        this.mvc.perform(
            post("/admin/users/new")
                .param("firstName", req.getFirstName())
                .param("lastName", req.getLastName())
                .param("email", req.getEmail())
                .param("password", req.getPassword())
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/users/1"));

    }

    @Test
    @WithMockUser("system@localhost")
    public void shouldDisplayDetailsUserPage() throws Exception {

        final ReadableUser user = createReadableUser();

        when(this.userService.loadUserById(eq(UsersFactory.USER_ID)))
            .thenReturn(Optional.of(user));

        this.mvc.perform(
            get("/admin/users/{userId}", UsersFactory.USER_ID)
        )
            .andExpect(status().isOk())
            .andExpect(view().name("users/userDetails"));
    }

    @Test
    @WithMockUser("system@localhost")
    public void shouldDisplayUpdateUserPage() throws Exception {

        final UpdatableUser user = UsersFactory.createUpdatableUser();

        when(this.userService.loadUpdatableUserById(eq(UsersFactory.USER_ID)))
            .thenReturn(Optional.of(user));

        this.mvc.perform(
            get("/admin/users/{userId}/update", UsersFactory.USER_ID)
        )
            .andExpect(status().isOk())
            .andExpect(view().name("users/formUpdateUser"))
            .andExpect(model().attributeExists("user"));
    }

    @Test
    @WithMockUser("system@localhost")
    public void shouldDisplayDetailsUserPageAfterSuccessfulUpdatingUser() throws Exception {
        final User user = UsersFactory.createActivatedUserEntity();
        when(this.userService.updateUser(any(UpdatableUser.class))).thenReturn(user);

        this.mvc.perform(
            post("/admin/users/{userId}/update", UsersFactory.USER_ID)
                .param("id", String.valueOf(UsersFactory.USER_ID))
                .param("firstName", UsersFactory.UPDATED_USER_FIRSTNAME)
                .param("lastName", UsersFactory.UPDATED_USER_LASTNAME)
                .param("email", UsersFactory.UPDATED_USER_EMAIL)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/users/1"));
    }

    @Test
    @WithMockUser("system@localhost")
    public void shouldRemoveUserById() throws Exception {

        this.mvc.perform(
            post("/admin/users/{userId}/delete", UsersFactory.USER_ID)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/users"));
    }

    @Test
    @WithMockUser("system@localhost")
    public void shouldDisplayUserDetailsPage() throws Exception {
        final ReadableUser user = createReadableUser();

        when(this.userService.loadUserById(eq(UsersFactory.USER_ID)))
            .thenReturn(Optional.of(user));

        this.mvc.perform(
            get("/admin/users/{userId}", UsersFactory.USER_ID)
        )
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("user"))
            .andExpect(view().name("users/userDetails"));
    }

    @Test
    @WithMockUser("system@localhost")
    public void shouldReturnBadRequestWhenDisplayUserDetailsPageIfUserNotFound() throws Exception {
        when(this.userService.loadUserById(eq(UsersFactory.USER_ID)))
            .thenReturn(Optional.empty());

        this.mvc.perform(
            get("/admin/users/{userId}", UsersFactory.USER_ID)
        )
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser("system@localhost")
    public void shouldDisplayUserDetailsPageAfterSuccessfulActivationUser() throws Exception {

        this.mvc.perform(
            post("/admin/users/{userId}/activate", UsersFactory.USER_ID)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/users/" + UsersFactory.USER_ID));

        verify(this.securityService).activate(UsersFactory.USER_ID);
    }
}
