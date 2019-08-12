package com.atitto.domain.chat;

import com.atitto.domain.entities.ChatListItem;

import java.util.List;

public interface ChatUseCase {

    List<ChatListItem> getChats();

    void storeChat(ChatListItem chat);
}
