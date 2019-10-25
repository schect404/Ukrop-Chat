package com.atitto.data.socket;

import com.atitto.domain.socket.SocketClient;
import com.atitto.domain.socket.SocketClientInfo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class SocketClientImpl implements SocketClient {

    private PublishSubject<String> onSendMessage = PublishSubject.create();

    private ExecutorService executor = Executors.newCachedThreadPool();

    @Override
    public void sendMessage(String message) {
        onSendMessage.onNext(message);
    }

    @Override
    public void connectSocket(SocketClientInfo socketClientInfo) {
        socketClientInfo.setOnSendMessage(onSendMessage);
        SocketClientThread socketClientThread = new SocketClientThread(socketClientInfo);
        executor.submit(socketClientThread);
    }

}