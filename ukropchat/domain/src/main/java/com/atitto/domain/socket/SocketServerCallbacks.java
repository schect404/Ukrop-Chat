package com.atitto.domain.socket;

import android.util.Pair;

import java.net.Socket;

import io.reactivex.subjects.PublishSubject;

public class SocketServerCallbacks {

    private PublishSubject<String> onError;
    private PublishSubject<Pair<String, Socket>> onConnect;
    private PublishSubject<String> onSocketDisconnected;
    private PublishSubject<String> onMessageReceived;

    public SocketServerCallbacks(PublishSubject<String> onError, PublishSubject<Pair<String, Socket>> onConnect, PublishSubject<String> onSocketDisconnected, PublishSubject<String> onMessageReceived) {
        this.onError = onError;
        this.onConnect = onConnect;
        this.onSocketDisconnected = onSocketDisconnected;
        this.onMessageReceived = onMessageReceived;
    }

    public PublishSubject<String> getOnMessageReceived() {
        return onMessageReceived;
    }

    public PublishSubject<String> getOnError() {
        return onError;
    }

    public PublishSubject<Pair<String, Socket>> getOnConnect() {
        return onConnect;
    }

    public PublishSubject<String> getOnSocketDisconnected() {
        return onSocketDisconnected;
    }
}
