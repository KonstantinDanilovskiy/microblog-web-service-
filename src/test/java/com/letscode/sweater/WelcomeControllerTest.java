package com.letscode.sweater;

import com.letscode.sweater.controller.WelcomeController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@WithUserDetails("testUser1")
@TestPropertySource("/application-test.properties")
@Sql(value = "/data.sql")
public class WelcomeControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WelcomeController welcomeController;

    @Test
    public void getMainPage_thenRedirectToLogin() throws Exception {
        mockMvc.perform(get("/main"))
                .andExpect(authenticated())
                .andExpect(xpath("//*[@id='navbarSupportedContent']/div/div[1]").string("testUser1"));
    }

    @Test
    public void getMyMessagePage_thenCountMessage() throws Exception {
        mockMvc.perform(get("/message/testUser1"))
                .andExpect(authenticated())
                .andExpect(xpath("//*[@id='messageListCards']/div").nodeCount(3));
    }
}
