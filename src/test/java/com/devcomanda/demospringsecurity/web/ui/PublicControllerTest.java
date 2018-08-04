package com.devcomanda.demospringsecurity.web.ui;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
@RunWith(SpringRunner.class)
@WebMvcTest(PublicController.class)
@WithMockUser("user")
public class PublicControllerTest {
    @Autowired
    private MockMvc mvc;

    @Test
    public void shouldDisplayListDemoModels() throws Exception {

        this.mvc.perform(
                get("/public/demomodels")
        )
                .andExpect(status().isOk())
                .andExpect(view().name("public/listDemoModels"))
                .andDo(print());

    }
}
