package com.umnix.musmarket.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umnix.musmarket.MusMarketApplication;
import com.umnix.musmarket.R;
import com.umnix.musmarket.bus.StoreBus;
import com.umnix.musmarket.ui.adapter.StockAdapter;
import com.umnix.musmarket.ui.adapter.VerticalSpaceItemDecoration;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Subscription;
import timber.log.Timber;

public class InstrumentListFragment extends Fragment {

    @Inject
    protected StoreBus storeBus;

    @BindView(R.id.instrument_list_view)
    protected RecyclerView instrumentListView;

    private Unbinder unbinder;
    private Subscription stockSubscription;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_instrument_list, container, false);

        MusMarketApplication.getComponent().inject(this);
        unbinder = ButterKnife.bind(this, view);

        instrumentListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        instrumentListView.addItemDecoration(new VerticalSpaceItemDecoration(2));

        stockSubscription = storeBus.onStockListAvailable()
                .subscribe(
                        stockList -> {
                            instrumentListView.setAdapter(new StockAdapter(stockList.second));
                        },
                        throwable -> {
                            Timber.e(throwable, "Error on getting stock list");
                        });

        return view;
    }

    @Override
    public void onDestroyView() {
        if (stockSubscription != null) {
            stockSubscription.unsubscribe();
        }

        unbinder.unbind();

        super.onDestroyView();
    }
}
