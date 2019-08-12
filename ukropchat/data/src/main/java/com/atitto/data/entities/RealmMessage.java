package com.atitto.data.entities;

import io.realm.RealmObject;

public class RealmMessage extends RealmObject {

    private String text;
    private Boolean isMy;

    public RealmMessage() {
        this.text = "";
        this.isMy = false;
    }

    public Boolean getMy() {
        return isMy;
    }

    public void setMy(Boolean my) {
        isMy = my;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public RealmMessage(String text, Boolean isMy) {
        this.text = text;
        this.isMy = isMy;
    }
}
