package com.letscode.sweater.service;

import com.letscode.sweater.entity.Message;
import com.letscode.sweater.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Set;

public interface UserService extends UserDetailsService {
    User getByUsername(String username);

    User getByUserId(long userId);

    List<User> getAllUsers();

    void save(User user);

    User addUser(User user);

    boolean activateUserByCode(String code);

    Set<Message> getAllMessagesByUser(String userName);

    Page<Message> getAllMessagesByUser(String userName, Pageable pageable);

    void subscribe(User loggedInUser, String userName);

    void unsubscribe(User loggedInUser, String userName);
}
