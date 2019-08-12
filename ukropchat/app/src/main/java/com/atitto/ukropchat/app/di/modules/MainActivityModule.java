package com.atitto.ukropchat.app.di.modules;

import com.atitto.ukropchat.app.chat.ChatFragment;
import com.atitto.ukropchat.app.mainscreen.MainScreenFragment;

import dagger.Module;
import dagger.Provides;

@Module
public class MainActivityModule {

    @Provides
    MainScreenFragment mainScreen() {
        return new MainScreenFragment();
    }

    @Provides
    ChatFragment chatFragment() {
        return new ChatFragment();
    }

}
