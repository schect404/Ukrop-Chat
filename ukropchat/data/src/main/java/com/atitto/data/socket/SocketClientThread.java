package com.atitto.data.socket;

import com.atitto.domain.socket.SocketClientInfo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class SocketClientThread implements Runnable {

    private static final String HANDSHAKE_MESSAGE = "Handshake";

    private BufferedReader in;
    private PrintWriter out;

    private CompositeDisposable disposer = new CompositeDisposable();

    private SocketClientInfo socketClientInfo;

    private AtomicBoolean shouldExecute = new AtomicBoolean(true);

    SocketClientThread(SocketClientInfo socketClientInfo) {
        this.socketClientInfo = socketClientInfo;
    }

    @Override
    public void run() {
        try {
            runSocket();
            subscribeOnSend();
            subscribeLoopOnIncome();
            subscribeOnClose();
        }
        catch (Exception e) {
            socketClientInfo.getOnSocketError().onNext(e.toString());
        }
    }

    private void runSocket() throws IOException {
        InetAddress serverAddress = InetAddress.getByName(socketClientInfo.getIp());
        if (socketClientInfo.getSocket() == null) {
            socketClientInfo.setSocket(new Socket(serverAddress, socketClientInfo.getPort()));
            initStreams();
        }
        else {
            initStreams();
            checkAndSendHandshake();
        }
    }

    private void initStreams() throws IOException {
        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socketClientInfo.getSocket().getOutputStream())), true);
        in = new BufferedReader(new InputStreamReader(socketClientInfo.getSocket().getInputStream()));
    }

    private void checkAndSendHandshake() {
        if (out != null && !out.checkError()) {
            writeToSocket(HANDSHAKE_MESSAGE);
            socketClientInfo.getOnSocketReplied().onNext("replied " + socketClientInfo.getIp() + ":" + socketClientInfo.getPort());
        }
    }

    private void writeToSocket(String message) {
        out.println(message);
        out.flush();
    }

    private void subscribeOnSend() {
        disposer.add(
                socketClientInfo.getOnSendMessage()
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .subscribe(message -> { if (out != null && !out.checkError()) writeToSocket(message); })
        );
    }

    private void subscribeOnClose() {
        disposer.add(socketClientInfo.getOnNeedToCloseSocket()
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .subscribe(v -> {
                    shouldExecute.set(false);
                    closeSockets();
                    disposer.dispose();
                }));
    }

    private void closeSockets() throws IOException {
        out.close();
        in.close();
        socketClientInfo.getSocket().close();
        disposer.dispose();
    }

    private void subscribeLoopOnIncome() throws IOException {
        String incomingMessage;
        while (((incomingMessage = in.readLine()) != null)&&shouldExecute.get()) {
            if (incomingMessage.equals(HANDSHAKE_MESSAGE))
                socketClientInfo.getOnSocketReplied().onNext("replied " + socketClientInfo.getIp() + ":" + socketClientInfo.getPort());
            else socketClientInfo.getOnMessageReceived().onNext(incomingMessage);
        }
    }

}
