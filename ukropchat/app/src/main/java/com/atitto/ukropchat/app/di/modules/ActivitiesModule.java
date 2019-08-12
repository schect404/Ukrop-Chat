package com.atitto.ukropchat.app.di.modules;

import com.atitto.ukropchat.app.MainActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivitiesModule {

    @ContributesAndroidInjector(modules = {MainActivityModule.class})
    abstract MainActivity contributeActivityAndroidInjector();

}
