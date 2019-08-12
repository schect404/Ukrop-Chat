package com.atitto.domain.chat;

import com.atitto.domain.ChatRepository;
import com.atitto.domain.entities.ChatListItem;

import java.util.List;

import javax.inject.Inject;

public class ChatUseCaseImpl implements ChatUseCase {

    private ChatRepository chatRepository;

    @Inject
    public ChatUseCaseImpl(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    @Override
    public List<ChatListItem> getChats() {
        return chatRepository.getChats();
    }

    @Override
    public void storeChat(ChatListItem chat) {
        chatRepository.storeChat(chat);
    }
}
