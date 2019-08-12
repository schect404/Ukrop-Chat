package com.atitto.data.socket;

import android.util.Log;
import android.util.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class SocketServerThread extends Thread {

    private ServerSocket serverSocket;
    private Socket socket;
    private final static int SocketServerPort = 8080;
    private String incomingMessage;

    public Observable<String> getOnError() {
        return onError;
    }

    public Observable<Pair<String, Socket>> getOnConnect() {
        return onConnect;
    }

    public Observable<String> getSetupEvent() {
        return setupEvent;
    }

    public Socket getSocket() {
        return socket;
    }

    public Observable<String> getOnSocketDisconnected() {
        return onSocketDisconnected;
    }

    public Observable<String> getOnMessageReceived() {
        return onMessageReceived;
    }

    private PublishSubject<String> onError = PublishSubject.create();
    private PublishSubject<Pair<String, Socket>> onConnect = PublishSubject.create();
    private PublishSubject<String> setupEvent = PublishSubject.create();
    private PublishSubject<String> onSocketDisconnected = PublishSubject.create();
    private PublishSubject<String> onMessageReceived;

    void subscribeWith(PublishSubject<String> onMessageReceived) {
        this.onMessageReceived = onMessageReceived;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(SocketServerPort);
            setupEvent.onNext("Server working");
            while (true) {
                socket = serverSocket.accept();
                String ip = socket.getInetAddress().toString() + ":" + socket.getLocalPort();
                onConnect.onNext(new Pair<>(ip, socket));
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                while ((incomingMessage = in.readLine()) != null) {
                    onMessageReceived.onNext(incomingMessage);
                }
            }
        } catch (IOException e) {
            onError.onNext(e.getMessage());
        }
    }

}
