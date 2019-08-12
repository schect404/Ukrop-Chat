package com.atitto.data.socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class SocketClientThread implements Runnable {

    private static final String HANDSHAKE_MESSAGE = "Handshake";

    private String ip;
    private String port;
    private BufferedReader in;
    private PrintWriter out;
    private Observable<String> onSendMessage;
    private Observable<String> onNeedToCloseSocket;
    private Socket socket;
    private CompositeDisposable disposer = new CompositeDisposable();

    SocketClientThread(String ip, String port, Observable<String> onSendMessage,
                       Observable<String> onNeedToCloseSocket,
                       Socket socket, PublishSubject<String> onSocketReplied,
                       PublishSubject<String> onSocketError,
                       PublishSubject<String> onMessageReceived) {
        this.ip = ip;
        this.port = port;
        this.onSendMessage = onSendMessage;
        this.socket = socket;
        this.onSocketReplied = onSocketReplied;
        this.onSocketError = onSocketError;
        this.onMessageReceived = onMessageReceived;
        this.onNeedToCloseSocket = onNeedToCloseSocket;
    }

    Observable<String> getOnSocketReplied() {
        return onSocketReplied;
    }

    Observable<String> getOnSocketCreated() {
        return onSocketCreated;
    }

    Observable<String> getOnMessageReceived() {
        return onMessageReceived;
    }

    Observable<String> getOnSocketError() {
        return onSocketError;
    }

    private PublishSubject<String> onSocketReplied;
    private PublishSubject<String> onSocketCreated = PublishSubject.create();
    private PublishSubject<String> onMessageReceived;
    private PublishSubject<String> onSocketError;

    @Override
    public void run() {
        try {
            InetAddress serverAddress = InetAddress.getByName(ip);

            if (socket == null) socket = new Socket(serverAddress, Integer.parseInt(port));
            else {
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                if (out != null && !out.checkError()) {
                    out.println(HANDSHAKE_MESSAGE);
                    out.flush();
                    onSocketReplied.onNext("replied " + ip + ":" + port);
                }
            }

            try {
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                socket.setTcpNoDelay(true);
                disposer.add(onSendMessage.subscribeOn(AndroidSchedulers.mainThread())
                        .observeOn(Schedulers.io())
                        .subscribe(s -> {
                            if (out != null && !out.checkError()) {
                                out.print(s + '\n');
                                out.flush();
                            }
                        }));

                disposer.add(onNeedToCloseSocket.subscribeOn(AndroidSchedulers.mainThread())
                        .observeOn(Schedulers.io())
                        .subscribe(v -> socket.close()));

                String incomingMessage;
                while ((incomingMessage = in.readLine()) != null) {
                    if (incomingMessage.equals(HANDSHAKE_MESSAGE))
                        onSocketReplied.onNext("replied " + ip + ":" + port);
                    else onMessageReceived.onNext(incomingMessage);

                }

            } catch (Exception e) {
                onSocketError.onNext(e.getLocalizedMessage());
            } finally {
                out.flush();
                out.close();
                in.close();
                socket.close();
                disposer.dispose();
            }

        } catch (Exception e) {
            onSocketError.onNext(e.toString());
        }

    }
}
