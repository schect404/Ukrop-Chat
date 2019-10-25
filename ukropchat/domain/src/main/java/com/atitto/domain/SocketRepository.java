package com.atitto.domain;

import android.util.Pair;

import java.net.Socket;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public interface SocketRepository {

    Observable<String> getOnSocketReplied();
    Observable<String> getOnMessageReceived();
    Observable<String> getOnSocketError();
    Observable<String> getOnSocketDisconnected();
    Observable<Pair<String, Socket>> getOnConnect();

    String getIpAddress(boolean useIPv4);
    Socket findSocket(String ip);

    void startSocketServer();
    void storeSocket(Pair<String, Socket> socket);
    void sendMessage(String message);
    void removeSocket(String ip);
    void connectSocket(String ip, String port, Socket defaultSocket);
    void connectByUdp(String port);
    void closeSocket(String ip);
    void closeSocket();
}
