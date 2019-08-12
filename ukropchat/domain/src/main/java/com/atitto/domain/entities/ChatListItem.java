package com.atitto.domain.entities;

import java.util.List;

public class ChatListItem {

    private String title;
    private List<Message> messages;

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public ChatListItem(String title, List<Message> messages) {
        this.messages = messages;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
