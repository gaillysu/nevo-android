package com.medcorp.nevo.activity.tutorial;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.WindowManager;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ActivityCompat.checkSelfPermission(TutorialPage5Activity.this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                new MaterialDialog.Builder(this)
                        .title(R.string.location_access_title)
                        .content(R.string.location_access_content)
                        .positiveText(getString(android.R.string.ok))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                ActivityCompat.requestPermissions(TutorialPage5Activity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                            }
                        })
                        .cancelable(false)
                        .show();
            }
        }

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
