package com.umnix.musmarket.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.umnix.musmarket.MusMarketApplication;
import com.umnix.musmarket.R;
import com.umnix.musmarket.bus.StoreBus;
import com.umnix.musmarket.model.Store;
import com.umnix.musmarket.ui.StoreListActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.Subscription;
import timber.log.Timber;

public class StoreDetailsFragment extends Fragment {

    private static final String EXTRA_STORE_ID = "extra.store.id";

    @BindView(R.id.address_value)
    TextView addressView;

    @BindView(R.id.phone_value)
    TextView phoneView;

    @BindView(R.id.website_value)
    TextView webSiteView;

    @BindView(R.id.email_value)
    TextView emailView;

    @BindView(R.id.instruments_value)
    TextView instrumentsView;

    @Inject
    protected StoreBus storeBus;

    private Subscription phoneGrantedSubscription;
    private Subscription stockSubscription;

    private Unbinder unbinder;
    private Store store;

    public static StoreDetailsFragment newInstance(Store store) {
        StoreDetailsFragment fragment = new StoreDetailsFragment();
        Bundle args = new Bundle();
        args.putLong(EXTRA_STORE_ID, store.getId());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store_details, container, false);

        MusMarketApplication.getComponent().inject(this);
        unbinder = ButterKnife.bind(this, view);

        long storeId = getArguments().getLong(EXTRA_STORE_ID);
        store = ((StoreListActivity) getActivity()).getStoreById(storeId);

        if (store != null) {
            initValues();
        }

        stockSubscription = storeBus.onStockListAvailable()
                .subscribe(
                        stockList -> {
                            if (store != null && store.getId() == stockList.first.getId()) {
                                store.setInstrumentsCount(stockList.second);
                                instrumentsView.setText(store.getInstrumentCount() + "");
                            }
                        },
                        throwable -> {
                            Timber.e(throwable, "Error on getting stock list");
                        });

        phoneGrantedSubscription = storeBus.onGrantedLocation()
                .subscribe(
                        isGranted -> {
                            //TODO: mark availability to make a phone call
                        },
                        throwable -> {
                            Timber.e(throwable, "Error on setting location");
                        });

        return view;
    }

    private void initValues() {
        addressView.setText(store.getAddress());
        phoneView.setText(store.getPhone());
        webSiteView.setText(store.getWebsite());
        emailView.setText(store.getEmail());
        // set via callback
        //instrumentsView.setText(store.countInstruments());
    }

    @Override
    public void onDestroyView() {
        if (phoneGrantedSubscription != null) {
            phoneGrantedSubscription.unsubscribe();
        }
        if (stockSubscription != null) {
            stockSubscription.unsubscribe();
        }

        unbinder.unbind();
        super.onDestroyView();
    }

    @OnClick(R.id.phone_value)
    @SuppressWarnings("MissingPermission") //check is done
    protected void makePhoneCall() {
        if (TextUtils.isEmpty(store.getPhone())) {
            return;
        }

        StoreListActivity activity = (StoreListActivity) getActivity();
        if (!activity.hasPhonePermissions()) {
            activity.showPhonePermissionInfo();
            return;
        }

        Intent intent = new Intent(Intent.ACTION_CALL);

        intent.setData(Uri.parse("tel:" + store.getPhone()));
        getContext().startActivity(intent);
    }

    @OnClick(R.id.website_value)
    protected void browseWebSite() {
        if (TextUtils.isEmpty(store.getWebsite())) {
            return;
        }

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(store.getWebsite()));
        startActivity(browserIntent);
    }

    @OnClick(R.id.email_value)
    protected void sendEmail() {
        if (TextUtils.isEmpty(store.getEmail())) {
            return;
        }

        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + store.getEmail()));
        startActivity(Intent.createChooser(intent, getActivity().getString(R.string.send_email_caption)));
    }
}
