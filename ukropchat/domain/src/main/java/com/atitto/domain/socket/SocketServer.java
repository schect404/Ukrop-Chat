package com.atitto.domain.socket;

import android.util.Pair;

import java.net.Socket;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public interface SocketServer {

    Observable<String> getErrorEvent();
    Observable<Pair<String, Socket>> getConnectedEvent();
    Observable<String> getOnSocketDisconnectedEvent();
    Observable<String> getOnMessageReceived();

    void startServerSocket();
    void subscribeWith(PublishSubject<String> onMessageReceived);

}
