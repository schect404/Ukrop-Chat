package com.atitto.ukropchat.app.mainscreen;

import com.atitto.domain.chat.ChatUseCase;
import com.atitto.domain.socket.SocketUseCase;

import dagger.Module;
import dagger.Provides;

@Module
public class MainScreenModule {

    @Provides
    MainScreenViewModel viewModel(SocketUseCase socketUseCase, ChatUseCase chatUseCase) {
        return new MainScreenViewModelImpl(socketUseCase, chatUseCase);
    }

}
