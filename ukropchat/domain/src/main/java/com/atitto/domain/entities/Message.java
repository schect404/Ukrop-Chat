package com.atitto.domain.entities;

public class Message {
    private String text;
    private Boolean isMy;

    public Message(String text, Boolean isMy) {
        this.text = text;
        this.isMy = isMy;
    }

    public String getText() {
        return text;
    }

    public Boolean getMy() {
        return isMy;
    }
}
