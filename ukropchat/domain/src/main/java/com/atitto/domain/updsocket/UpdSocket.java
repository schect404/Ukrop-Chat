package com.atitto.domain.updsocket;

import io.reactivex.Observable;

public interface UpdSocket {

    Observable<String> getOnMessageReceived();
    Observable<String> getOnSocketError();

    void sendMessage(String message);
    void connectSocket();
    void closeSocket();
}
