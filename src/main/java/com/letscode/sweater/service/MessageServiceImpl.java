package com.letscode.sweater.service;

import com.letscode.sweater.entity.Message;
import com.letscode.sweater.entity.User;
import com.letscode.sweater.repository.MessageRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;
    private final UserService userService;

    @Value("${upload.path}")
    private String uploadPath;

    @Autowired
    public MessageServiceImpl(MessageRepository messageRepository, UserService userService) {
        this.messageRepository = messageRepository;
        this.userService = userService;
    }

    @Override
    public List<Message> getAll() {
        return messageRepository.findAll();
    }

    @Override
    public Message getById(long id) {
        return messageRepository.findById(id).orElse(null);
    }

    @Override
    public List<Message> getByTag(String tag) {
        return messageRepository.findByTag(tag);
    }

    @Override
    public Page<Message> getAllSubscriptionMessages(String userName, Pageable pageable) {
        List<Message> result = new ArrayList<>();
        User userFromDb = userService.getByUsername(userName);
        if (userFromDb != null) {
            if (userFromDb.getSubscriptions() != null) {
                userFromDb.getSubscriptions().forEach(sub -> result.addAll(sub.getMessages()));
            }
        }
        return new PageImpl<>(result);
    }

    @Override
    public void save(Message message) {
        messageRepository.save(message);
    }

    @Override
    @Transactional
    public void saveAll(List<Message> messages) {
        messageRepository.saveAll(messages);
    }

    @Override
    public Message editMessage(long messageId) {
        if (messageId < 0) {
            return null;
        }
        Message  messageFromDB = getById(messageId);
        if (messageFromDB != null) {
            if (messageFromDB.getFilename() != null && !messageFromDB.getFilename().isEmpty()) {
                int index = messageFromDB.getFilename().indexOf('.');
                messageFromDB.setFilename(messageFromDB.getFilename().substring(index + 1));
            }
            return messageFromDB;
        }
        return null;
    }

    @Override
    public Page<Message> getMessagesByUserName(String userName, Pageable pageable) {
        return messageRepository.findByAuthor(userName, pageable);
    }

    @Override
    public void deleteById(long messageId) {
        messageRepository.deleteById(messageId);
    }

    @Override
    public void deleteMessageWithFile(Message message) {
        if (message != null && message.getFilename() != null) {
            Path deletedFile = Paths.get(uploadPath + message.getFilename());
            try {
                Files.deleteIfExists(deletedFile);
                log.info("File with name {} have been deleted", message.getFilename());
            } catch (IOException e) {
                log.error("IOException during deletion a file with name: {}", message.getFilename());
            }
            deleteById(message.getId());
        }
    }
}
