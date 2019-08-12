package com.atitto.data.utils;

import com.atitto.data.entities.RealmChat;
import com.atitto.data.entities.RealmMessage;
import com.atitto.domain.entities.ChatListItem;
import com.atitto.domain.entities.Message;
import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;

public class ConverterUtil {

    public static Message mapRealmMessage(RealmMessage message) {
        return new Message(message.getText(), message.getMy());
    }

    public static List<Message> mapRealmMessages(RealmList<RealmMessage> messages) {
        ArrayList<Message> newMessages = new ArrayList<>();
        messages.forEach( message -> newMessages.add(mapRealmMessage(message)));
        return  newMessages;
    }

    public static ChatListItem mapRealmChat(RealmChat chat) {
        return new ChatListItem(chat.getId(), mapRealmMessages(chat.getMessages()));
    }

    public static List<ChatListItem> mapRealmChats(ArrayList<RealmChat>  chats) {
        ArrayList<ChatListItem> newChats = new ArrayList<>();
        chats.forEach(chat -> newChats.add(mapRealmChat(chat)));
        return newChats;
    }

    public static RealmMessage mapMessage(Message message) {
        return new RealmMessage(message.getText(), message.getMy());
    }

    public static RealmList<RealmMessage> mapMessages(List<Message> messages) {
        RealmList<RealmMessage> newMessages = new RealmList<>();
        messages.forEach( message -> newMessages.add(mapMessage(message)));
        return  newMessages;
    }

    public static RealmChat mapChat(ChatListItem chat) {
        return new RealmChat(chat.getTitle(), mapMessages(chat.getMessages()));
    }

    public static RealmList<RealmChat> mapChats(List<ChatListItem>  chats) {
        RealmList<RealmChat> newChats = new RealmList<>();
        chats.forEach(chat -> newChats.add(mapChat(chat)));
        return newChats;
    }

}
