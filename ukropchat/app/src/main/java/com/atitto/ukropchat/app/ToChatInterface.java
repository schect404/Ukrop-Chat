package com.atitto.ukropchat.app;

import com.atitto.domain.entities.ChatListItem;

public interface ToChatInterface {
    void toChat(String id, ChatListItem chat);
}
