package com.atitto.domain.socket;

import android.util.Pair;

import com.atitto.domain.SocketRepository;

import java.net.Socket;

import javax.inject.Inject;
import io.reactivex.Observable;

public class SocketUseCaseImpl implements SocketUseCase {

    private SocketRepository socketRepository;
    private SocketServer socketServer;

    @Inject
    public SocketUseCaseImpl(SocketRepository repository, SocketServer socketServer){
        this.socketRepository = repository;
        this.socketServer = socketServer;
    }

    @Override
    public Observable<String> getOnError() {
        return socketServer.getErrorEvent();
    }

    @Override
    public Observable<Pair<String, Socket>> getOnConnected() {
        return socketServer.getConnectedEvent();
    }

    @Override
    public Observable<String> getOnSocketDisconnected() {
        return socketServer.getOnSocketDisconnectedEvent();
    }

    @Override
    public Observable<String> getOnSocketReplied() {
        return socketRepository.getOnSocketReplied();
    }

    @Override
    public Observable<String> getOnMessageReceived() {
        return socketRepository.getOnMessageReceived();
    }

    @Override
    public Observable<String> getOnSocketError() {
        return socketRepository.getOnSocketError();
    }

    @Override
    public String getIpAddress() {
        return socketRepository.getIpAddress(true);
    }

    @Override
    public void startSocketServer() {
        socketServer.startServerSocket();
    }

    @Override
    public void storeConnectedSocket(Pair<String, Socket> socket) {
        socketRepository.storeSocket(socket);
    }

    @Override
    public void removeSocket(String ip) {
        socketRepository.removeSocket(ip);
    }

    @Override
    public void connectSocket(String ip, String port) {
        socketRepository.connectSocket(ip, port, getStoredSocket("/" + ip + ":" + port));
    }

    @Override
    public void connectUdpSocket(String port) {
        socketRepository.connectByUdp(port);
    }

    @Override
    public Socket getStoredSocket(String ip) {
        return socketRepository.findSocket(ip);
    }

    @Override
    public void sendNewMessage(String text) {
        socketRepository.sendMessage(text);
    }

    @Override
    public void closeSocket(String ip) {
        socketRepository.closeSocket(ip);
    }
}
