package com.medcorp.nevo.activity.tutorial;

import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.medcorp.nevo.R;
import com.medcorp.nevo.activity.base.BaseActivity;
import com.medcorp.nevo.event.ConnectionStateChangedEvent;
import com.medcorp.nevo.event.SearchEvent;
import com.medcorp.nevo.view.RoundProgressBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by gaillysu on 16/1/14.
 */
public class TutorialPage5Activity extends BaseActivity{

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
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe
    public void onEvent(SearchEvent event){
        Log.w("Karl", "On event = " + event.getStatus());
        switch (event.getStatus()) {
            case FAILED:   runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    startActivity(TutorialPageFailedActivity.class);
                }
            });
                break;
            case SEARCHING:
                Log.w("Karl","Searching.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        searchIndex = (searchIndex + 1) % 20;
                        roundProgressBar.setProgress(searchIndex * 5);
                    }
                });
                break;
        }
    }

    @Subscribe
    public void onEvent(ConnectionStateChangedEvent event){
        if (event.isConnected()){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    startConnectedActivity();
                }
            });
        }
    }
}
