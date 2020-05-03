package com.letscode.sweater.service;


import com.letscode.sweater.entity.User;
import com.letscode.sweater.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import static junit.framework.TestCase.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceImplTest {
    @Autowired
    private UserService userService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private MailSender mailSender;
    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    public void getByUsername() {
    }

    @Test
    public void getByUserId() {
    }

    @Test
    public void getAllUsers() {
    }

    @Test
    public void addUser() {
        User user = new User();
        user.setUsername("testUser");
        user.setEmail("test@mail.ru");
        when(userRepository.findByUsername(user.getUsername())).thenReturn(null);
        when(userRepository.save(user)).thenReturn(user);
        User newUser =userService.addUser(user);
        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(userRepository, times(1)).save(user);
        verify(mailSender, times(1)).send(ArgumentMatchers.eq(user.getEmail()),
                                                                  ArgumentMatchers.eq("Activation Code"),
                                                                    ArgumentMatchers.anyString());
        assertFalse(newUser.isActive());
        assertNotNull(newUser.getActivationCode());
        assertEquals("testUser", newUser.getUsername());
    }
}