package com.atitto.domain.updsocket;

import com.atitto.domain.socket.SocketClientInfo;

public interface UpdSocket {
    void sendMessage(String message);
    void connectSocket(SocketClientInfo socketClientInfo, String myIp);
}
