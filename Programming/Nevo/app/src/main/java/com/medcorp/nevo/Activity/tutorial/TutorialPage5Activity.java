package com.medcorp.nevo.activity.tutorial;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.medcorp.nevo.R;
import com.medcorp.nevo.activity.MainActivity;
import com.medcorp.nevo.activity.base.BaseActivity;
import com.medcorp.nevo.activity.observer.ActivityObservable;
import com.medcorp.nevo.ble.util.Constants;
import com.medcorp.nevo.model.Battery;
import com.medcorp.nevo.view.RoundProgressBar;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by gaillysu on 16/1/14.
 */
public class TutorialPage5Activity extends BaseActivity implements ActivityObservable, View.OnClickListener{

    @Bind(R.id.nextTextView)
    TextView nextTextView;

    @Bind(R.id.retryTextView)
    TextView retryTextView;

    @Bind(R.id.activity_tutorial_page5_searching_layout)
    RelativeLayout searchingLayout;

    @Bind(R.id.activity_tutorial_page5_search_failure_layout)
    RelativeLayout searchFailureLayout;

    @Bind(R.id.activity_tutorial_page5_search_success_layout)
    RelativeLayout searchSuccessLayout;

    @Bind(R.id.roundProgressBar)
    RoundProgressBar  roundProgressBar;

    private int searchIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_tutorial_page_5);
        ButterKnife.bind(this);
        getModel().observableActivity(this);
        nextTextView.setOnClickListener(this);
        retryTextView.setOnClickListener(this);
        if(getModel().isWatchConnected())
        {
            initConnectedScreen();
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
        searchingLayout.setVisibility(View.VISIBLE);
        searchSuccessLayout.setVisibility(View.GONE);
        searchFailureLayout.setVisibility(View.GONE);
    }
    private void initConnectedScreen()
    {
        searchSuccessLayout.setVisibility(View.VISIBLE);
        searchingLayout.setVisibility(View.GONE);
        searchFailureLayout.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.nextTextView)
        {
            startActivity(new Intent(this, MainActivity.class));
            overridePendingTransition(R.anim.anim_enter, R.anim.anim_exit);
            getSharedPreferences(Constants.PREF_NAME, 0).edit().putBoolean(Constants.FIRST_FLAG, false).commit();
            finish();
        }
        else if(v.getId() == R.id.retryTextView)
        {
            initSearchScreen();
            getModel().startConnectToWatch(true);

        }
    }

    @Override
    public void notifyDatasetChanged() {

    }

    @Override
    public void notifyOnConnected() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                searchSuccessLayout.setVisibility(View.VISIBLE);
                searchingLayout.setVisibility(View.GONE);
                searchFailureLayout.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void notifyOnDisconnected() {
        //DO NOTHING WHEN HAS GOT CONNECTED,MAINACTIVITY WILL CONTINUE CONNECT WATCH AGAIN
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
                searchSuccessLayout.setVisibility(View.GONE);
                searchingLayout.setVisibility(View.GONE);
                searchFailureLayout.setVisibility(View.VISIBLE);
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
