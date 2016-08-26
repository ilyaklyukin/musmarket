package com.umnix.musmarket.net;

import com.umnix.musmarket.model.Stock;
import com.umnix.musmarket.model.Store;

import java.util.List;

import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

public interface StoreApi {
    @GET("stores")
    Observable<Response<List<Store>>> getStores();

    @GET("stores/{storeId}/instruments")
    Observable<Response<List<Stock>>> getInstruments(@Path("storeId") long storeId);
}
