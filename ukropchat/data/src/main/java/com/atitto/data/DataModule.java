package com.atitto.data;

import android.content.Context;

import com.atitto.data.socket.SocketClientImpl;
import com.atitto.data.socket.SocketServerImpl;
import com.atitto.data.socket.SocketServerThread;
import com.atitto.data.socketudp.UdpSocketImpl;
import com.atitto.domain.ChatRepository;
import com.atitto.domain.SocketRepository;
import com.atitto.domain.socket.SocketClient;
import com.atitto.domain.socket.SocketServer;
import com.atitto.domain.updsocket.UpdSocket;

import javax.inject.Singleton;
import dagger.Module;
import dagger.Provides;
import io.realm.Realm;

@Module
public class DataModule {

    @Provides
    @Singleton
    Realm provideRealmInstance() {
        return Realm.getDefaultInstance();
    }

    @Provides
    @Singleton
    ChatRepository provideChatRepository(Realm database) {
        return new ChatRepositoryImpl(database);
    }

    @Provides
    @Singleton
    SocketRepository provideSocketRepository(SocketServer socketServer, SocketClient socketClient, UpdSocket udpSocket){
        return new SocketRepositoryImpl(socketServer, socketClient, udpSocket);
    }

    @Provides
    @Singleton
    SocketServer provideSocketServer() {
        return new SocketServerImpl();
    }

    @Provides
    @Singleton
    UpdSocket provideUdpSocket(Context context) {
        return new UdpSocketImpl(context);
    }

    @Provides
    @Singleton
    SocketClient provideSocketClient() {
        return new SocketClientImpl();
    }

}
