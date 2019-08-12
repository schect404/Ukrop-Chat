package com.atitto.ukropchat.app.chat;

import com.atitto.domain.chat.ChatUseCase;
import com.atitto.domain.socket.SocketUseCase;

import dagger.Module;
import dagger.Provides;

@Module
public class ChatModule {

    @Provides
    ChatViewModel viewModel(SocketUseCase socketUseCase, ChatUseCase chatUseCase) {
        return new ChatViewModelImpl(socketUseCase, chatUseCase);
    }

}
