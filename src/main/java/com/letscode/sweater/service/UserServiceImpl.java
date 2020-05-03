package com.letscode.sweater.service;

import com.letscode.sweater.entity.Message;
import com.letscode.sweater.entity.Role;
import com.letscode.sweater.entity.User;
import com.letscode.sweater.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private static final String ACTIVATION_CODE = "Activation Code";
    private static final String EMAIL_MESSAGE = "Hello, %s! \n Welcome to web service. Please visit next link: %s%s";
    private final UserRepository userRepository;
    private final MailSender mailSender;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private MessageService messageService;

    @Value("${email.confirmation.link}")
    private String EMAIL_ACTIVATION_LINK;

    public UserServiceImpl(UserRepository userRepository, MailSender mailSender) {
        this.userRepository = userRepository;
        this.mailSender = mailSender;
    }

    @Override
    public User getByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User getByUserId(long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return userRepository.findByUsername(s);
    }

    @Override
    public User addUser(User user) {
        User userFromDB = getByUsername(user.getUsername());
        if (userFromDB != null) {
            return null;
        }
        user.setActive(false);
        user.setRoles(Collections.singleton(Role.USER));
        user.setActivationCode(UUID.randomUUID().toString());
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (!StringUtils.isEmpty(user.getEmail())) {
            String message = String.format(EMAIL_MESSAGE, user.getUsername(), EMAIL_ACTIVATION_LINK, user.getActivationCode());
            mailSender.send(user.getEmail(), ACTIVATION_CODE, message);
        }
        return userRepository.save(user);
    }

    @Override
    public boolean activateUserByCode(String code) {
        User userFromDB = userRepository.findByActivationCode(code);
        if (userFromDB == null) {
            return false;
        }
        userFromDB.setActivationCode(null);
        userFromDB.setActive(true);
        save(userFromDB);
        return true;
    }

    @Transactional
    @Override
    public Set<Message> getAllMessagesByUser(String userName) {
        User user = getByUsername(userName);
        return user != null ? user.getMessages() : Collections.emptySet();
    }

    @Override
    public Page<Message> getAllMessagesByUser(String userName, Pageable pageable) {
        User user = getByUsername(userName);
        return user != null ? messageService.getMessagesByUserName(userName, pageable) : Page.empty();
    }

    @Override
    public void subscribe(User loggedInUser, String userName) {
        User currentUser = getByUsername(loggedInUser.getUsername());
        User user = getByUsername(userName);
        if (currentUser != null && user != null) {
            user.getSubscribers().add(currentUser);
            save(user);
        }
    }

    @Override
    public void unsubscribe(User loggedInUser, String userName) {
        User currentUser = getByUsername(loggedInUser.getUsername());
        User user = getByUsername(userName);
        if (currentUser != null && user != null) {
            user.getSubscribers().remove(currentUser);
            save(user);
        }
    }
}
