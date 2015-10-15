package com.medcorp.nevo.application;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import com.medcorp.nevo.activity.MainActivity;
import com.medcorp.nevo.ble.controller.OtaController;
import com.medcorp.nevo.ble.controller.SyncController;
import com.medcorp.nevo.ble.listener.OnSyncControllerListener;
import com.medcorp.nevo.ble.model.packet.NevoPacket;
import com.medcorp.nevo.ble.model.request.SensorRequest;
import com.medcorp.nevo.ble.util.Constants;
import com.medcorp.nevo.database.DatabaseHelper;

/**
 * Created by Karl on 10/15/15.
 */
public class ApplicationModel extends Application  implements OnSyncControllerListener {

    private SyncController  mSyncController;
    private OtaController mOtaController;
    //private NetworkController mNetworkController;
    private DatabaseHelper mDatabaseHelper;

    private Activity mCurrentActivity;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.w("Karl", "On create app model");
        mSyncController = SyncController.Singleton.getInstance(this);
        mOtaController = OtaController.Singleton.getInstance(this,false);
        mDatabaseHelper =  DatabaseHelper.getInstance(this);
    }

    @Override
    public void packetReceived(NevoPacket packet) {
        //dispatch to current activity
        if(mCurrentActivity !=null && !mCurrentActivity.isDestroyed()
                && mCurrentActivity instanceof MainActivity)
        {
            ((MainActivity)mCurrentActivity).packetReceived(packet);
        }
    }

    @Override
    public void connectionStateChanged(boolean isConnected) {
        //dispatch to current activity
        if(mCurrentActivity !=null && !mCurrentActivity.isDestroyed()
                && mCurrentActivity instanceof MainActivity)
        {
            ((MainActivity)mCurrentActivity).connectionStateChanged(isConnected);
        }
    }

    @Override
    public void firmwareVersionReceived(Constants.DfuFirmwareTypes whichfirmware, String version) {
        //dispatch to current activity
        if(mCurrentActivity !=null && !mCurrentActivity.isDestroyed()
                && mCurrentActivity instanceof MainActivity)
        {
            ((MainActivity)mCurrentActivity).firmwareVersionReceived(whichfirmware, version);
        }
    }

    /**
     * set current active activity
     * @param activity
     */
    public void setActiveActivity(Activity activity)
    {
        mCurrentActivity = activity;
    }

    /**
     * send request to nevo
     * @param request
     */
    public void sendRequest(SensorRequest request)
    {
        mSyncController.sendRequest(request);
    }

    /**
     * send request to MED server, such as upload data/login/register profile
     */
   // public void sendRequest(NetworkRequest request)
   // {

   // }

    public SyncController getSyncController(){return mSyncController;}
    public OtaController getOtaController(){return mOtaController;}
    public DatabaseHelper getDatabaseHelper(){return mDatabaseHelper;}

}
