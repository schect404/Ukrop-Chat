package com.atitto.data.socketudp;

import android.util.Log;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class UpdSocketThread implements Runnable {

    private int port;
    private Observable<String> onSendMessage;
    private Observable<String> onNeedToCloseSocket;
    private CompositeDisposable disposer = new CompositeDisposable();
    private DatagramSocket socket;

    private PublishSubject<String> onMessageReplied;
    private PublishSubject<String> onSocketError;

    Observable<String> getOnSocketError() {
        return onSocketError;
    }

    Observable<String> getOnMessageReplied() {
        return onMessageReplied;
    }

    UpdSocketThread(int port, Observable<String> onSendMessage,
                    Observable<String> onNeedToCloseSocket,
                    PublishSubject<String> onSocketError,
                    PublishSubject<String> onMessageReplied) {
        this.port = port;
        this.onSendMessage = onSendMessage;
        this.onSocketError = onSocketError;
        this.onMessageReplied = onMessageReplied;
        this.onNeedToCloseSocket = onNeedToCloseSocket;
    }

    @Override
    public void run() {
        try {
            InetAddress hostAddress = InetAddress.getByName("255.255.255.255");
            socket = new DatagramSocket(port+1);
            onMessageReplied.onNext(Integer.toString(port+1));
            socket.setReuseAddress(true);
            socket.setBroadcast(true);

            disposer.add(onSendMessage.subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(Schedulers.io())
                    .subscribe(s -> {
                        byte[] buf = s.getBytes();
                        DatagramPacket request = new DatagramPacket(buf, buf.length, hostAddress, port);
                        socket.send(request);
                    }));

            disposer.add(onNeedToCloseSocket.subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(Schedulers.io())
                    .subscribe(v -> socket.close()));

        } catch (Exception e) {
            onSocketError.onNext(e.getMessage());
        }
    }

}
