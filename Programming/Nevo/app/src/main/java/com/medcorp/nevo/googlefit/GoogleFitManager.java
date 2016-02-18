package com.medcorp.nevo.googlefit;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessStatusCodes;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.tasks.Tasks;
import com.medcorp.nevo.R;
import com.medcorp.nevo.view.ToastHelper;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by karl-john on 20/11/15.
 */
public class GoogleFitManager implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    public final int REQUEST_OAUTH = 1001;
    private GoogleApiClient apiClient;

    private GoogleApiClient.ConnectionCallbacks connectionCallbacks;
    private GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener;

    private Context context;
    private Activity activity;

    public GoogleFitManager(Context context) {
        this.context = context;
        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(context, Arrays.asList(Scopes.FITNESS_ACTIVITY_READ_WRITE, Scopes.FITNESS_BODY_READ_WRITE));
        Tasks task = new Tasks.Builder(AndroidHttp.newCompatibleTransport(), GsonFactory.getDefaultInstance(), credential)
                //TODO put into keys.xml
                .setApplicationName("nevo")
                .build();
        build();
    }

    private void build(){
        apiClient = new GoogleApiClient.Builder(context)
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
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

    public void setActivityForResults(AppCompatActivity activity){
        this.activity = activity;
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (result.getErrorCode() == ConnectionResult.SIGN_IN_REQUIRED ||
                result.getErrorCode() == FitnessStatusCodes.NEEDS_OAUTH_PERMISSIONS) {
            try {
                if (activity != null) {
                    result.startResolutionForResult(activity, REQUEST_OAUTH);
                }
            } catch (IntentSender.SendIntentException e) {
                ToastHelper.showShortToast(context, R.string.google_fit_could_not_login);
            }
        } else {
            ToastHelper.showShortToast(context,R.string.google_fit_connecting);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        ToastHelper.showLongToast(context, R.string.google_fit_connected);
        new GoogleHistoryUpdateTask(this,null).execute(getData());
    }

    @Override
    public void onConnectionSuspended(int result) {
        if (result == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
            ToastHelper.showShortToast(context,R.string.google_fit_network_lost);
        } else if (result == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
            ToastHelper.showShortToast(context,R.string.google_fit_service_disconnected);
        }else{
            ToastHelper.showShortToast(context,R.string.google_fit_unknown_network);
        }
    }

    private DataSet getData(){
        Calendar calendar = Calendar.getInstance();
        Date now = new Date();
        calendar.setTime(now);
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.HOUR_OF_DAY, -1);
        long startTime = calendar.getTimeInMillis();

        DataSource dataSource = new DataSource.Builder()
                .setAppPackageName(context)
                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .setName("nevo - step count")
                .setType(DataSource.TYPE_RAW)
                .build();

        int stepCountDelta = 101;
        DataSet dataSet = DataSet.create(dataSource);
        DataPoint dataPoint = dataSet.createDataPoint();
        dataPoint.getValue(Field.FIELD_STEPS).setInt(stepCountDelta);
        dataPoint.setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS);
        dataSet.add(dataPoint);
        return dataSet;
    }


    public void setOnConnectionFailedListener(GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {
        this.onConnectionFailedListener = onConnectionFailedListener;
    }

    public void setConnectionCallbacks(GoogleApiClient.ConnectionCallbacks connectionCallbacks) {
        this.connectionCallbacks = connectionCallbacks;
    }

    protected GoogleApiClient getApiClient() {
        return apiClient;
    }
}


