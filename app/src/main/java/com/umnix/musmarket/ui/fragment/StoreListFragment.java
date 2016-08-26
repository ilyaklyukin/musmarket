package com.umnix.musmarket.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umnix.musmarket.MusMarketApplication;
import com.umnix.musmarket.R;
import com.umnix.musmarket.bus.StoreBus;
import com.umnix.musmarket.model.Store;
import com.umnix.musmarket.ui.OnItemClickListener;
import com.umnix.musmarket.ui.StoreListActivity;
import com.umnix.musmarket.ui.adapter.StoreAdapter;
import com.umnix.musmarket.ui.adapter.VerticalSpaceItemDecoration;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.Subscription;
import timber.log.Timber;

public class StoreListFragment extends Fragment implements OnItemClickListener<Store> {

    @Inject
    protected StoreBus storeBus;

    @BindView(R.id.store_list_view)
    protected RecyclerView storeListView;

    private Unbinder unbinder;
    private Subscription storeListSubscription;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store_list, container, false);

        MusMarketApplication.getComponent().inject(this);
        unbinder = ButterKnife.bind(this, view);

        storeListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        storeListView.addItemDecoration(new VerticalSpaceItemDecoration(1));

        storeListSubscription = storeBus.onStoreListAvailable()
                .subscribe(
                        storeList -> {
                            storeListView.setAdapter(new StoreAdapter(storeList, this));
                        },
                        throwable -> {
                            Timber.e(throwable, "Error on getting store list");
                        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(getActivity().getResources().getString(R.string.app_name));
    }

    @Override
    public void onItemClick(Store store) {
        Timber.d("Selected store: %s", store);

        ((StoreListActivity) getActivity()).onSelectStore(store);
    }

    @Override
    public void onInfoIconClick(Store store) {
        Timber.d("Selected store info: %s", store);

        ((StoreListActivity) getActivity()).onShowStoreDetails(store);
    }


    @Override
    public void onDestroyView() {
        if (storeListSubscription != null) {
            storeListSubscription.unsubscribe();
        }

        unbinder.unbind();

        super.onDestroyView();
    }

    @OnClick(R.id.map_button)
    protected void onSwitchMap() {
        storeBus.showMap();
    }

}
