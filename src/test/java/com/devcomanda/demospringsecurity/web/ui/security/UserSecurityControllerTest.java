package com.devcomanda.demospringsecurity.web.ui.security;

import com.devcomanda.demospringsecurity.exceptions.security.InvalidActivationKeyException;
import com.devcomanda.demospringsecurity.services.UserSecurityService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
@RunWith(SpringRunner.class)
@WebMvcTest(value = UserSecurityController.class, secure = false)
// Secure property is false because spring  boot loads auto-config without our security configuration
// Open issue -  https://github.com/spring-projects/spring-boot/issues/6514
public class UserSecurityControllerTest {

    @MockBean
    private UserSecurityService securityService;

    @Autowired
    private MockMvc mvc;

    @Test
    public void shouldDisplayPageWithSuccessResultAfterActivationUser() throws Exception {

        final String key = "activation-key";

        this.mvc.perform(
            get("/security/activate/{key}", key)
        )
            .andExpect(status().isOk())
            .andExpect(view().name("security/successfulActivation"));
    }

    @Test
    public void shouldDisplayPageWithErrorResultAfterActivationUser() throws Exception {

        final String key = "activation-key";

        when(this.securityService.activate(eq(key))).thenThrow(InvalidActivationKeyException.class);

        this.mvc.perform(
            get("/security/activate/{key}", key)
        )
            .andExpect(status().isOk())
            .andExpect(view().name("security/errorActivation"));
    }

}
