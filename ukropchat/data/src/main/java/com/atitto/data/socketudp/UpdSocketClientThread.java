package com.atitto.data.socketudp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class UpdSocketClientThread implements Runnable {

    private int port;
    private String myIP;
    private PublishSubject<String> onMessageReceived;
    private PublishSubject<String> onSocketError;
    private Observable<String> onNeedToCloseSocket;
    private CompositeDisposable disposer = new CompositeDisposable();

    UpdSocketClientThread(int port, String myIP, Observable<String> onNeedToCloseSocket,
                          PublishSubject<String> onSocketError,
                          PublishSubject<String> onMessageReceived) {
        this.port = port;
        this.myIP = myIP;
        this.onSocketError = onSocketError;
        this.onMessageReceived = onMessageReceived;
        this.onNeedToCloseSocket = onNeedToCloseSocket;
    }

    Observable<String> getOnMessageReceived() {
        return onMessageReceived;
    }

    @Override
    public void run() {
        try {
            InetAddress hostAddress = InetAddress.getLocalHost();
            DatagramSocket recSocket = new DatagramSocket(null);
            byte[] lMsg = new byte[1024];
            recSocket.setBroadcast(true);
            recSocket.setReuseAddress(true);
            recSocket.setSoTimeout(2000);
            recSocket.bind(new InetSocketAddress(port));

            disposer.add(onNeedToCloseSocket.subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(Schedulers.io())
                    .subscribe(v -> recSocket.close()));

            while (true) {
                DatagramPacket dp = new DatagramPacket(lMsg, lMsg.length, InetAddress.getByName("255.255.255.255"), port);
                try {
                    recSocket.receive(dp);
                    if(!dp.getAddress().getHostAddress().equals(myIP))
                    onMessageReceived.onNext(new String(lMsg, 0, dp.getLength()));

                } catch (Exception ex) {
                }
            }
        } catch (Exception e) {
            onSocketError.onNext(e.getMessage());
        }
    }

}
