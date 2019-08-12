package com.atitto.domain;

import android.util.Pair;

import java.net.Socket;

import io.reactivex.Observable;

public interface SocketRepository {

    Observable<String> getOnSocketReplied();
    Observable<String> getOnMessageReceived();
    Observable<String> getOnSocketError();

    String getIpAddress(boolean useIPv4);
    Socket findSocket(String ip);
    void storeSocket(Pair<String, Socket> socket);
    void sendMessage(String message);
    void removeSocket(String ip);
    void connectSocket(String ip, String port, Socket defaultSocket);
    void connectByUdp(String port);
    void closeSocket(String ip);
}
