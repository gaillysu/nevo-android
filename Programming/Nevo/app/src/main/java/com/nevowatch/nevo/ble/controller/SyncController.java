package com.nevowatch.nevo.ble.controller;

import com.nevowatch.nevo.Model.Goal;

import android.content.Context;

/**
 * this class define some functions for communication with Nevo,
 * all UI activity or Fragment should use this interface,
 * Usage in  Activity or Fragment
 *
 * SyncController  sync = SyncController.Factory.getInstance(this)
 *  "this"  object should implement OnSyncControllerListener, mostly "this" object is a activity, it also is
 *  a Fragment. such as  GoalFragment
 *
 *  step1: sync.startConnect(true,this); // step1 should be called when the app start or user press "connect"
 *                                       //only step1 connected, can do step2, otherwise, step2 will return without sending request.
 *  step2: sync.setGoal(new NumberOfStepsGoal(10000))
 *
 *
 * @author Gaillysu
 *
 * /!\/!\/!\Backbone Class : Modify with care/!\/!\/!\
 *
 */
public interface SyncController {

	public  class Singleton {
		private static  SyncControllerImpl sInstance = null;
		public static SyncController getInstance(Context context) {
			if(null == sInstance )
			{
				sInstance = new SyncControllerImpl(context);
			} else {
				sInstance.setContext(context);
			}
			return sInstance;
		}
	}
	
    /*
    return SyncController 's Context , mostly it is a activity, such as MainActivity or OTA activity
     */
	public Context getContext();

    /*
    start Connect Nevo
    input:forceScan, if true,do scanning before connect (will forget old UDID, and save new UUID when connected)
    if false, connect the saved UDID 's Nevo
     */
	void startConnect(boolean forceScan,OnSyncControllerListener listenser);

    /*
    return Nevo connect true or false
     */
	public boolean isConnected();

    /*
    set Steps Goal
    inputL goal =  new NumberOfStepsGoal(XXXX)
     */
	public void setGoal(Goal goal);
    /*
    input hour:0~23
          minute:0~59
          enable : true or false
     */
	public void setAlarm(int hour,int minute,boolean enable);
    /*
      return Nevo 's current daily step count and step Goal, refresh mainhome's Clock screen.
     */
    public void getStepsAndGoal();

    public void showMessage(int titleID, int msgID);

    public void setSyncControllerListenser(OnSyncControllerListener syncControllerListenser);

    public String getFirmwareVersion();
    public String getSoftwareVersion();

    /**
     * when mainActivity goes to background, set true, otherwise set false
     */
	public void setVisible(boolean isVisible);

    /**
     * forget saved Nevo device
     */
    public void forgetDevice();

}
