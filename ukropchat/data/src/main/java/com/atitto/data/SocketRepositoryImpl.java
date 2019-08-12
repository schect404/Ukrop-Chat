package com.atitto.data;

import android.util.Pair;

import com.atitto.data.socket.SocketClientImpl;
import com.atitto.data.socketudp.UdpSocketImpl;
import com.atitto.domain.SocketRepository;
import com.atitto.domain.socket.SocketClient;
import com.atitto.domain.socket.SocketServer;
import com.atitto.domain.updsocket.UpdSocket;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class SocketRepositoryImpl implements SocketRepository {

    private PublishSubject<String> onSocketReplied = PublishSubject.create();
    private PublishSubject<String> onMessageReceived = PublishSubject.create();
    private PublishSubject<String> onSocketError = PublishSubject.create();
    private HashMap<String, Socket> connectedSockets = new HashMap<>();
    private SocketClient socketClient;
    private SocketServer socketServer;
    private UpdSocket updSocket;

    @Inject
    SocketRepositoryImpl(SocketServer socketServer) {
        this.socketServer = socketServer;
    }

    @Override
    public Observable<String> getOnSocketReplied() {
        return onSocketReplied;
    }

    @Override
    public Observable<String> getOnMessageReceived() {
        return onMessageReceived;
    }

    @Override
    public Observable<String> getOnSocketError() {
        return onSocketError;
    }

    @Override
    public void sendMessage(String message) {
        try {
            updSocket.sendMessage(message);
        } catch (Exception e) {
            try {
                socketClient.sendMessage(message);
            } catch (Exception ex) {
                onSocketError.onNext(ex.getMessage());
            }
        }
    }

    @Override
    public String getIpAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':') < 0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim < 0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return "";
    }

    @Override
    public void storeSocket(Pair<String, Socket> socket) {
        if (connectedSockets.get(socket.first) != null) connectedSockets.remove(socket.first);
        connectedSockets.put(socket.first, socket.second);
    }

    @Override
    public Socket findSocket(String ip) {
        return connectedSockets.get(ip);
    }

    @Override
    public void removeSocket(String ip) {
        connectedSockets.remove(ip);
    }

    @Override
    public void connectSocket(String ip, String port, Socket defaultSocket) {
        socketServer.subscribeWith(onMessageReceived);
        socketClient = new SocketClientImpl(ip, port, defaultSocket, onSocketReplied, onSocketError, onMessageReceived);
        socketClient.connectSocket();
    }

    @Override
    public void connectByUdp(String port) {
        try {
            updSocket = new UdpSocketImpl(Integer.parseInt(port), getIpAddress(true), onSocketError, onMessageReceived, onSocketReplied);
            updSocket.connectSocket();
        } catch (Exception e) {
            onSocketError.onNext(e.getMessage());
        }
    }

    @Override
    public void closeSocket(String ip) {
        removeSocket(ip);
        if(socketClient != null) socketClient.closeSocket();
        if(updSocket != null) updSocket.closeSocket();
    }

}
