package com.medcorp.nevo.fragment.observer;

/**
 * Created by Karl on 10/16/15.
 */
public interface FragmentObservable {
    public void notifyDatasetChanged();
    public void notifyOnConnected();
    public void notifyOnDisconnected();
}
