package com.atitto.domain;

import com.atitto.domain.chat.ChatUseCase;
import com.atitto.domain.chat.ChatUseCaseImpl;
import com.atitto.domain.socket.SocketServer;
import com.atitto.domain.socket.SocketUseCase;
import com.atitto.domain.socket.SocketUseCaseImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DomainModule {

    @Provides
    @Singleton
    SocketUseCase provideSocketUseCase(SocketRepository repository, SocketServer socketServer) {
        return new SocketUseCaseImpl(repository, socketServer);
    }

    @Provides
    @Singleton
    ChatUseCase provideChatUseCase(ChatRepository repository) {
        return new ChatUseCaseImpl(repository);
    }

}
