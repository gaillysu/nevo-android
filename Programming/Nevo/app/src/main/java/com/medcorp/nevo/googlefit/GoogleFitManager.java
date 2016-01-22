package com.medcorp.nevo.googlefit;

import android.content.IntentSender;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessStatusCodes;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.tasks.Tasks;
import com.medcorp.nevo.R;
import com.medcorp.nevo.activity.base.BaseActivity;
import com.medcorp.nevo.view.ToastHelper;

import java.util.Arrays;

/**
 * Created by karl-john on 20/11/15.
 */
public class GoogleFitManager implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    public final int REQUEST_OAUTH = 1001;
    private GoogleApiClient apiClient;
    private GoogleAccountCredential credential;

    private GoogleApiClient.ConnectionCallbacks connectionCallbacks;
    private GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener;

    private BaseActivity activity;

    public GoogleFitManager(BaseActivity baseActivity) {
        this.activity = baseActivity;
        credential = GoogleAccountCredential.usingOAuth2(activity, Arrays.asList(Scopes.FITNESS_ACTIVITY_READ_WRITE, Scopes.FITNESS_BODY_READ_WRITE));
        Tasks task = new Tasks.Builder(AndroidHttp.newCompatibleTransport(), GsonFactory.getDefaultInstance(), credential)
                //TODO put into keys.xml
                .setApplicationName("Nevo Watch")
                .build();
        build();
    }

    private void build(){
        apiClient = new GoogleApiClient.Builder(activity)
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_BODY_READ_WRITE))
                .addConnectionCallbacks(getConnectionCallback())
                .addOnConnectionFailedListener(getConnectionFailedListener())
                .useDefaultAccount()
                .build();
    }

    public void connect(){
        apiClient.connect();
    }

    public void disconnect(){
        apiClient.disconnect();
    }

    public boolean isConnected(){
        return apiClient.isConnected();
    }

    public boolean isConnecting(){
        return apiClient.isConnecting();
    }

    private GoogleApiClient.OnConnectionFailedListener getConnectionFailedListener() {
        if (onConnectionFailedListener == null){
            return this;
        }
        return onConnectionFailedListener;
    }

    private GoogleApiClient.ConnectionCallbacks getConnectionCallback() {
        if (connectionCallbacks == null){
            return this;
        }
        return connectionCallbacks;
    }

    public void switchAccount(ResultCallback<Status> callback){
        PendingResult<Status> pendingResult = apiClient.clearDefaultAccountAndReconnect();
        pendingResult.setResultCallback(callback);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (result.getErrorCode() == ConnectionResult.SIGN_IN_REQUIRED ||
                result.getErrorCode() == FitnessStatusCodes.NEEDS_OAUTH_PERMISSIONS) {
            try {
                result.startResolutionForResult(activity, REQUEST_OAUTH);
            } catch (IntentSender.SendIntentException e) {
                ToastHelper.showShortToast(activity, R.string.google_fit_could_not_login);
            }
        } else {
            ToastHelper.showShortToast(activity,R.string.google_fit_connecting);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        ToastHelper.showShortToast(activity,R.string.google_fit_connected);
    }

    @Override
    public void onConnectionSuspended(int result) {
        if (result == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
            ToastHelper.showShortToast(activity,R.string.google_fit_network_lost);
        } else if (result == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
            ToastHelper.showShortToast(activity,R.string.google_fit_service_disconnected);
        }else{
            ToastHelper.showShortToast(activity,R.string.google_fit_unknown_network);
        }
    }

    private boolean sendSteps(){
        return false;
    }

    private boolean sendSleep(){
        return false;
    }

    public void setOnConnectionFailedListener(GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {
        this.onConnectionFailedListener = onConnectionFailedListener;
    }

    public void setConnectionCallbacks(GoogleApiClient.ConnectionCallbacks connectionCallbacks) {
        this.connectionCallbacks = connectionCallbacks;
    }
}
