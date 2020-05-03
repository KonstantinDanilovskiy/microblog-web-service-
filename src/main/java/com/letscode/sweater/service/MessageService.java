package com.letscode.sweater.service;

import com.letscode.sweater.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface MessageService {
    List<Message> getAll();

    List<Message> getByTag(String tag);

    Page<Message> getAllSubscriptionMessages(String userName, Pageable pageable);

    Message getById(long id);

    void save(Message message);

    void saveAll(List<Message> messages);

    Message editMessage(long messageId);

    void deleteById(long messageId);

    void deleteMessageWithFile(Message message);

    Page<Message> getMessagesByUserName(String username, Pageable pageable);
}
