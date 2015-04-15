package com.nevowatch.nevo;

import android.app.Application;
import com.nevowatch.nevo.ble.controller.OnSyncControllerListener;
import com.nevowatch.nevo.ble.controller.SyncController;
import com.nevowatch.nevo.ble.model.packet.NevoPacket;

/**
 * Gobal Variables in the application
 */
public class MyApplication extends Application implements OnSyncControllerListener {

    private static SyncController mSyncController;
    public static final String ALARMFRAGMENT = "AlarmFragment";
    public static final String GOALFRAGMENT = "GoalFragment";
    public static final String WELCOMEFRAGMENT = "WelcomeFragment";
    public static final String CONNECTFRAGMENT = "ConnectAnimationFragment";

    @Override
    public void onCreate() {
        super.onCreate();
        if(mSyncController == null){
            mSyncController = SyncController.Factory.newInstance(this);
        }
    }

    public static SyncController getSyncController() {
        return mSyncController;
    }

    @Override
    public void packetReceived(NevoPacket packet) {

    }

    @Override
    public void connectionStateChanged(boolean isConnected) {

    }

}
