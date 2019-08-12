package com.atitto.ukropchat.app.di.modules;

import com.atitto.ukropchat.app.chat.ChatFragment;
import com.atitto.ukropchat.app.chat.ChatModule;
import com.atitto.ukropchat.app.mainscreen.MainScreenFragment;
import com.atitto.ukropchat.app.mainscreen.MainScreenModule;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FragmentsModule {

    @ContributesAndroidInjector(modules = {MainScreenModule.class})
    abstract MainScreenFragment contributeMainFragment();

    @ContributesAndroidInjector(modules = {ChatModule.class})
    abstract ChatFragment contributeChatFragment();
}