package com.medcorp.nevo.activity.tutorial;

import android.os.Bundle;
import android.view.WindowManager;

import com.medcorp.nevo.R;
import com.medcorp.nevo.activity.base.BaseActivity;
import com.medcorp.nevo.activity.observer.ActivityObservable;
import com.medcorp.nevo.model.Battery;
import com.medcorp.nevo.view.RoundProgressBar;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by gaillysu on 16/1/14.
 */
public class TutorialPage5Activity extends BaseActivity implements ActivityObservable{

    @Bind(R.id.roundProgressBar)
    RoundProgressBar  roundProgressBar;

    private int searchIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_tutorial_page_5);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getModel().observableActivity(this);
        if(getModel().isWatchConnected())
        {
            startConnectedActivity();
        }
        else
        {
            initSearchScreen();
            getModel().startConnectToWatch(true);
        }
    }

    private void initSearchScreen()
    {
        searchIndex = 0;
        roundProgressBar.setProgress(2);
    }
    private void startConnectedActivity()
    {
        startActivity(TutorialPageSuccessActivity.class);
        finish();
    }
    @Override
    public void notifyDatasetChanged() {

    }

    @Override
    public void notifyOnConnected() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startConnectedActivity();
            }
        });
    }

    @Override
    public void notifyOnDisconnected() {
        //DO NOTHING WHEN HAS GOT CONNECTED ,MAINACTIVITY WILL CONTINUE CONNECT WATCH AGAIN
    }

    @Override
    public void batteryInfoReceived(Battery battery) {

    }

    @Override
    public void findWatchSuccess() {

    }

    @Override
    public void onSearching() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                searchIndex = (searchIndex + 1) % 20;
                roundProgressBar.setProgress(searchIndex * 5);
            }
        });
    }

    @Override
    public void onSearchSuccess() {

    }

    @Override
    public void onSearchFailure() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startActivity(TutorialPageFailedActivity.class);
            }
        });
    }

    @Override
    public void onConnecting() {

    }

    @Override
    public void onSyncStart() {

    }

    @Override
    public void onSyncEnd() {

    }

    @Override
    public void onInitializeStart() {

    }

    @Override
    public void onInitializeEnd() {

    }

    @Override
    public void onRequestResponse(boolean success) {

    }
}
