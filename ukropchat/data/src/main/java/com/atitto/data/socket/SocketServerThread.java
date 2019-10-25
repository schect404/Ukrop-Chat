package com.atitto.data.socket;

import android.util.Pair;

import com.atitto.domain.socket.SocketServerCallbacks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class SocketServerThread implements Runnable {

    private final static int SOCKET_SERVER_PORT = 8080;

    private SocketServerCallbacks socketServerCallbacks;

    private AtomicBoolean shouldExecute = new AtomicBoolean(true);

    SocketServerThread(SocketServerCallbacks socketServerCallbacks) {
        this.socketServerCallbacks = socketServerCallbacks;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(SOCKET_SERVER_PORT);
            while (shouldExecute.get()) {
                Socket socket = serverSocket.accept();

                String ip = socket.getInetAddress().toString() + ":" + socket.getLocalPort();
                socketServerCallbacks.getOnConnect().onNext(new Pair<>(ip, socket));

                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String incomingMessage;
                while (((incomingMessage = in.readLine()) != null)&&shouldExecute.get()) {
                    socketServerCallbacks.getOnMessageReceived().onNext(incomingMessage);
                }
            }
        } catch (IOException e) {
            socketServerCallbacks.getOnError().onNext(e.getMessage());
        }
    }

}
