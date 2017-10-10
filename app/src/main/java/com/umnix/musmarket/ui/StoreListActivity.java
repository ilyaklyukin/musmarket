package com.umnix.musmarket.ui;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;

import com.umnix.musmarket.MusMarketApplication;
import com.umnix.musmarket.R;
import com.umnix.musmarket.Utility;
import com.umnix.musmarket.bus.StoreBus;
import com.umnix.musmarket.model.Store;
import com.umnix.musmarket.net.StoreApi;
import com.umnix.musmarket.ui.fragment.InstrumentListFragment;
import com.umnix.musmarket.ui.fragment.MapViewFragment;
import com.umnix.musmarket.ui.fragment.StoreDetailsFragment;
import com.umnix.musmarket.ui.fragment.StoreListFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class StoreListActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_LOCATION = 100;
    private static final int PERMISSIONS_REQUEST_PHONE = 101;

    @BindView(R.id.root)
    protected ViewGroup rootLayout;

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    @Inject
    protected StoreApi api;

    @Inject
    protected StoreBus storeBus;

    private CompositeSubscription subscriptions;

    private List<Store> stores = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MusMarketApplication.getComponent().inject(this);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        toolbar.setTitle(getResources().getString(R.string.app_name));
        setSupportActionBar(toolbar);

        subscriptions = new CompositeSubscription();

        addSubscription(storeBus.onShowMap(), o -> onSwitchMap(), "Switch map failed");

        attachStoreListFragment();
        loadStores();
    }

    private void addSubscription(Observable observable, Action1 onNext, String excMessage) {
        Subscription subscription = observable.subscribe(onNext, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Timber.e(throwable, excMessage);
                Snackbar.make(rootLayout, R.string.something_went_wrong, Snackbar.LENGTH_LONG).show();
            }
        });
        subscriptions.add(subscription);
    }

    @Override
    protected void onDestroy() {
        if (subscriptions != null) {
            subscriptions.unsubscribe();
        }

        super.onDestroy();
    }

    private void attachStoreListFragment() {
        createFragment(new StoreListFragment(), false);
    }

    private void attachInstrumentListFragment() {
        createFragment(new InstrumentListFragment(), true);
    }

    private void attachStoreDetailsFragment(Store store) {
        StoreDetailsFragment fragment = StoreDetailsFragment.newInstance(store);
        createFragment(fragment, true);
    }

    private void createFragment(Fragment fragment, boolean isInStack) {
        String tag = fragment.getClass().getName();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        if (isInStack) {
            ft.addToBackStack(tag);
        }
        ft.replace(R.id.container, fragment, tag);
        ft.commit();
    }

    public void onSelectStore(Store store) {
        toolbar.setTitle(store.getName());
        attachInstrumentListFragment();
        loadInstruments(store);
    }

    public void onShowStoreDetails(Store store) {
        toolbar.setTitle(store.getName());
        attachStoreDetailsFragment(store);
        //refresh instruments list to get total amount
        loadInstruments(store);
    }


    private void onSwitchMap() {
        createFragment(new MapViewFragment(), true);
    }

    private void loadStores() {
        if (!Utility.isNetworkAvailable(this)) {
            Snackbar.make(rootLayout, R.string.network_unavailable, Snackbar.LENGTH_LONG).show();
            return;
        }

        subscriptions.add(createStoreListSubscription());
    }

    private Subscription createStoreListSubscription() {
        return api.getStores()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(storeListResponse -> {
                    //progressBar.setVisibility(View.GONE);
                    if (storeListResponse.isSuccessful()) {
                        setStores(storeListResponse.body());
                    } else {
                        Snackbar.make(rootLayout, R.string.something_went_wrong, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }, throwable -> Snackbar.make(rootLayout, R.string.something_went_wrong, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show());
    }

    private void loadInstruments(Store store) {
        if (!Utility.isNetworkAvailable(this)) {
            Snackbar.make(rootLayout, R.string.network_unavailable, Snackbar.LENGTH_LONG).show();
            return;
        }

        subscriptions.add(createInstrumentListSubscription(store));
    }

    private Subscription createInstrumentListSubscription(Store store) {
        return api.getInstruments(store.getId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(stockListResponse -> {
                    //progressBar.setVisibility(View.GONE);

                    if (stockListResponse.isSuccessful()) {
                        Timber.d("Loaded stock: %s", stockListResponse.body());
                        store.setInstrumentsCount(stockListResponse.body());
                        storeBus.setStockList(new Pair<>(store, stockListResponse.body()));
                    } else {
                        Snackbar.make(rootLayout, R.string.something_went_wrong, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }, throwable -> {
                    Timber.e(throwable, "Error on loading stock");
                    Snackbar.make(rootLayout, R.string.something_went_wrong, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                });
    }

    public List<Store> getStores() {
        return new ArrayList<>(stores);
    }

    public void setStores(List<Store> stores) {
        //copy to avoid unpredictable changes
        this.stores.addAll(stores);

        storeBus.setStoreList(stores);
    }

    public Store getStoreById(long id) {
        for (Store store : stores) {
            if (store.getId() == id) {
                return store;
            }
        }

        return null;
    }

    @Override
    @SuppressWarnings("MissingPermission")
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    storeBus.locationGranted(true);
                } else {
                    storeBus.locationGranted(false);
                }
                return;
            }
            case PERMISSIONS_REQUEST_PHONE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    storeBus.phoneGranted(true);
                } else {
                    storeBus.phoneGranted(false);
                }
                return;
            }
        }
    }

    private void requestLocationPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_LOCATION);
        }
    }

    private void requestPhonePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                    new String[]{
                            Manifest.permission.CALL_PHONE}, PERMISSIONS_REQUEST_PHONE);
        }
    }

    public boolean hasLocationPermissions() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || isLocationPermissionGranted();
    }

    public boolean hasPhonePermissions() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || isPhonePermissionGranted();
    }

    private boolean isLocationPermissionGranted() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean isPhonePermissionGranted() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED;
    }

    public void showLocationPermissionInfo() {
        // User pressed not to ask again
        boolean shouldRequest = ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if (shouldRequest) {
            openPermissionRemindDialog(R.string.permissions_location_remind);
        } else {
            requestLocationPermissions();
        }
    }

    public void showPhonePermissionInfo() {
        // User pressed not to ask again
        boolean shouldRequest = ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CALL_PHONE);

        if (shouldRequest) {
            openPermissionRemindDialog(R.string.permissions_contact_remind);
        } else {
            requestPhonePermissions();
        }
    }

    private void openPermissionRemindDialog(int messageId) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(messageId);
        builder.setCancelable(false);

        builder.setPositiveButton(R.string.btn_accept, (dialog, which) -> {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        });

        builder.show();
    }
}
