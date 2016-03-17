package com.medcorp.nevo.googlefit;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.medcorp.nevo.event.GoogleApiClientConnectionFailedEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by karl-john on 17/3/16.
 */
public class GoogleApiClientFailedListener implements GoogleApiClient.OnConnectionFailedListener {
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        EventBus.getDefault().post(new GoogleApiClientConnectionFailedEvent(connectionResult));
    }
}
