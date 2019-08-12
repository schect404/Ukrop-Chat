package com.atitto.data;

import com.atitto.data.entities.RealmChat;
import com.atitto.data.entities.RealmMessage;
import com.atitto.data.socket.SocketClientImpl;
import com.atitto.data.utils.ConverterUtil;
import com.atitto.domain.ChatRepository;
import com.atitto.domain.entities.ChatListItem;
import com.atitto.domain.entities.Message;
import com.atitto.domain.socket.SocketClient;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

class ChatRepositoryImpl implements ChatRepository {

    private Realm database;

    @Inject
    ChatRepositoryImpl(Realm database) {
        this.database = database;
    }

    @Override
    public List<ChatListItem> getChats() {
        RealmResults res = database.where(RealmChat.class).findAll();
        ArrayList<RealmChat> chats = new ArrayList<RealmChat>(res);
        return ConverterUtil.mapRealmChats(chats);
    }

    @Override
    public void storeChat(ChatListItem chat) {
        database.beginTransaction();
        RealmChat chatToInclude = ConverterUtil.mapChat(chat);
        database.copyToRealmOrUpdate(chatToInclude);
        database.commitTransaction();
    }
}
