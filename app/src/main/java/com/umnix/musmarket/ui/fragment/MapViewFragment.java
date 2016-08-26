package com.umnix.musmarket.ui.fragment;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.umnix.musmarket.MusMarketApplication;
import com.umnix.musmarket.R;
import com.umnix.musmarket.bus.StoreBus;
import com.umnix.musmarket.model.Location;
import com.umnix.musmarket.model.Store;
import com.umnix.musmarket.ui.StoreListActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Subscription;
import timber.log.Timber;

public class MapViewFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    @Inject
    protected StoreBus storeBus;

    private MapView mapView;
    private GoogleMap map;

    private Unbinder unbinder;
    private Subscription locationGrantedSubscription;

    private Map<String, Store> markers = new HashMap<>();

    @Override
    @SuppressWarnings("MissingPermission") // check is done
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        MusMarketApplication.getComponent().inject(this);
        unbinder = ButterKnife.bind(this, view);

        mapView = (MapView) view.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(this);

        locationGrantedSubscription = storeBus.onGrantedLocation()
                .subscribe(
                        isGranted -> {
                            map.setMyLocationEnabled(isGranted);
                        },
                        throwable -> {
                            Timber.e(throwable, "Error on setting location");
                        });

        return view;
    }


    @Override
    public void onMapReady(GoogleMap map) {

        this.map = map;

        map.setOnMarkerClickListener(this);

        moveToMarketCity();

        placeStores();

        showOwnLocation();
    }

    private void moveToMarketCity() {
        Resources resources = getActivity().getResources();

        int zoom = resources.getInteger(R.integer.map_zoom);
        int city_latitude = resources.getInteger(R.integer.market_city_latitude);
        int city_longitude = resources.getInteger(R.integer.market_city_longitude);

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new Location(city_latitude, city_longitude).getCoordinates(), zoom));
    }

    @SuppressWarnings("MissingPermission") //check is done
    private void showOwnLocation() {
        StoreListActivity activity = (StoreListActivity) getActivity();
        if (!activity.hasLocationPermissions()) {
            activity.showLocationPermissionInfo();
            return;
        }

        map.setMyLocationEnabled(true);
    }

    private void placeStores() {
        if (!(getActivity() instanceof StoreListActivity)) {
            return;
        }

        markers.clear();

        List<Store> stores = ((StoreListActivity) getActivity()).getStores();
        if (stores == null) {
            return;
        }

        for (Store store : stores) {
            Marker marker = map.addMarker(
                    new MarkerOptions()
                            .position(store.getLocation().getCoordinates())
                            .title(store.getName())
                            .snippet(store.getPhone()));
            markers.put(marker.getId(), store);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();

        Store store = markers.get(marker.getId());
        if (store == null) {
            return false;
        }

        ((StoreListActivity) getActivity()).onSelectStore(store);

        return true;
    }

    @Override
    public void onDestroyView() {
        if (locationGrantedSubscription != null) {
            locationGrantedSubscription.unsubscribe();
        }

        unbinder.unbind();
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onPause() {
        if (mapView != null) {
            mapView.onPause();
        }
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }

    @Override
    public void onDestroy() {
        if (mapView != null) {
            try {
                mapView.onDestroy();
            } catch (NullPointerException e) {
                Timber.e(e, "Error while attempting MapView.onDestroy(), ignoring exception");
            }
        }
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) {
            mapView.onLowMemory();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView != null) {
            mapView.onSaveInstanceState(outState);
        }
    }
}
