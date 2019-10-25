package com.atitto.data.socketudp;

import android.content.Context;

import com.atitto.domain.socket.SocketClientInfo;
import com.atitto.domain.updsocket.UpdSocket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class UdpSocketImpl implements UpdSocket {

    private Context context;

    private PublishSubject<String> onSendMessage = PublishSubject.create();

    private ExecutorService executorService = Executors.newCachedThreadPool();

    public UdpSocketImpl(Context context) {
        this.context = context;
    }

    @Override
    public void sendMessage(String message) {
        onSendMessage.onNext(message);
    }

    @Override
    public void connectSocket(SocketClientInfo socketClientInfo, String myIp) {
        socketClientInfo.setOnSendMessage(onSendMessage);
        CountDownLatch latch = new CountDownLatch(1);
        UpdSocketThread udpSocketThread = new UpdSocketThread(socketClientInfo, latch, context);
        UpdSocketClientThread udpSocketClientThread = new UpdSocketClientThread(socketClientInfo, myIp, latch);
        executorService.submit(udpSocketThread);
        executorService.submit(udpSocketClientThread);
        latch.countDown();
    }
}
