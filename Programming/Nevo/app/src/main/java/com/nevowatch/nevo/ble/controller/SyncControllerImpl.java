package com.nevowatch.nevo.ble.controller;

import java.util.ArrayList;
import java.util.List;

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
import com.nevowatch.nevo.ble.model.packet.NevoRawData;
import com.nevowatch.nevo.ble.model.request.Goal;
import com.nevowatch.nevo.ble.model.request.SensorRequest;
import com.nevowatch.nevo.ble.model.request.SetAlarmNevoRequest;
import com.nevowatch.nevo.ble.model.request.SetGoalNevoRequest;
import com.nevowatch.nevo.ble.model.request.SetRtcNevoRequest;
import com.nevowatch.nevo.ble.util.QueuedMainThreadHandler;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;

public class SyncControllerImpl implements SyncController{

	Context mContext;
	
	private ImazeBT  mImazeBT;
	private OnSyncControllerListener mOnSyncControllerListener;
	
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
				if((byte)0xFF == nevoData.getRawData()[0])
				{
					QueuedMainThreadHandler.getInstance().next();
					
					NevoPacket packet = new NevoPacket();
					mOnSyncControllerListener.packetReceived(packet);
				}
			}
			
		}
	};
	
	private OnConnectListener mConnectListener = new OnConnectListener() {

		@Override
		public void onConnect(String peripheralAdress) {
			mOnSyncControllerListener.connectionStateChanged(true);

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
		
	public SyncControllerImpl(Context context)
	{
		mContext = context;	
		
		mImazeBT = ImazeBT.Factory.newInstance(context);				
		mImazeBT.connectCallback(mConnectListener);
		mImazeBT.addCallback(mDataReceivedListener);
		mImazeBT.disconnectCallback(mDisconnectListener);
		mImazeBT.exceptionCallback(mExceptionListener);				
	}

	@Override
	public void setContext(Context context) {
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BluetoothDisabledException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
	@Override
	public void setGoal(Goal goal) {
		sendRequest(new SetGoalNevoRequest(goal));
	}

    @Override
    public void setRtc() {
        sendRequest(new SetRtcNevoRequest());
    }

    @Override
	public void setAlarm(int hour, int minute, boolean enable) {
		sendRequest(new SetAlarmNevoRequest(hour,minute,enable));	
	}


	@Override
	public boolean isConnected() {
		
		return !mImazeBT.isDisconnected();
	}
	
}
