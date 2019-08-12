package com.atitto.domain.socket;

import android.util.Pair;

import java.net.Socket;

import io.reactivex.Observable;

public interface SocketUseCase {

    Observable<String> getOnError();
    Observable<Pair<String, Socket>> getOnConnected();
    Observable<String> getOnSocketDisconnected();
    Observable<String> getOnSocketReplied();
    Observable<String> getOnMessageReceived();
    Observable<String> getOnSocketError();

    String getIpAddress();
    Socket getStoredSocket(String id);
    void startSocketServer();
    void storeConnectedSocket(Pair<String, Socket> socket);
    void removeSocket(String id);
    void connectSocket(String ip, String port);
    void connectUdpSocket(String port);
    void sendNewMessage(String text);
    void closeSocket(String ip);
}
