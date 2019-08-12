package com.atitto.data.socket;

import com.atitto.domain.socket.SocketClient;

import java.net.Socket;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class SocketClientImpl implements SocketClient {

    private SocketClientThread socketClientThread;

    private PublishSubject<String> onSendMessage = PublishSubject.create();
    private PublishSubject<String> onNeedToCloseSocket = PublishSubject.create();

    public SocketClientImpl(String ip, String port, Socket socket,
                            PublishSubject<String> onSocketReplied,
                            PublishSubject<String> onSocketError,
                            PublishSubject<String> onMessageReceived) {
        socketClientThread = new SocketClientThread(ip, port, onSendMessage, onNeedToCloseSocket, socket, onSocketReplied, onSocketError, onMessageReceived);
    }

    @Override
    public Observable<String> getOnSocketReplied() {
        return socketClientThread.getOnSocketReplied();
    }

    @Override
    public Observable<String> getOnSocketCreated() {
        return socketClientThread.getOnSocketCreated();
    }

    @Override
    public Observable<String> getOnMessageReceived() {
        return socketClientThread.getOnMessageReceived();
    }

    @Override
    public Observable<String> getOnSocketError() {
        return socketClientThread.getOnSocketError();
    }

    @Override
    public void sendMessage(String message) {
        onSendMessage.onNext(message);
    }

    @Override
    public void connectSocket() {
        new Thread(socketClientThread).start();
    }

    @Override
    public void closeSocket() {
        onNeedToCloseSocket.onNext("");
    }

}
