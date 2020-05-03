package com.letscode.sweater.dto;

import com.letscode.sweater.entity.Message;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MessagesDTO {
    private List<Message> messages = new ArrayList<>();

    public void addMessage(Message message) {
        getMessages().add(message);
    }
}
