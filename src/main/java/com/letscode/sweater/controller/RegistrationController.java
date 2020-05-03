package com.letscode.sweater.controller;

import com.letscode.sweater.dto.CaptchaDTO;
import com.letscode.sweater.entity.User;
import com.letscode.sweater.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Map;

@Controller
public class RegistrationController {
    private static final String LOGIN = "login";
    private static final String REGISTRATION = "registration";
    private final static String CAPTCHA_URL = "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s";

    @Autowired
    UserService userService;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${security.captcha.secret}")
    String recaptchaSecret;

    @GetMapping("/registration")
    public String registration() {
        return REGISTRATION;
    }

    @PostMapping("/registration")
    public String addUser(@RequestParam("g-recaptcha-response") String captchaResponse,
                          @Valid User user,
                          BindingResult bindingResult,
                          Model model) {
        String url = String.format(CAPTCHA_URL, recaptchaSecret, captchaResponse);
        CaptchaDTO captchaDTO = restTemplate.postForObject(url, Collections.emptyList(), CaptchaDTO.class);
        boolean isCaptchaError = captchaDTO != null && !captchaDTO.isSuccess();
        if (isCaptchaError) {
            model.addAttribute("captchaError", "The checkbox shown above should be selected");
        }
        boolean isPasswordError = user != null && !user.getPassword().equals(user.getRepeatPassword());
        if (isPasswordError) {
            model.addAttribute("passwordError", "Password and confirmation password are not equals");
        }
        if (bindingResult.hasErrors() || isPasswordError || isCaptchaError) {
            if (bindingResult.hasErrors()) {
                Map<String, String> errorMap = ControllerUtils.getErrors(bindingResult);
                model.addAllAttributes(errorMap);
            }
            model.addAttribute(user);
        } else {
            User result = userService.addUser(user);
            if (result == null) {
                model.addAttribute("usernameError", String.format("User %s already exist here!", user.getUsername()));
                return REGISTRATION;
            }
            model.addAttribute("infoMessage", "Please check your email to activate your account");
            return LOGIN;
        }
        return REGISTRATION;
    }

    @GetMapping("/activate/{code}")
    public String emailConfirmation(Model model, @PathVariable String code) {
        boolean isActivated = userService.activateUserByCode(code);
        if (!isActivated) {
            model.addAttribute("infoMessage", String.format("Activation code is not found. Try again!"));
            return REGISTRATION;
        }
        model.addAttribute("infoMessage", String.format("User have been successfully activated!"));
        return LOGIN;

    }
}
