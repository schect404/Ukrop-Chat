package com.atitto.data;

import com.atitto.data.socket.SocketServerImpl;
import com.atitto.data.socket.SocketServerThread;
import com.atitto.domain.ChatRepository;
import com.atitto.domain.SocketRepository;
import com.atitto.domain.socket.SocketServer;
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
    SocketRepository provideSocketRepository(SocketServer socketServer){
        return new SocketRepositoryImpl(socketServer);
    }

    @Provides
    @Singleton
    SocketServer provideSocketServer(SocketServerThread socketServerThread) {
        return new SocketServerImpl(socketServerThread);
    }

    @Provides
    @Singleton
    SocketServerThread provideServer() {
        return new SocketServerThread();
    }

}
