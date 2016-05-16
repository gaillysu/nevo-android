package com.medcorp.nevo.network.med.service;

import com.medcorp.nevo.network.med.retrofit.MedCorp;
import com.octo.android.robospice.retrofit.RetrofitGsonSpiceService;

import retrofit.RestAdapter;

/**
 * Created by Karl on 3/15/16.
 */
public class BaseMedRetroService extends RetrofitGsonSpiceService {

    private final static String BASE_URL = "http://api.nevowatch.com";

    @Override
    public void onCreate() {
        super.onCreate();
        addRetrofitInterface(MedCorp.class);
    }

    @Override
    protected String getServerUrl() {
        return BASE_URL;
    }

    @Override
    protected RestAdapter.Builder createRestAdapterBuilder() {
        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.HEADERS)
                .setConverter(getConverter())
                .setEndpoint(getServerUrl());
        return builder;
    }
}
