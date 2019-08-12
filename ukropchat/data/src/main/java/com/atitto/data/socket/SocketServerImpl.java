package com.atitto.data.socket;

import android.util.Pair;
import com.atitto.domain.socket.SocketServer;
import java.net.Socket;
import javax.inject.Inject;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class SocketServerImpl implements SocketServer {

    private SocketServerThread socketServerThread;

    @Inject
    public SocketServerImpl(SocketServerThread socketServerThread) {
        this.socketServerThread = socketServerThread;
    }

    @Override
    public Observable<String> getErrorEvent() {
        return socketServerThread.getOnError();
    }

    @Override
    public Observable<Pair<String, Socket>> getConnectedEvent() {
        return socketServerThread.getOnConnect();
    }

    @Override
    public Observable<String> getOnSocketDisconnectedEvent() {
        return socketServerThread.getOnSocketDisconnected();
    }

    @Override
    public Observable<String> getOnMessageReceived() {
        return socketServerThread.getOnMessageReceived();
    }

    @Override
    public void startServerSocket() {
        if(!socketServerThread.isAlive()) socketServerThread.start();
    }

    @Override
    public void subscribeWith(PublishSubject<String> onMessageReceived) {
        socketServerThread.subscribeWith(onMessageReceived);
    }

}