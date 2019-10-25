package com.atitto.data.socketudp;

import com.atitto.domain.socket.SocketClientInfo;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class UpdSocketClientThread implements Runnable {

    private CompositeDisposable disposer = new CompositeDisposable();
    private SocketClientInfo socketClientInfo;
    private String myIP;
    private CountDownLatch countDownLatch;

    private AtomicBoolean shouldExecute = new AtomicBoolean(true);

    UpdSocketClientThread(SocketClientInfo socketClientInfo, String myIp, CountDownLatch countDownLatch) {
        this.socketClientInfo = socketClientInfo;
        this.myIP = myIp;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        try {
            countDownLatch.await();

            DatagramSocket recSocket = new DatagramSocket(socketClientInfo.getPort(), InetAddress.getByName("0.0.0.0"));
            byte[] lMsg = new byte[1024];
            recSocket.setBroadcast(true);

            disposer.add(socketClientInfo.getOnNeedToCloseSocket()
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(Schedulers.io())
                    .subscribe(v -> {
                        shouldExecute.set(false);
                        recSocket.close();
                        disposer.dispose();
                    }));

            socketClientInfo.getOnSocketReplied().onNext(Integer.toString(socketClientInfo.getPort()));

            while (shouldExecute.get()) {

                try {
                    DatagramPacket dp = new DatagramPacket(lMsg, lMsg.length);
                    recSocket.receive(dp);
                    if(!dp.getAddress().getHostAddress().equals(myIP))
                    socketClientInfo.getOnMessageReceived().onNext(new String(lMsg, 0, dp.getLength()));
                } catch (Exception ex) {
                    socketClientInfo.getOnSocketError().onNext(ex.getMessage());
                }
            }
        } catch (Exception e) {
            socketClientInfo.getOnSocketError().onNext(e.getMessage());
            disposer.dispose();
        }
    }

}
