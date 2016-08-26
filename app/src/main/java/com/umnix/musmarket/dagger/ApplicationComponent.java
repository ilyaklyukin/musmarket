package com.umnix.musmarket.dagger;

import com.umnix.musmarket.MusMarketApplication;
import com.umnix.musmarket.job.LoadStoresJob;
import com.umnix.musmarket.ui.fragment.InstrumentListFragment;
import com.umnix.musmarket.ui.fragment.MapViewFragment;
import com.umnix.musmarket.ui.fragment.StoreDetailsFragment;
import com.umnix.musmarket.ui.StoreListActivity;
import com.umnix.musmarket.ui.fragment.StoreListFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    void inject(MusMarketApplication application);

    void inject(StoreListActivity storeListActivity);

    void inject(StoreListFragment storeListFragment);

    void inject(InstrumentListFragment instrumentListFragment);

    void inject(MapViewFragment mapViewFragment);

    void inject(StoreDetailsFragment storeDetailsFragment);

    void inject(LoadStoresJob job);
}