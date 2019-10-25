package com.atitto.domain.socket;

public interface SocketClient {
    void sendMessage(String message);
    void connectSocket(SocketClientInfo socketClientInfo);
}
