package com.umnix.musmarket.dagger;

import android.content.Context;

import com.path.android.jobqueue.JobManager;
import com.path.android.jobqueue.config.Configuration;
import com.umnix.musmarket.MusMarketApplication;
import com.umnix.musmarket.R;
import com.umnix.musmarket.bus.StoreBus;
import com.umnix.musmarket.net.StoreApi;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

@Module
public class ApplicationModule {

    private final MusMarketApplication musMarketApplication;

    public ApplicationModule(MusMarketApplication kweakApplication) {
        this.musMarketApplication = kweakApplication;
    }

    public void bootstrap() {
        Timber.plant(new Timber.DebugTree());
    }

    @Provides
    @Singleton
    Context provideApplicationContext() {
        return this.musMarketApplication;
    }

    @Provides
    @Singleton
    StoreApi provideStoreApiService() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(musMarketApplication.getResources().getString(R.string.base_stores_url))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(client)
                .build();
        return retrofit.create(StoreApi.class);
    }

    @Provides
    @Singleton
    StoreBus provideStoreBus() {
        return new StoreBus();
    }

    @Provides
    @Singleton
    JobManager provideJobManager() {
        Configuration.Builder builder = new Configuration.Builder(musMarketApplication);

        return new JobManager(musMarketApplication, builder.build());
    }
}
