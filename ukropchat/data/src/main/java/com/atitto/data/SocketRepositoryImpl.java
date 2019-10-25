package com.atitto.data;

import android.util.Pair;

import com.atitto.data.socket.SocketClientImpl;
import com.atitto.data.socketudp.UdpSocketImpl;
import com.atitto.domain.SocketRepository;
import com.atitto.domain.socket.SocketClient;
import com.atitto.domain.socket.SocketClientInfo;
import com.atitto.domain.socket.SocketServer;
import com.atitto.domain.socket.SocketServerCallbacks;
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
    private PublishSubject<Pair<String, Socket>> onConnect = PublishSubject.create();
    private PublishSubject<String> onSocketDisconnected = PublishSubject.create();
    private PublishSubject<String> onNeedToCloseSocket = PublishSubject.create();

    private HashMap<String, Socket> connectedSockets = new HashMap<>();

    private SocketServer socketServer;
    private SocketClient socketClient;
    private UpdSocket udpSocket;

    @Inject
    SocketRepositoryImpl(SocketServer socketServer, SocketClient socketClient, UpdSocket udpSocket) {
        this.socketServer = socketServer;
        this.socketClient = socketClient;
        this.udpSocket = udpSocket;
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
    public Observable<Pair<String, Socket>> getOnConnect() {
        return onConnect;
    }

    @Override
    public Observable<String> getOnSocketDisconnected() {
        return onSocketDisconnected;
    }

    @Override
    public void sendMessage(String message) {
        try {
            udpSocket.sendMessage(message);
            socketClient.sendMessage(message);
        } catch (Exception ex) {
            onSocketError.onNext(ex.getMessage());
        }
    }

    @Override
    public void startSocketServer() {
        SocketServerCallbacks socketServerCallbacks = new SocketServerCallbacks(onSocketError, onConnect, onSocketDisconnected, onMessageReceived);
        socketServer.startServerSocket(socketServerCallbacks);
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
        socketClient.connectSocket(new SocketClientInfo(ip, Integer.parseInt(port), defaultSocket, onSocketReplied, onMessageReceived, onSocketError, onNeedToCloseSocket));
    }

    @Override
    public void connectByUdp(String port) {
        try {
            udpSocket.connectSocket(new SocketClientInfo("0.0.0.0", Integer.parseInt(port), null, onSocketReplied, onMessageReceived, onSocketError , onNeedToCloseSocket), getIpAddress(true));
        } catch (Exception e) {
            onSocketError.onNext(e.getMessage());
        }
    }

    @Override
    public void closeSocket(String ip) {
        removeSocket(ip);
        onNeedToCloseSocket.onNext("");
    }

    @Override
    public void closeSocket() {
        onNeedToCloseSocket.onNext("");
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
                        boolean isIPv4 = sAddr.indexOf(':') < 0;
                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%');
                                return delim < 0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) { }
        return "";
    }

}
