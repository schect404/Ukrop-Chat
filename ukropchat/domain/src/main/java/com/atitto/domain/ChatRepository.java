package com.atitto.domain;

import com.atitto.domain.entities.ChatListItem;

import java.util.List;

public interface ChatRepository {

    List<ChatListItem> getChats();

    void storeChat(ChatListItem chat);

}
