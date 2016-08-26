package com.umnix.musmarket.bus;

import android.support.v4.util.Pair;

import com.umnix.musmarket.model.Stock;
import com.umnix.musmarket.model.Store;

import java.util.List;

import rx.Observable;
import rx.subjects.BehaviorSubject;

public class StoreBus {

    private BehaviorSubject<List<Store>> storeListSubject = BehaviorSubject.create();
    private BehaviorSubject<Pair<Store, List<Stock>>> stockListSubject = BehaviorSubject.create();
    private BehaviorSubject mapViewSubject = BehaviorSubject.create();
    private BehaviorSubject<Boolean> locationPermissionGrantedSubject = BehaviorSubject.create();
    private BehaviorSubject<Boolean> phonePermissionGrantedSubject = BehaviorSubject.create();

    public void setStoreList(List<Store> storeList) {
        storeListSubject.onNext(storeList);
    }

    public Observable<List<Store>> onStoreListAvailable() {
        return storeListSubject;
    }

    public void setStockList(Pair<Store, List<Stock>> stockList) {
        stockListSubject.onNext(stockList);
    }

    public Observable<Pair<Store, List<Stock>>> onStockListAvailable() {
        return stockListSubject;
    }

    public void showMap() {
        mapViewSubject.onNext(null);
    }

    public Observable onShowMap() {
        return mapViewSubject;
    }

    public void locationGranted(Boolean isGranted) {
        locationPermissionGrantedSubject.onNext(isGranted);
    }

    public Observable<Boolean> onGrantedLocation() {
        return locationPermissionGrantedSubject;
    }

    public void phoneGranted(Boolean isGranted) {
        phonePermissionGrantedSubject.onNext(isGranted);
    }

    public Observable<Boolean> onGrantedPhone() {
        return phonePermissionGrantedSubject;
    }
}
