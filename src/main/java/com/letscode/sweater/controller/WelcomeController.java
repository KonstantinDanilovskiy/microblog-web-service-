package com.letscode.sweater.controller;

import com.letscode.sweater.dto.MessagesDTO;
import com.letscode.sweater.entity.Message;
import com.letscode.sweater.entity.User;
import com.letscode.sweater.service.MessageService;
import com.letscode.sweater.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class WelcomeController {
    private static final String MAIN_URL = "/main";
    private static final String MESSAGE_URL = "/message/%s";
    @Autowired
    MessageService messageService;
    @Autowired
    UserService userService;

    @Value("${upload.path}")
    private String uploadPath;

    @GetMapping("/")
    public String greeting(Model model) {
        return "home";
    }

    @GetMapping("/hello")
    public String greeting() {
        return "hello";
    }

    @GetMapping("/main")
    public String main(@AuthenticationPrincipal User loggedInUser, Model model,
                       @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
        model.addAttribute("messages", messageService.getAllSubscriptionMessages(loggedInUser.getUsername(), pageable));
        model.addAttribute("url", MAIN_URL);
        return "main";
    }

    @GetMapping("/message/{userName}")
    public String main(@AuthenticationPrincipal User loggedInUser, @PathVariable String userName, Model model,
                       @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC, value = 9) Pageable pageable) {
        if (loggedInUser.equals(userService.getByUsername(userName))) {
//            model.addAttribute("messages", userService.getAllMessagesByUser(userName));
            model.addAttribute("messages", userService.getAllMessagesByUser(userName, pageable));
            model.addAttribute("url", String.format(MESSAGE_URL, userName));
        }
        return "main";
    }

    @GetMapping("/find")
    public String main(@RequestParam String tag,
                       @RequestParam(name = "showAll", required = false) boolean isShowAll,
                       Model model) {
        if ((tag != null || tag.isEmpty()) && !isShowAll) {
            model.addAttribute("messages", messageService.getByTag(tag));
            return "main";
        } else {
            return "redirect:/main";
        }
    }

    @PostMapping("/save")
    public String addMessage(@AuthenticationPrincipal User loggedInUser, @ModelAttribute MessagesDTO messagesDTO, Model model) {
        List<Message> messages = messagesDTO.getMessages().stream()
                                                          .filter(msg -> !msg.getText().isEmpty())
                                                          .map(msg -> {msg.setAuthor(loggedInUser);
                                                                     return msg;
                                                          })
                                                          .collect(Collectors.toList());

        messageService.saveAll(messages);
        return "redirect:/main";
    }

    @GetMapping("/create")
    public String create(@RequestParam(name="count", required=false, defaultValue="1") int count, Model model) {
        MessagesDTO messagesDTO = new MessagesDTO();
        for (int i = 1; i <= count; i++) {
            messagesDTO.addMessage(new Message());
        }
        model.addAttribute("formData", messagesDTO);
        return "createMessageForm";
    }

    @GetMapping("/create-one")
    public String createOne(Model model) {
        model.addAttribute("newMessage", new Message());
        return "createOneMessageForm";
    }

    @PostMapping("/save-one")
    public String addMessage(@AuthenticationPrincipal User loggedInUser,
                             @Valid @ModelAttribute Message message,
                             BindingResult bindingResult,
                             @RequestParam("file") MultipartFile file,
                             Model model) throws IOException {
        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = ControllerUtils.getErrors(bindingResult);
            model.addAllAttributes(errorMap);
            model.addAttribute("newMessage", new Message());
        } else {
            if (!message.getText().isEmpty()) {
                if (file != null && file.getOriginalFilename() != null && !file.getOriginalFilename().isEmpty()) {
                    File uploadDir = new File(uploadPath);
                    if (!uploadDir.exists()) {
                        uploadDir.mkdir();
                    }

                    String uuidFile = UUID.randomUUID().toString();
                    String resultFileName = uuidFile + "." + file.getOriginalFilename();
                    file.transferTo(new File(uploadPath + resultFileName));
                    message.setFilename(resultFileName);
                }
                message.setAuthor(loggedInUser);
                messageService.save(message);
                return "redirect:/main";
            }
        }
        return "createOneMessageForm";
    }

    @GetMapping("/message/edit/{messageId}")
    public String getMessage(@PathVariable long messageId, Model model) {
        Message messageFromDB = messageService.editMessage(messageId);
        if (messageFromDB != null) {
            model.addAttribute("newMessage", messageFromDB);
            return "createOneMessageForm";
        }
        return "messageNotFound";
    }

    @ResponseBody
    @DeleteMapping("/message/edit/{messageId}")
    public ResponseEntity<String> deleteMessage(@PathVariable long messageId, Model model) {
        Message messageFromDB = messageService.getById(messageId);
        if (messageFromDB != null) {
            if (messageFromDB.getFilename() == null) {
                messageService.deleteById(messageId);
            } else {
                messageService.deleteMessageWithFile(messageFromDB);
            }
        }
        model.addAttribute("messages", messageService.getAll());
        return new ResponseEntity<>("Message is successfully deleted", HttpStatus.OK);
    }
}
