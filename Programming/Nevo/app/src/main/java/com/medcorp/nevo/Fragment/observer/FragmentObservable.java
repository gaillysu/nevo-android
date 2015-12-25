package com.medcorp.nevo.fragment.observer;

import com.medcorp.nevo.model.Battery;

/**
 * Created by Karl on 10/16/15.
 */
public interface FragmentObservable {
    public void notifyDatasetChanged();
    public void notifyOnConnected();
    public void notifyOnDisconnected();
    public void batteryInfoReceived(Battery battery);
    public void findWatchSuccess();
    /**
     * add searching functions: @onSearching,@onSearchSuccess,@onSearchFailure,@onConnecting
     */
    public void onSearching();

    public void onSearchSuccess();

    public void onSearchFailure();

    public void onConnecting();

    //the  two functions @onSyncStart,@onSyncEnd
    public void onSyncStart();

    public void onSyncEnd();
}
