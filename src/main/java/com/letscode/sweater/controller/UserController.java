package com.letscode.sweater.controller;

import com.letscode.sweater.entity.User;
import com.letscode.sweater.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/account/{userName}")
    public String getMessage(@PathVariable String userName, Model model) {
        User userFromDB = userService.getByUsername(userName);
        if (userFromDB != null) {
            model.addAttribute("user", userFromDB);
            return "userSettingsForm";
        }
        return "userNotFound";
    }

    @PostMapping("/edit/{userId}")
    public String getMessage(@PathVariable long userId, User user, Model model) {
        User userFromDB = userService.getByUserId(userId);
        if (userFromDB != null && user != null) {
            if (user.getUsername() != null && !user.getUsername().isEmpty() && !userFromDB.getUsername().equals(user.getUsername())) {
                userFromDB.setUsername(user.getUsername());
                userService.save(userFromDB);
                model.addAttribute("infoMessage", "User name successfully changed!");
                return "login";
            }
            return "redirect:/user/account/" + userFromDB.getUsername();
        }
        return "userNotFound";
    }

    @GetMapping("/subscription")
    public String subscription(@AuthenticationPrincipal User loggedInUser, @RequestParam(required = false) String userName, Model model) {
        User user = userService.getByUsername(loggedInUser.getUsername());
        User linkedUser = userService.getByUsername(userName);
        model.addAttribute("user", user);
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("userSubscriptions", user.getSubscriptions());
        model.addAttribute("userSubscribers", user.getSubscribers());
        return "subscription";
    }

    @GetMapping("/subscribe/{userName}")
    public String subscribe(@AuthenticationPrincipal User loggedInUser, @PathVariable String userName, Model model) {
        userService.subscribe(loggedInUser, userName);
        return "redirect:/user/subscription";
    }

    @GetMapping("/unsubscribe/{userName}")
    public String unsubscribe(@AuthenticationPrincipal User loggedInUser, @PathVariable String userName, Model model) {
        userService.unsubscribe(loggedInUser, userName);
        return "redirect:/user/subscription";
    }

}
