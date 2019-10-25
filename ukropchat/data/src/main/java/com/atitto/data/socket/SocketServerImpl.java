package com.atitto.data.socket;

import com.atitto.domain.socket.SocketServer;
import com.atitto.domain.socket.SocketServerCallbacks;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketServerImpl implements SocketServer {

    private ExecutorService executor = Executors.newCachedThreadPool();

    @Override
    public void startServerSocket(SocketServerCallbacks socketServerCallbacks) {
        SocketServerThread socketServerThread = new SocketServerThread(socketServerCallbacks);
        executor.submit(socketServerThread);
    }

}