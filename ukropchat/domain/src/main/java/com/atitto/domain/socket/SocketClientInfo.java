package com.atitto.domain.socket;

import java.net.Socket;

import io.reactivex.subjects.PublishSubject;

public class SocketClientInfo {

    private String ip;
    private int port;
    private Socket socket;

    private PublishSubject<String> onSocketReplied;
    private PublishSubject<String> onMessageReceived;
    private PublishSubject<String> onSocketError;
    private PublishSubject<String> onSendMessage;
    private PublishSubject<String> onNeedToCloseSocket;

    public PublishSubject<String> getOnNeedToCloseSocket() {
        return onNeedToCloseSocket;
    }

    public PublishSubject<String> getOnSendMessage() {
        return onSendMessage;
    }

    public void setOnSendMessage(PublishSubject<String> onSendMessage) {
        this.onSendMessage = onSendMessage;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public PublishSubject<String> getOnSocketReplied() {
        return onSocketReplied;
    }

    public void setOnSocketReplied(PublishSubject<String> onSocketReplied) {
        this.onSocketReplied = onSocketReplied;
    }

    public PublishSubject<String> getOnMessageReceived() {
        return onMessageReceived;
    }

    public void setOnMessageReceived(PublishSubject<String> onMessageReceived) {
        this.onMessageReceived = onMessageReceived;
    }

    public PublishSubject<String> getOnSocketError() {
        return onSocketError;
    }

    public void setOnSocketError(PublishSubject<String> onSocketError) {
        this.onSocketError = onSocketError;
    }

    public SocketClientInfo(String ip,
                            int port,
                            Socket socket,
                            PublishSubject<String> onSocketReplied,
                            PublishSubject<String> onMessageReceived,
                            PublishSubject<String> onSocketError,
                            PublishSubject<String> onNeedToCloseSocket) {

        this.ip = ip;
        this.port = port;
        this.socket = socket;
        this.onSocketReplied = onSocketReplied;
        this.onMessageReceived = onMessageReceived;
        this.onSocketError = onSocketError;
        this.onNeedToCloseSocket = onNeedToCloseSocket;
    }

}
