package com.letscode.sweater;

import com.letscode.sweater.controller.WelcomeController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SweaterApplicationTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WelcomeController welcomeController;

    @Test
    public void contextLoads() throws Exception {
        assertThat(welcomeController).isNotNull();
    }

    @Test
    public void getWelcomePage() throws Exception {
       mockMvc.perform(get("/"))
               .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Welcome!")));
    }

    @Test
    public void getMainPage_thenRedirectToLogin() throws Exception {
        mockMvc.perform(get("/main"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    public void correctLogin_thenRedirectToWelcomePage() throws Exception {
        mockMvc.perform(formLogin().user("Yana").password("1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    public void badCredentialsLogin() throws Exception {
        mockMvc.perform(post("/login").param("user","Anderson"))
                .andExpect(status().isForbidden());
    }
}
