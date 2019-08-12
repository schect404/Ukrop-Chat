package com.atitto.data.socketudp;

import com.atitto.domain.updsocket.UpdSocket;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class UdpSocketImpl implements UpdSocket {

    private UpdSocketThread udpSocketThread;
    private UpdSocketClientThread updSocketClientThread;

    private PublishSubject<String> onSendMessage = PublishSubject.create();
    private PublishSubject<String> onNeedToCloseSocket = PublishSubject.create();

    public UdpSocketImpl(int port,
                         String myIp,
                         PublishSubject<String> onSocketError,
                         PublishSubject<String> onMessageReceive,
                         PublishSubject<String> onMessageReplied) {
        udpSocketThread = new UpdSocketThread(port, onSendMessage, onSocketError, onNeedToCloseSocket,onMessageReplied);
        updSocketClientThread = new UpdSocketClientThread(port, myIp, onNeedToCloseSocket, onSocketError, onMessageReceive);
    }

    @Override
    public Observable<String> getOnMessageReceived() {
        return updSocketClientThread.getOnMessageReceived();
    }

    @Override
    public Observable<String> getOnSocketError() {
        return udpSocketThread.getOnSocketError();
    }

    @Override
    public void sendMessage(String message) {
        onSendMessage.onNext(message);
    }

    @Override
    public void connectSocket() {
        new Thread(udpSocketThread).start();
        new Thread(updSocketClientThread).start();
    }

    @Override
    public void closeSocket() {
        onNeedToCloseSocket.onNext("");
    }
}
