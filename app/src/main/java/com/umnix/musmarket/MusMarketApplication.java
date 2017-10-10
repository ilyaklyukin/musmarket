package com.umnix.musmarket;

import android.app.Application;
import android.content.Context;

import com.birbit.android.jobqueue.JobManager;
import com.umnix.musmarket.dagger.ApplicationComponent;
import com.umnix.musmarket.dagger.ApplicationModule;
import com.umnix.musmarket.dagger.DaggerApplicationComponent;

import javax.inject.Inject;

public class MusMarketApplication extends Application {

    private static ApplicationComponent applicationComponent;

    private static Context context;

    @Inject
    protected JobManager jobManager;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        ApplicationModule module = new ApplicationModule(this);
        setComponent(DaggerApplicationComponent.builder()
                .applicationModule(module)
                .build());

        getComponent().inject(this);
        module.bootstrap();
    }

    public static Context getContext() {
        return context;
    }

    public static ApplicationComponent getComponent() {
        return applicationComponent;
    }

    public void setComponent(ApplicationComponent applicationComponent) {
        this.applicationComponent = applicationComponent;
    }
}
