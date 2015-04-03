package com.nevowatch.nevo.ble.controller;

import com.nevowatch.nevo.ble.model.request.Goal;

import android.content.Context;

/**
 * this class define some functions for communication with Nevo,
 * all UI activity should use this interface,
 * Usage in one Activity, eg: setGoalActivity
 * 
 * @author Gaillysu
 *  SyncController  sync = SyncController.Factory.newInstance(setGoalActivity.this)
 *  setGoalActivity should implement OnSyncControllerListener
 */
public interface SyncController {

	public  class Factory{
		private static  SyncControllerImpl sInstance = null;
		public static SyncControllerImpl newInstance(Context context) {
			if(null == sInstance )
			{
				sInstance = new SyncControllerImpl(context);
			} else {
				sInstance.setContext(context);
			}
			return sInstance;
		}
	}
	
	void setContext(Context context);
	public Context getContext();	
	void startConnect(boolean forceScan,OnSyncControllerListener listenser);
	
	public boolean isConnected();
	public void setGoal(Goal goal);
    public void setRtc();
	public void setAlarm(int hour,int minute,boolean enable);
	
}
