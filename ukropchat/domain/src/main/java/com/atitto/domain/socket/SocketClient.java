package com.atitto.domain.socket;

import io.reactivex.Observable;

public interface SocketClient {

    Observable<String> getOnSocketReplied();
    Observable<String> getOnSocketCreated();
    Observable<String> getOnMessageReceived();
    Observable<String> getOnSocketError();

    void sendMessage(String message);
    void connectSocket();
    void closeSocket();
}
