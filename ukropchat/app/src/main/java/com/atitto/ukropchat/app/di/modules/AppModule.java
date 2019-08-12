package com.atitto.ukropchat.app.di.modules;

import android.content.Context;

import com.atitto.ukropchat.app.BaseApplication;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    @Provides
    @Singleton
    static Context context(BaseApplication app) {
        return app.getApplicationContext();
    }

}
