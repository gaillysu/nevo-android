package com.medcorp.nevo.activity.observer;

/**
 * Created by Karl on 10/16/15.
 */
public interface ActivityObservable {

    public void notifyDatasetChanged();
    public void notifyOnConnected();
    public void notifyOnDisconnected();
}