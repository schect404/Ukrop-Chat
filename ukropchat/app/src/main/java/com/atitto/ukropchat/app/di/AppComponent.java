package com.atitto.ukropchat.app.di;

import com.atitto.data.DataModule;
import com.atitto.domain.DomainModule;
import com.atitto.ukropchat.app.BaseApplication;
import com.atitto.ukropchat.app.di.modules.ActivitiesModule;
import com.atitto.ukropchat.app.di.modules.AppModule;
import com.atitto.ukropchat.app.di.modules.FragmentsModule;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

@Component(modules = {ActivitiesModule.class, AppModule.class, AndroidSupportInjectionModule.class, DataModule.class, FragmentsModule.class, DomainModule.class})
@Singleton
public interface AppComponent extends AndroidInjector<BaseApplication> {

    @Component.Builder
    abstract class Builder extends AndroidInjector.Builder<BaseApplication> {
    }
}
