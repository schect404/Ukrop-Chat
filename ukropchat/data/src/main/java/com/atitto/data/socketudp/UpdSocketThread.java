package com.atitto.data.socketudp;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.atitto.domain.socket.SocketClientInfo;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class UpdSocketThread implements Runnable {

    private CompositeDisposable disposer = new CompositeDisposable();
    private DatagramSocket socket;
    private Context context;

    private SocketClientInfo socketClientInfo;
    private CountDownLatch countDownLatch;

    private AtomicBoolean shouldExecute = new AtomicBoolean(true);

    UpdSocketThread(SocketClientInfo socketClientInfo, CountDownLatch latch, Context context) {
        this.socketClientInfo = socketClientInfo;
        this.countDownLatch = latch;
        this.context = context;
    }

    @Override
    public void run() {
        try {
            countDownLatch.await();

            InetAddress hostAddress = InetAddress.getByName(socketClientInfo.getIp());
            socket = new DatagramSocket();
            socket.setBroadcast(true);

            disposer.add(socketClientInfo.getOnNeedToCloseSocket()
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(Schedulers.io())
                    .subscribe(v -> {
                        shouldExecute.set(false);
                        socket.close();
                        disposer.dispose();
                    }));

            disposer.add(socketClientInfo.getOnSendMessage()
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(Schedulers.io())
                    .subscribe(s -> {
                        try {
                            byte[] buf = s.getBytes();
                            DatagramPacket request = new DatagramPacket(buf, buf.length, getBroadcastAddress(), socketClientInfo.getPort());
                            socket.send(request);
                        } catch(Exception e) {
                            Log.d(e.getMessage(), e.getMessage());
                        }
                    }));

            socketClientInfo.getOnSocketReplied().onNext(Integer.toString(socketClientInfo.getPort()));

            while(shouldExecute.get()) {
                shouldExecute.set(true);
            }

        } catch (Exception e) {
            socketClientInfo.getOnSocketError().onNext(e.getMessage());
            disposer.dispose();
        }
    }

    InetAddress getBroadcastAddress() throws IOException {
        WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifi.getDhcpInfo();
        // handle null somehow

        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        return InetAddress.getByAddress(quads);
    }

}
