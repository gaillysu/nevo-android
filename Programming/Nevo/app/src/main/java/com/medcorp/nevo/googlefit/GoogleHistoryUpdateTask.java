package com.medcorp.nevo.googlefit;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataSet;
import com.medcorp.nevo.listener.GoogleFitHistoryListener;

import java.util.concurrent.TimeUnit;

/**
 * Created by karl-john on 18/2/16.
 */
public class GoogleHistoryUpdateTask extends AsyncTask<DataSet,Void,Boolean>{

    private GoogleApiClient googleApiClient;
    private GoogleFitHistoryListener googleFitHistoryListener;

    public GoogleHistoryUpdateTask(GoogleFitManager googleFitManager, GoogleFitHistoryListener googleFitHistoryListener) {
        this.googleApiClient = googleFitManager.getApiClient();
        this.googleFitHistoryListener = googleFitHistoryListener;
    }

    @Override
    protected Boolean doInBackground(DataSet... dataSets) {
        DataSet dataSet = dataSets[0];
        if (googleApiClient == null || dataSet == null) {
            return false;

        }

        com.google.android.gms.common.api.Status insertStatus = Fitness.HistoryApi.insertData(googleApiClient, dataSet).await(1, TimeUnit.MINUTES);
        if (!insertStatus.isSuccess()) {
            return false;
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (aBoolean) {
            googleFitHistoryListener.onUpdateSuccess();
        }else{
            googleFitHistoryListener.onUpdateFailed();
        }
    }
}
