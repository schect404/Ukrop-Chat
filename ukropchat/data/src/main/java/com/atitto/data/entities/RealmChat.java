package com.atitto.data.entities;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmChat extends RealmObject {

    public RealmChat(String id, RealmList<RealmMessage> messages) {
        this.id = id;
        this.messages = messages;
    }

    public String getId() {
        return id;
    }

    public RealmChat() {
        this.id = "";
        this.messages = new RealmList<>();
    }

    public void setId(String id) {
        this.id = id;
    }

    public RealmList<RealmMessage> getMessages() {
        return messages;
    }

    public void setMessages(RealmList<RealmMessage> messages) {
        this.messages = messages;
    }

    @PrimaryKey
    private String id;
    private RealmList<RealmMessage> messages;
}
