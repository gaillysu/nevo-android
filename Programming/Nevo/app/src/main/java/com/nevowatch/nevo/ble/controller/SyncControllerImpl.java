package com.nevowatch.nevo.ble.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;

import com.nevowatch.nevo.Model.DailyHistory;
import com.nevowatch.nevo.ble.ble.SupportedService;
import com.nevowatch.nevo.ble.kernel.BLENotSupportedException;
import com.nevowatch.nevo.ble.kernel.BLEUnstableException;
import com.nevowatch.nevo.ble.kernel.BluetoothDisabledException;
import com.nevowatch.nevo.ble.kernel.ImazeBT;
import com.nevowatch.nevo.ble.kernel.OnConnectListener;
import com.nevowatch.nevo.ble.kernel.OnDataReceivedListener;
import com.nevowatch.nevo.ble.kernel.OnDisconnectListener;
import com.nevowatch.nevo.ble.kernel.OnExceptionListener;
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
import com.nevowatch.nevo.ble.util.Constants;
import com.nevowatch.nevo.ble.util.QueuedMainThreadHandler;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class SyncControllerImpl implements SyncController{

	Context mContext;
	private static final int SYNC_INTERVAL = 0*60*60*1000; //every hour , do sync when connected again
	private ImazeBT  mImazeBT;
	private OnSyncControllerListener mOnSyncControllerListener;
    private ArrayList<NevoRawData> mPacketsbuffer = new ArrayList<NevoRawData>();

    private ArrayList<DailyHistory> mSavedDailyHistory = new ArrayList<DailyHistory>();
    private int mCurrentDay = 0;

	/**
	 * This listener is called when new data is received
	 */
	private OnDataReceivedListener mDataReceivedListener = new OnDataReceivedListener() {
		
		@Override
		public void onDataReceived(com.nevowatch.nevo.ble.model.packet.SensorData data) {
			
			//if(last Packet)
			if (data.getType().equals(NevoRawData.TYPE))
			{
				final NevoRawData nevoData = (NevoRawData) data;
                mPacketsbuffer.add(nevoData);

				if((byte)0xFF == nevoData.getRawData()[0])
				{
					QueuedMainThreadHandler.getInstance().next();
					
					NevoPacket packet = new NevoPacket(mPacketsbuffer);
					mOnSyncControllerListener.packetReceived(packet);

                    if((byte)SetRtcNevoRequest.HEADER == nevoData.getRawData()[1])
                    {
                        //setp2:start set user profile
                        sendRequest(new SetProfileNevoRequest());
                    }
                    if((byte) SetProfileNevoRequest.HEADER == nevoData.getRawData()[1])
                    {
                        //step3:WriteSetting
                        sendRequest(new WriteSettingNevoRequest());
                    }
                    if((byte) WriteSettingNevoRequest.HEADER == nevoData.getRawData()[1])
                    {
                        //step4:SetCardio
                        sendRequest(new SetCardioNevoRequest());
                    }
                    if((byte) SetCardioNevoRequest.HEADER == nevoData.getRawData()[1])
                    {
                        //start sync data
                        syncActivityData();
                    }
                    if((byte) ReadDailyTrackerInfoNevoRequest.HEADER == nevoData.getRawData()[1])
                    {
                        DailyTrackerInfoNevoPacket infopacket = packet.newDailyTrackerInfoNevoPacket();
                        mCurrentDay = 0;
                        mSavedDailyHistory.add(infopacket.getDailyTrackerInfo());
                        getDailyTracker(mCurrentDay);
                    }

                    if((byte) ReadDailyTrackerNevoRequest.HEADER == nevoData.getRawData()[1])
                    {
                        /*TODO by gailly save to google fit or local database*/
                        mCurrentDay++;
                        if(mCurrentDay < mSavedDailyHistory.size())
                        {
                            getDailyTracker(mCurrentDay);
                        }
                        else
                        {
                            mCurrentDay = 0;
                            syncFinished();
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
			mOnSyncControllerListener.connectionStateChanged(true);
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
			
			}			
			mOnSyncControllerListener.connectionStateChanged(false);
		}
	};

    private void setRtc() {
        sendRequest(new SetRtcNevoRequest());
    }

    private void sendRequest(final SensorRequest request)
    {
        QueuedMainThreadHandler.getInstance().post(new Runnable(){
            @Override
            public void run() {
                mImazeBT.sendRequest(request);
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

    /**
     This function will syncrhonise activity data with the watch.
     It is a long process and hence shouldn't be done too often, so we save the date of previous sync.
     The watch should be emptied after all data have been saved.
     */
    void syncActivityData() {

        long lastSync = mContext.getSharedPreferences(Constants.PREF_NAME, 0).getLong(Constants.LAST_SYNC, 0);

        if(Calendar.getInstance().getTimeInMillis()-lastSync > SYNC_INTERVAL) {
            //We haven't synched for a while, let's sync now !
            Log.i("SyncControllerImpl","*** Sync started ! ***");
            getDailyTrackerInfo();
        }

    }

    /**
     When the sync process is finished, le't refresh the date of sync
     */
    void syncFinished() {
        Log.i("SyncControllerImpl","*** Sync finished ***");
        mContext.getSharedPreferences(Constants.PREF_NAME, 0).edit().putLong(Constants.LAST_SYNC, Calendar.getInstance().getTimeInMillis()).commit();
    }
		
	public SyncControllerImpl(Context context)
	{
		mContext = context;	
		
		mImazeBT = ImazeBT.Factory.newInstance(context);				
		mImazeBT.connectCallback(mConnectListener);
		mImazeBT.addCallback(mDataReceivedListener);
		mImazeBT.disconnectCallback(mDisconnectListener);
		mImazeBT.exceptionCallback(mExceptionListener);				
	}

	/*package*/void setContext(Context context) {
		mContext = context;		
	}

	@Override
	public Context getContext() {		
		return mContext;
	}

	@Override
	public void startConnect(boolean forceScan,
			OnSyncControllerListener listenser) {
		
		mOnSyncControllerListener = listenser;
		
		if (forceScan)
		{
			mImazeBT.forgetSavedAddress();
		}
		
		List<SupportedService> Servicelist = new ArrayList<SupportedService>();
		Servicelist.add( SupportedService.nevo);
		try {
			mImazeBT.connect(Servicelist);
		} catch (BLENotSupportedException e) {
			Log.d("SyncControllerImpl", "Ble not supported !");
			e.printStackTrace();
		} catch (BluetoothDisabledException e) {
			e.printStackTrace();
            Log.d("SyncControllerImpl", "Ble not supported !");
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
		
		return !mImazeBT.isDisconnected();
	}

	
}
