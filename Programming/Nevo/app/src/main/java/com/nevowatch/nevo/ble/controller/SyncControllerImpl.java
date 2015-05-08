package com.nevowatch.nevo.ble.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import com.nevowatch.nevo.GoogleFitManager;
import com.nevowatch.nevo.Model.DailyHistory;
import com.nevowatch.nevo.ble.ble.GattAttributes.SupportedService;
import com.nevowatch.nevo.ble.kernel.BLEConnectTimeoutException;
import com.nevowatch.nevo.ble.kernel.BLENotSupportedException;
import com.nevowatch.nevo.ble.kernel.BLEUnstableException;
import com.nevowatch.nevo.ble.kernel.BluetoothDisabledException;
import com.nevowatch.nevo.ble.kernel.NevoBT;
import com.nevowatch.nevo.ble.kernel.OnConnectListener;
import com.nevowatch.nevo.ble.kernel.OnDataReceivedListener;
import com.nevowatch.nevo.ble.kernel.OnDisconnectListener;
import com.nevowatch.nevo.ble.kernel.OnExceptionListener;
import com.nevowatch.nevo.ble.model.packet.DailyStepsNevoPacket;
import com.nevowatch.nevo.ble.model.packet.DailyTrackerNevoPacket;
import com.nevowatch.nevo.ble.model.packet.NevoPacket;
import com.nevowatch.nevo.ble.model.packet.DailyTrackerInfoNevoPacket;
import com.nevowatch.nevo.ble.model.packet.NevoRawData;
import com.nevowatch.nevo.ble.model.request.GetStepsGoalNevoRequest;
import com.nevowatch.nevo.Model.Goal;
import com.nevowatch.nevo.ble.model.request.SensorRequest;
import com.nevowatch.nevo.ble.model.request.SetAlarmNevoRequest;
import com.nevowatch.nevo.ble.model.request.SetGoalNevoRequest;
import com.nevowatch.nevo.ble.model.request.SetProfileNevoRequest;
import com.nevowatch.nevo.ble.model.request.SetRtcNevoRequest;
import com.nevowatch.nevo.ble.model.request.WriteSettingNevoRequest;
import com.nevowatch.nevo.ble.model.request.SetCardioNevoRequest;
import com.nevowatch.nevo.ble.model.request.ReadDailyTrackerInfoNevoRequest;
import com.nevowatch.nevo.ble.model.request.ReadDailyTrackerNevoRequest;
import com.nevowatch.nevo.ble.model.request.SetNotificationNevoRequest;
import com.nevowatch.nevo.ble.util.Constants;
import com.nevowatch.nevo.ble.util.Optional;
import com.nevowatch.nevo.ble.util.QueuedMainThreadHandler;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;
import com.nevowatch.nevo.R;

public class SyncControllerImpl implements SyncController{
    private final static String TAG = "SyncControllerImpl";
	Context mContext;
	private static final int SYNC_INTERVAL = 1*30*60*1000; //every half hour , do sync when connected again
	private NevoBT mNevoBT;
	private OnSyncControllerListener mOnSyncControllerListener;
    private ArrayList<NevoRawData> mPacketsbuffer = new ArrayList<NevoRawData>();

    private ArrayList<DailyHistory> mSavedDailyHistory = new ArrayList<DailyHistory>();
    private int mCurrentDay = 0;
    private int mTimeOutcount = 0;
    private boolean mVisible = true;
    //IMPORT!!!!, every get connected, will do sync profile data and activity data with Nevo
    //it perhaps long time(sync activity data perhaps need long time, MAX total 7 days)
    //so before sync finished, disable setGoal/setAlarm/getGoalSteps
    //make sure  the whole received packets
    private boolean mIsSendRequestLocked = true;

    private SensorRequest mCurrentrequest;

    /** The Handler of the ui thread. */
    Handler mUiThread = new Handler(Looper.getMainLooper());
    //send Command timeout
    int MAX_TIMEOUT = 2000;
    //when got timeout, then do disconnect Nevo from smartphone peer, and notify Activity layer
    Runnable mSendCommandTimeOut = new Runnable() {
        @Override
        public void run() {
            Log.e("SyncControllerImpl","send command timeout:" + mCurrentrequest.getClass().getName());
            if(mNevoBT.isDisconnected())
                mOnSyncControllerListener.connectionStateChanged(false);
            else
            {
                mNevoBT.disconnect(new Optional<String>(mNevoBT.getSaveAddress()));
                showMessage(R.string.ble_command_timeout_title,R.string.ble_connecttimeout);
            }

        }
    };

	/**
	 * This listener is called when new data is received
	 */
	private OnDataReceivedListener mDataReceivedListener = new OnDataReceivedListener() {

		@Override
		public void onDataReceived(com.nevowatch.nevo.ble.model.packet.SensorData data) {
            mUiThread.removeCallbacks(mSendCommandTimeOut);
			//if(last Packet)
			if (data.getType().equals(NevoRawData.TYPE))
			{
				final NevoRawData nevoData = (NevoRawData) data;
                mPacketsbuffer.add(nevoData);

				if((byte)0xFF == nevoData.getRawData()[0])
				{
					QueuedMainThreadHandler.getInstance().next();

					NevoPacket packet = new NevoPacket(mPacketsbuffer);
                    //if packets invaild, discard them, and reset buffer
                    if(!packet.isVaildPackets())
                    {
                        Log.e("Nevo Error","InVaild Packets Received!");
                        mPacketsbuffer.clear();
                        //disconnect and auto reconnect
                        mNevoBT.disconnect(new Optional<String>(mNevoBT.getSaveAddress()));
                        return;
                    }
					mOnSyncControllerListener.packetReceived(packet);

                    if((byte)SetRtcNevoRequest.HEADER == nevoData.getRawData()[1])
                    {
                        //setp2:start set user profile
                        sendRequest(new SetProfileNevoRequest());
                    }
                    else if((byte) SetProfileNevoRequest.HEADER == nevoData.getRawData()[1])
                    {
                        //step3:WriteSetting
                        sendRequest(new WriteSettingNevoRequest());
                    }
                    else if((byte) WriteSettingNevoRequest.HEADER == nevoData.getRawData()[1])
                    {
                        //step4:SetCardio
                        sendRequest(new SetCardioNevoRequest());
                    }

                    else if((byte) SetCardioNevoRequest.HEADER == nevoData.getRawData()[1])
                    {
                        //start sync notification, phone --> nevo
                        //TODO: set Local Notification setting to Nevo, when nevo 's battery removed, the
                        // Steps count is 0, and all notification is off, because Notification is very
                        // important for user, so here need use local's setting sync with nevo
                        sendRequest(new SetNotificationNevoRequest());
                    }
                    else if((byte) SetNotificationNevoRequest.HEADER == nevoData.getRawData()[1])
                    {
                    /*
                       //start sync Goal, nevo --> phone (nevo 's led light on is based on Nevo's goal)
                        syncStepandGoal();
                        //syncActivityData();
                    }
                    else if((byte) GetStepsGoalNevoRequest.HEADER == nevoData.getRawData()[1])
                    {
                    */
                        //start sync data, nevo-->phone
                        syncActivityData();
                    }
                    else if((byte) ReadDailyTrackerInfoNevoRequest.HEADER == nevoData.getRawData()[1])
                    {
                        DailyTrackerInfoNevoPacket infopacket = packet.newDailyTrackerInfoNevoPacket();
                        mCurrentDay = 0;
                        mSavedDailyHistory = infopacket.getDailyTrackerInfo();
                        Log.i("","History Total Days:" + mSavedDailyHistory.size() + ",Today is:" + new Date() );

                        getDailyTracker(mCurrentDay);
                    }
                    else if((byte) ReadDailyTrackerNevoRequest.HEADER == nevoData.getRawData()[1])
                    {
                        DailyTrackerNevoPacket thispacket = packet.newDailyTrackerNevoPacket();
                        mSavedDailyHistory.get(mCurrentDay).setTotalSteps(thispacket.getDailySteps());
                        mSavedDailyHistory.get(mCurrentDay).setHourlySteps(thispacket.getHourlySteps());


                        Log.i(mSavedDailyHistory.get(mCurrentDay).getDate().toString(), "Daily Steps:" + mSavedDailyHistory.get(mCurrentDay).getTotalSteps());
                        Log.i(mSavedDailyHistory.get(mCurrentDay).getDate().toString(), "Hourly Steps:" + mSavedDailyHistory.get(mCurrentDay).getHourlySteps().toString());

                        GoogleFitManager.getInstance().saveDailyHistory(mSavedDailyHistory.get(mCurrentDay));

                        mCurrentDay++;
                        if(mCurrentDay < mSavedDailyHistory.size())
                        {
                            getDailyTracker(mCurrentDay);
                        }
                        else
                        {
                            mCurrentDay = 0;
                            syncFinished();
                            mIsSendRequestLocked = false;
                        }
                    }

                    mPacketsbuffer.clear();
                }
			}

		}
	};

	private OnConnectListener mConnectListener = new OnConnectListener() {

		@Override
		public void onConnect(String peripheralAdress) {
            mTimeOutcount = 0;
			mOnSyncControllerListener.connectionStateChanged(true);
            mIsSendRequestLocked = true;
            mPacketsbuffer.clear();
            //step1:setRTC, should defer about 4s for waiting the Callback characteristic enable Notify
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    setRtc();
                }
            },4000);

		}		
	};
	private OnDisconnectListener mDisconnectListener = new OnDisconnectListener() {
		
		@Override
		public void onDisconnect(String peripheralAdress) {	
			mOnSyncControllerListener.connectionStateChanged(false);
			}
	};
	

	private OnExceptionListener mExceptionListener = new OnExceptionListener() {
		
		@Override
		public void onException(Exception e) {
			/*
			 * Standard exception callback
			 */
			if(e instanceof BLEUnstableException) {
				
			} else if (e instanceof BluetoothDisabledException) {
				
			} else if (e instanceof BLENotSupportedException) {
			
			}else if (e instanceof BLEUnstableException) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), R.string.ble_unstable, Toast.LENGTH_LONG).show();
                    }
                });
            }else if (e instanceof BLEConnectTimeoutException) {
                mTimeOutcount = mTimeOutcount + 1;
                //when reconnect is more than 3, popup message to user to reopen bluetooth or restart smartphone
                if (mTimeOutcount  == 3) {
                    mTimeOutcount = 0;
                    showMessage(R.string.ble_timeout_title,R.string.ble_connecttimeout);
                }
            }
            mOnSyncControllerListener.connectionStateChanged(false);
		}
	};

    private void setRtc() {
        sendRequest(new SetRtcNevoRequest());
    }

    public void sendRequest(final SensorRequest request)
    {
        if(mIsSendRequestLocked &&
                (request instanceof GetStepsGoalNevoRequest
                 || request instanceof SetGoalNevoRequest
                 || request instanceof SetAlarmNevoRequest))
        {
            Log.w("SyncControllerImpl", request.getClass().getName() + " cancel sent by lock");
            return;
        }
        QueuedMainThreadHandler.getInstance().post(new Runnable(){
            @Override
            public void run() {
                mUiThread.removeCallbacks(mSendCommandTimeOut);
                mUiThread.postDelayed(mSendCommandTimeOut,MAX_TIMEOUT);
                Log.i("SyncControllerImpl",request.getClass().getName());
                mCurrentrequest = request;
                mNevoBT.sendRequest(request);
            }
        });

    }

    void  getDailyTrackerInfo()
    {
        sendRequest(new ReadDailyTrackerInfoNevoRequest());
    }

    void  getDailyTracker(int trackerno)
    {
        sendRequest(new ReadDailyTrackerNevoRequest(trackerno));
    }

    void syncStepandGoal() {
        final SensorRequest request = new GetStepsGoalNevoRequest();
        QueuedMainThreadHandler.getInstance().post(new Runnable() {
            @Override
            public void run() {
                mUiThread.removeCallbacks(mSendCommandTimeOut);
                mUiThread.postDelayed(mSendCommandTimeOut, MAX_TIMEOUT);
                Log.i("SyncControllerImpl", request.getClass().getName());
                mNevoBT.sendRequest(request);
            }
        });
    }


    /**
     This function will syncrhonise activity data with the watch.
     It is a long process and hence shouldn't be done too often, so we save the date of previous sync.
     The watch should be emptied after all data have been saved.
     */
    void syncActivityData() {

        long lastSync = mContext.getSharedPreferences(Constants.PREF_NAME, 0).getLong(Constants.LAST_SYNC, 0);
        String lasttimezone = mContext.getSharedPreferences(Constants.PREF_NAME, 0).getString(Constants.LAST_SYNC_TIME_ZONE, "");
        if(Calendar.getInstance().getTimeInMillis()-lastSync > SYNC_INTERVAL
           || !TimeZone.getDefault().getID().equals(lasttimezone)     ) {
            //We haven't synched for a while, let's sync now !
            Log.i("SyncControllerImpl","*** Sync started ! ***");
            getDailyTrackerInfo();
        }
        else
        {
            mIsSendRequestLocked = false;
        }
    }

    /**
     When the sync process is finished, le't refresh the date of sync
     */
    void syncFinished() {
        Log.i("SyncControllerImpl","*** Sync finished ***");
        mContext.getSharedPreferences(Constants.PREF_NAME, 0).edit().putLong(Constants.LAST_SYNC, Calendar.getInstance().getTimeInMillis()).commit();
        mContext.getSharedPreferences(Constants.PREF_NAME, 0).edit().putString(Constants.LAST_SYNC_TIME_ZONE, TimeZone.getDefault().getID()).commit();
    }
    /*package*/SyncControllerImpl(Context context)
	{
		mContext = context;	
		
		mNevoBT = NevoBT.Factory.newInstance(context);
		mNevoBT.connectCallback(mConnectListener);
		mNevoBT.addCallback(mDataReceivedListener);
		mNevoBT.disconnectCallback(mDisconnectListener);
		mNevoBT.exceptionCallback(mExceptionListener);

        Intent intent = new Intent(mContext,LocalService.class);
        mContext.bindService(intent,mCurrentServiceConnection, Activity.BIND_AUTO_CREATE);
	}

	/*package*/void setContext(Context context) {
        if(context!=null)
		    mContext = context;
	}

	@Override
	public Context getContext() {		
		return mContext;
	}

	@Override
	public void startConnect(boolean forceScan,
			OnSyncControllerListener listenser) {

        setSyncControllerListenser(listenser);
		
		if (forceScan)
		{
			mNevoBT.forgetSavedAddress();
		}
		
		List<SupportedService> Servicelist = new ArrayList<SupportedService>();
		Servicelist.add( SupportedService.nevo);
		try {
			mNevoBT.connect(Servicelist);
		} catch (BLENotSupportedException e) {
			Log.d("SyncControllerImpl", "Ble not supported !");
			e.printStackTrace();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(), R.string.ble_not_supported, Toast.LENGTH_LONG).show();
                }
            });
		} catch (BluetoothDisabledException e) {
			e.printStackTrace();
            Log.d("SyncControllerImpl", "Bluetooth Off!, please open bluetooth.");
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(), R.string.ble_deactivated, Toast.LENGTH_LONG).show();
                }
            });
		}
		
	}

	@Override
	public void setGoal(Goal goal) {
		sendRequest(new SetGoalNevoRequest(goal));
	}

    @Override
	public void setAlarm(int hour, int minute, boolean enable) {
		sendRequest(new SetAlarmNevoRequest(hour,minute,enable));	
	}

    @Override
    public void getStepsAndGoal() {
        sendRequest(new GetStepsGoalNevoRequest());
    }


    @Override
	public boolean isConnected() {
		
		return !mNevoBT.isDisconnected();
	}

    @Override
    public void showMessage(final int titleID, final int msgID) {
        if(mLocalService!=null && getVisible()) mLocalService.PopupMessage(titleID,msgID);
    }

    @Override
    public void setSyncControllerListenser(OnSyncControllerListener syncControllerListenser) {
        mOnSyncControllerListener = syncControllerListenser;
    }

    @Override
    public String getFirmwareVersion() {
        return mNevoBT.getFirmwareVersion();
    }

    @Override
    public String getSoftwareVersion() {
        return mNevoBT.getSoftwareVersion();
    }

    @Override
    public void setVisible(boolean isVisible) {
        mVisible = isVisible;
    }

    @Override
    public boolean getVisible() {
        return mVisible;
    }

    //below code added to popup a dialog whenever nevo app runs background or foreground
    private LocalService.LocalBinder mLocalService = null;
    private ServiceConnection mCurrentServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.v(TAG, name+" Service disconnected");
            mLocalService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.v(TAG, name+" Service connected");
            mLocalService = (LocalService.LocalBinder)service;
        }
    };

    /*inner class , static type, @link:http://stackoverflow.com/questions/10305261/broadcastreceiver-cant-instantiate-class-no-empty-constructor */
    static public class LocalService extends Service
    {
        private AlertDialog mAlertDialog = null;

        @Override
        public IBinder onBind(Intent intent) {
            return new LocalBinder();
        }

        @Override
        public boolean onUnbind(Intent intent) {
            return super.onUnbind(intent);
        }

        private void PopupMessage(final int titleID, final int msgID)
        {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if(mAlertDialog !=null && mAlertDialog.isShowing())
                    {
                        //mAlertDialog.dismiss();
                        //mAlertDialog =null;
                        return;
                    }
                    AlertDialog.Builder ab = new AlertDialog.Builder(LocalService.this, AlertDialog.THEME_HOLO_LIGHT)
                            .setPositiveButton("Bluetooth", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent("android.intent.action.View");
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings"));
                                    LocalService.this.startActivity(intent);
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .setCancelable(false)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle(titleID)
                            .setMessage(msgID);

                    mAlertDialog = ab.create();
                    mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                    mAlertDialog.setCanceledOnTouchOutside(false);
                    mAlertDialog.setCancelable(false);
                    mAlertDialog.show();
                }
            });
        }

        public class LocalBinder extends Binder {

            public void PopupMessage(int titleID, int msgID)
            {
                LocalService.this.PopupMessage(titleID, msgID);
            }
        }
    }
}
