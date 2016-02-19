package com.medcorp.nevo.googlefit;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.tasks.Tasks;

import java.util.Arrays;


/**
 * Created by karl-john on 20/11/15.
 */
public class GoogleFitManager{

    private GoogleApiClient apiClient;

    private Context context;
    private Activity activity;

    public GoogleFitManager(Context context,GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {
        this.context = context;
        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(context, Arrays.asList(Scopes.FITNESS_ACTIVITY_READ_WRITE, Scopes.FITNESS_BODY_READ_WRITE));
        Tasks task = new Tasks.Builder(AndroidHttp.newCompatibleTransport(), GsonFactory.getDefaultInstance(), credential)
                //TODO put into keys.xml
                .setApplicationName("nevo")
                .build();
        build(connectionCallbacks, onConnectionFailedListener);
    }

    private void build(GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener){
        apiClient = new GoogleApiClient.Builder(context)
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addConnectionCallbacks(connectionCallbacks)
                .addOnConnectionFailedListener(onConnectionFailedListener)
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

    public void switchAccount(ResultCallback<Status> callback){
        PendingResult<Status> pendingResult = apiClient.clearDefaultAccountAndReconnect();
        pendingResult.setResultCallback(callback);
    }

    public void setActivityForResults(AppCompatActivity activity){
        this.activity = activity;
    }


    protected GoogleApiClient getApiClient() {
        return apiClient;
    }

    public Activity getActivity() {
        return activity;
    }
}


