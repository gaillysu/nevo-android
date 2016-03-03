package com.medcorp.nevo.activity.observer;

/**
 * Created by Karl on 10/16/15.
 */
public interface ActivityObservable {

    public void notifyOnConnected();
    public void notifyOnDisconnected();
    /**
     * add searching functions: @onSearching,@onSearchSuccess,@onSearchFailure,@onConnecting
     */
    public void onSearching();

    public void onSearchSuccess();

    public void onSearchFailure();

    public void onConnecting();

}