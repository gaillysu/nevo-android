package com.medcorp.nevo.ble.controller;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;
import com.medcorp.nevo.R;
import com.medcorp.nevo.ble.datasource.NotificationDataHelper;
import com.medcorp.nevo.ble.exception.BLEConnectTimeoutException;
import com.medcorp.nevo.ble.exception.BLENotSupportedException;
import com.medcorp.nevo.ble.exception.BLEUnstableException;
import com.medcorp.nevo.ble.exception.BluetoothDisabledException;
import com.medcorp.nevo.ble.exception.NevoException;
import com.medcorp.nevo.ble.exception.QuickBTSendTimeoutException;
import com.medcorp.nevo.ble.exception.QuickBTUnBindNevoException;
import com.medcorp.nevo.ble.exception.visitor.NevoExceptionVisitor;
import com.medcorp.nevo.ble.listener.OnSyncControllerListener;
import com.medcorp.nevo.ble.model.notification.Notification;
import com.medcorp.nevo.ble.model.notification.CalendarNotification;
import com.medcorp.nevo.ble.model.notification.EmailNotification;
import com.medcorp.nevo.ble.model.notification.FacebookNotification;
import com.medcorp.nevo.ble.model.notification.SmsNotification;
import com.medcorp.nevo.ble.model.notification.TelephoneNotification;
import com.medcorp.nevo.ble.model.notification.WeChatNotification;
import com.medcorp.nevo.ble.model.notification.WhatsappNotification;
import com.medcorp.nevo.ble.model.notification.visitor.NotificationColorGetter;
import com.medcorp.nevo.ble.model.packet.DailyTrackerInfoNevoPacket;
import com.medcorp.nevo.ble.model.packet.DailyTrackerNevoPacket;
import com.medcorp.nevo.ble.model.packet.NevoPacket;
import com.medcorp.nevo.ble.model.packet.NevoRawData;
import com.medcorp.nevo.ble.model.packet.SensorData;
import com.medcorp.nevo.ble.model.request.GetBatteryLevelNevoRequest;
import com.medcorp.nevo.ble.model.request.GetStepsGoalNevoRequest;
import com.medcorp.nevo.ble.model.request.LedLightOnOffNevoRequest;
import com.medcorp.nevo.ble.model.request.ReadDailyTrackerInfoNevoRequest;
import com.medcorp.nevo.ble.model.request.ReadDailyTrackerNevoRequest;
import com.medcorp.nevo.ble.model.request.SensorRequest;
import com.medcorp.nevo.ble.model.request.SetAlarmNevoRequest;
import com.medcorp.nevo.ble.model.request.SetCardioNevoRequest;
import com.medcorp.nevo.ble.model.request.SetGoalNevoRequest;
import com.medcorp.nevo.ble.model.request.SetNotificationNevoRequest;
import com.medcorp.nevo.ble.model.request.SetProfileNevoRequest;
import com.medcorp.nevo.ble.model.request.SetRtcNevoRequest;
import com.medcorp.nevo.ble.model.request.TestModeNevoRequest;
import com.medcorp.nevo.ble.model.request.WriteSettingNevoRequest;
import com.medcorp.nevo.ble.util.Constants;
import com.medcorp.nevo.ble.util.Optional;
import com.medcorp.nevo.ble.util.QueuedMainThreadHandler;
import com.medcorp.nevo.database.DatabaseHelper;
import com.medcorp.nevo.database.IDailyHistory;
import com.medcorp.nevo.fragment.AlarmFragment;
import com.medcorp.nevo.model.Alarm;
import com.medcorp.nevo.model.DailyHistory;
import com.medcorp.nevo.model.Goal;
import com.medcorp.nevo.view.TimePickerView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/*package*/ class SyncControllerImpl implements SyncController, ConnectionController.Delegate, NevoExceptionVisitor<Void> {
    private final static String TAG = "SyncControllerImpl";

	private Context mContext;

	private static final int SYNC_INTERVAL = 1*30*60*1000; //every half hour , do sync when connected again

	private ConnectionController mConnectionController;

	private Optional<OnSyncControllerListener> mOnSyncControllerListener = new Optional<OnSyncControllerListener>();

    private List<NevoRawData> mPacketsbuffer = new ArrayList<NevoRawData>();

    private List<DailyHistory> mSavedDailyHistory = new ArrayList<DailyHistory>();
    private int mCurrentDay = 0;
    private int mTimeOutcount = 0;
    private long mLastPressAkey = 0;
    private boolean mEnableTestMode = false;
    private boolean mSyncAllFlag = false;
    //IMPORT!!!!, every get connected, will do sync profile data and activity data with Nevo
    //it perhaps long time(sync activity data perhaps need long time, MAX total 7 days)
    //so before sync finished, disable setGoal/setAlarm/getGoalSteps
    //make sure  the whole received packets

    public SyncControllerImpl(Context context)
    {
        mContext = context;

        mConnectionController = ConnectionController.Singleton.getInstance(context);

        mConnectionController.setDelegate(this);

        Intent intent = new Intent(mContext,LocalService.class);
        mContext.getApplicationContext().bindService(intent,mCurrentServiceConnection, Activity.BIND_AUTO_CREATE);
    }

    /*package*/void setContext(Context context) {
        if(context!=null)
            mContext = context;
    }


    @Override
    public void startConnect(boolean forceScan,
                             OnSyncControllerListener listenser) {

        setSyncControllerListenser(listenser);

        if (forceScan)
        {
            mConnectionController.forgetSavedAddress();
        }

        mConnectionController.connect();

    }

    //Each packets should go through this function. It will ensure that they are properly queued and sent in order.
    @Override
    public void sendRequest(final SensorRequest request)
    {
        if(mConnectionController.getOTAMode()) {
            return;
        }
        QueuedMainThreadHandler.getInstance(QueuedMainThreadHandler.QueueType.SyncController).post(new Runnable(){
            @Override
            public void run() {

                Log.i(TAG,request.getClass().getName());

                mConnectionController.sendRequest(request);
            }
        });

    }

	/**
	 * This listener is called when new data is received
	 */
    @Override
    public void onDataReceived(SensorData data) {

			if (data.getType().equals(NevoRawData.TYPE))
			{
				final NevoRawData nevoData = (NevoRawData) data;
                mPacketsbuffer.add(nevoData);

				if((byte)0xFF == nevoData.getRawData()[0])
				{
					QueuedMainThreadHandler.getInstance(QueuedMainThreadHandler.QueueType.SyncController).next();

					NevoPacket packet = new NevoPacket(mPacketsbuffer);
                    //if packets invaild, discard them, and reset buffer
                    if(!packet.isVaildPackets())
                    {
                        Log.e("Nevo Error","InVaild Packets Received!");
                        mPacketsbuffer.clear();

                        return;
                    }
					if(mOnSyncControllerListener.notEmpty()) mOnSyncControllerListener.get().packetReceived(packet);

                    if((byte)SetRtcNevoRequest.HEADER == nevoData.getRawData()[1])
                    {
                        //setp2:start set user profile
                        sendRequest(new SetProfileNevoRequest(mContext));
                    }
                    else if((byte) SetProfileNevoRequest.HEADER == nevoData.getRawData()[1])
                    {
                        //step3:WriteSetting
                        sendRequest(new WriteSettingNevoRequest(mContext));
                    }
                    else if((byte) WriteSettingNevoRequest.HEADER == nevoData.getRawData()[1])
                    {
                        //step4:SetCardio
                        sendRequest(new SetCardioNevoRequest(mContext));
                    }

                    else if((byte) SetCardioNevoRequest.HEADER == nevoData.getRawData()[1])
                    {
                        //start sync notification, phone --> nevo
                        // set Local Notification setting to Nevo, when nevo 's battery removed, the
                        // Steps count is 0, and all notification is off, because Notification is very
                        // important for user, so here need use local's setting sync with nevo

                        Map<Notification, Integer> applicationNotificationColorMap = new HashMap<Notification, Integer>();
                        NotificationColorGetter getter = new NotificationColorGetter(mContext);
                        NotificationDataHelper dataHelper = new NotificationDataHelper(mContext);
                        Notification applicationNotification = new TelephoneNotification();
                        applicationNotificationColorMap.put(dataHelper.getState(applicationNotification), applicationNotification.accept(getter).getColor());
                        applicationNotification = new SmsNotification();
                        applicationNotificationColorMap.put(dataHelper.getState(applicationNotification), applicationNotification.accept(getter).getColor());
                        applicationNotification = new EmailNotification();
                        applicationNotificationColorMap.put(dataHelper.getState(applicationNotification), applicationNotification.accept(getter).getColor());
                        applicationNotification = new FacebookNotification();
                        applicationNotificationColorMap.put(dataHelper.getState(applicationNotification), applicationNotification.accept(getter).getColor());
                        applicationNotification = new CalendarNotification();
                        applicationNotificationColorMap.put(dataHelper.getState(applicationNotification), applicationNotification.accept(getter).getColor());
                        applicationNotification = new WeChatNotification();
                        applicationNotificationColorMap.put(dataHelper.getState(applicationNotification), applicationNotification.accept(getter).getColor());
                        applicationNotification = new WhatsappNotification();
                        applicationNotificationColorMap.put(dataHelper.getState(applicationNotification), applicationNotification.accept(getter).getColor());

                        sendRequest(new SetNotificationNevoRequest(mContext,applicationNotificationColorMap));
                    }
                    else if((byte) SetNotificationNevoRequest.HEADER == nevoData.getRawData()[1])
                    {
                        List<Alarm> list = new ArrayList<Alarm>();

                        String[] strAlarm = TimePickerView.getAlarmFromPreference(0,mContext).split(":");
                        Boolean onOff = AlarmFragment.getClockStateFromPreference(0, mContext);
                        list.add(new Alarm(0,Integer.parseInt(strAlarm[0]),Integer.parseInt(strAlarm[1]),onOff));

                        strAlarm = TimePickerView.getAlarmFromPreference(1,mContext).split(":");
                        onOff = AlarmFragment.getClockStateFromPreference(1,mContext);
                        list.add(new Alarm(1,Integer.parseInt(strAlarm[0]),Integer.parseInt(strAlarm[1]),onOff));

                        strAlarm = TimePickerView.getAlarmFromPreference(2,mContext).split(":");
                        onOff = AlarmFragment.getClockStateFromPreference(2,mContext);
                        list.add(new Alarm(2,Integer.parseInt(strAlarm[0]),Integer.parseInt(strAlarm[1]),onOff));
                        setAlarm(list);
                    }
                    else if((byte) SetAlarmNevoRequest.HEADER == nevoData.getRawData()[1])
                    {
                        //start sync data, nevo-->phone
                        syncActivityData();
                    }
                    else if((byte) ReadDailyTrackerInfoNevoRequest.HEADER == nevoData.getRawData()[1])
                    {
                        DailyTrackerInfoNevoPacket infopacket = packet.newDailyTrackerInfoNevoPacket();
                        mCurrentDay = 0;
                        mSavedDailyHistory = infopacket.getDailyTrackerInfo();
                        Log.i("","History Total Days:" + mSavedDailyHistory.size() + ",Today is:" + new Date() );
                        if(!mSavedDailyHistory.isEmpty()) {
                            mSyncAllFlag = true;
                            getDailyTracker(mCurrentDay);
                        }
                    }
                    else if((byte) ReadDailyTrackerNevoRequest.HEADER == nevoData.getRawData()[1])
                    {
                        DailyTrackerNevoPacket thispacket = packet.newDailyTrackerNevoPacket();

                        if(mSavedDailyHistory.isEmpty()) {
                            mCurrentDay = 0;
                            mSavedDailyHistory.add(mCurrentDay, new DailyHistory(thispacket.getDate()));
                        }
                        mSavedDailyHistory.get(mCurrentDay).setTotalSteps(thispacket.getDailySteps());
                        mSavedDailyHistory.get(mCurrentDay).setHourlySteps(thispacket.getHourlySteps());
                        Log.i(mSavedDailyHistory.get(mCurrentDay).getDate().toString(), "Daily Steps:" + mSavedDailyHistory.get(mCurrentDay).getTotalSteps());
                        Log.i(mSavedDailyHistory.get(mCurrentDay).getDate().toString(), "Hourly Steps:" + mSavedDailyHistory.get(mCurrentDay).getHourlySteps().toString());

                        mSavedDailyHistory.get(mCurrentDay).setTotalSleepTime(thispacket.getTotalSleepTime());
                        mSavedDailyHistory.get(mCurrentDay).setHourlySleepTime(thispacket.getHourlySleepTime());

                        Log.i(mSavedDailyHistory.get(mCurrentDay).getDate().toString(), "Daily Sleep time:" + mSavedDailyHistory.get(mCurrentDay).getTotalSleepTime());
                        Log.i(mSavedDailyHistory.get(mCurrentDay).getDate().toString(), "Hourly Sleep time:" + mSavedDailyHistory.get(mCurrentDay).getHourlySleepTime().toString());

                        mSavedDailyHistory.get(mCurrentDay).setTotalWakeTime(thispacket.getTotalWakeTime());
                        mSavedDailyHistory.get(mCurrentDay).setHourlyWakeTime(thispacket.getHourlyWakeTime());

                        Log.i(mSavedDailyHistory.get(mCurrentDay).getDate().toString(), "Daily Wake time:" + mSavedDailyHistory.get(mCurrentDay).getTotalWakeTime());
                        Log.i(mSavedDailyHistory.get(mCurrentDay).getDate().toString(), "Hourly Wake time:" + mSavedDailyHistory.get(mCurrentDay).getHourlyWakeTime().toString());

                        mSavedDailyHistory.get(mCurrentDay).setTotalLightTime(thispacket.getTotalLightTime());
                        mSavedDailyHistory.get(mCurrentDay).setHourlyLightTime(thispacket.getHourlyLightTime());

                        Log.i(mSavedDailyHistory.get(mCurrentDay).getDate().toString(), "Daily light time:" + mSavedDailyHistory.get(mCurrentDay).getTotalLightTime());
                        Log.i(mSavedDailyHistory.get(mCurrentDay).getDate().toString(), "Hourly light time:" + mSavedDailyHistory.get(mCurrentDay).getHourlyLightTime().toString());


                        mSavedDailyHistory.get(mCurrentDay).setTotalDeepTime(thispacket.getTotalDeepTime());
                        mSavedDailyHistory.get(mCurrentDay).setHourlDeepTime(thispacket.getHourlDeepTime());

                        Log.i(mSavedDailyHistory.get(mCurrentDay).getDate().toString(), "Daily deep time:" + mSavedDailyHistory.get(mCurrentDay).getTotalDeepTime());
                        Log.i(mSavedDailyHistory.get(mCurrentDay).getDate().toString(), "Hourly deep time:" + mSavedDailyHistory.get(mCurrentDay).getHourlDeepTime().toString());

                        mSavedDailyHistory.get(mCurrentDay).setTotalDist(thispacket.getTotalDist());
                        mSavedDailyHistory.get(mCurrentDay).setHourlyDist(thispacket.getHourlyDist());
                        mSavedDailyHistory.get(mCurrentDay).setTotalCalories(thispacket.getTotalCalories());
                        mSavedDailyHistory.get(mCurrentDay).setHourlyCalories(thispacket.getHourlyCalories());

                        Log.i(mSavedDailyHistory.get(mCurrentDay).getDate().toString(), "Daily Total Disc (m):" + mSavedDailyHistory.get(mCurrentDay).getTotalDist());
                        Log.i(mSavedDailyHistory.get(mCurrentDay).getDate().toString(), "Hourly Disc (m):" + mSavedDailyHistory.get(mCurrentDay).getHourlyDist().toString());
                        Log.i(mSavedDailyHistory.get(mCurrentDay).getDate().toString(), "Daily Total Calories (kcal):" + mSavedDailyHistory.get(mCurrentDay).getTotalCalories());
                        Log.i(mSavedDailyHistory.get(mCurrentDay).getDate().toString(), "Hourly Calories (kcal):" + mSavedDailyHistory.get(mCurrentDay).getHourlyCalories().toString());

                        //save it to local database
                        try {
                                IDailyHistory history = new IDailyHistory(mSavedDailyHistory.get(mCurrentDay));
                                DatabaseHelper.getInstance(mContext).SaveDailyHistory(history);
                                Log.i(TAG,mSavedDailyHistory.get(mCurrentDay).getDate().toString() + " successfully saved to database, created = " + history.getCreated());
                        } catch (SQLException e) {
                                e.printStackTrace();
                                Log.i(TAG,mSavedDailyHistory.get(mCurrentDay).getDate().toString() + " Failure saved to database, "+e.toString());
                        }

                        //discard tutorial activity, only MainActivity can save data to Google git
//                        if(mContext instanceof MainActivity) GoogleFitManager.getInstance(mContext,(Activity)mContext).saveDailyHistory(mSavedDailyHistory.get(mCurrentDay));
                        mCurrentDay++;
                        if(mCurrentDay < mSavedDailyHistory.size() && mSyncAllFlag)
                        {
                            getDailyTracker(mCurrentDay);
                        }
                        else
                        {
                            mSyncAllFlag = false;
                            mCurrentDay = 0;
                            //DatabaseHelper.outPutDatabase(mContext);
                            syncFinished();
                        }
                    }
                    //press B key once--- down and up within 500ms
                    else if((byte) 0xF1 == nevoData.getRawData()[1] && (byte) 0x02 == packet.getPackets().get(0).getRawData()[2])
                    {
                       long currentTime = System.currentTimeMillis();
                       //remove repeat press B key down within 6s
                       if(currentTime - mLastPressAkey > 6000)
                       {
                           mLastPressAkey = currentTime;
                       }
                    }
                    else if((byte) 0xF1 == nevoData.getRawData()[1] && (byte) 0x00 == packet.getPackets().get(0).getRawData()[2])
                    {
                        long currentTime = System.currentTimeMillis();
                        if(currentTime - mLastPressAkey < 500)
                        {
                            if(mLocalService!=null) {
                                mLocalService.findCellPhone();
                            }
                            //let all color LED light on, means that find CellPhone is successful.
                            sendRequest(new TestModeNevoRequest(mContext,0x3F0000,false));
                        }
                    }
                    else if((byte) GetStepsGoalNevoRequest.HEADER == nevoData.getRawData()[1])
                    {
                        if (!mEnableTestMode)
                        {
                            mEnableTestMode = true;
                            sendRequest(new TestModeNevoRequest(mContext,0,false));
                        }
                    }

                    mPacketsbuffer.clear();
                }
			}

	}

    @Override
    public void onConnectionStateChanged(boolean isConnected, String address) {

        if(isConnected) {
            mEnableTestMode = false;
            mTimeOutcount = 0;
            if(mOnSyncControllerListener.notEmpty()) mOnSyncControllerListener.get().connectionStateChanged(true);
            //step1:setRTC, should defer about 4s for waiting the Callback characteristic enable Notify
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    mPacketsbuffer.clear();
                    setRtc();
                }
            }, 4000);

        } else {
            if(mOnSyncControllerListener.notEmpty()) mOnSyncControllerListener.get().connectionStateChanged(false);
            QueuedMainThreadHandler.getInstance(QueuedMainThreadHandler.QueueType.SyncController).clear();
            mPacketsbuffer.clear();
        }

	}

    /**
     This function will synchronise activity data with the watch.
     It is a long process and hence shouldn't be done too often, so we save the date of previous sync.
     The watch should be emptied after all data have been saved.
     */
    private void syncActivityData() {

        long lastSync = mContext.getSharedPreferences(Constants.PREF_NAME, 0).getLong(Constants.LAST_SYNC, 0);
        String lasttimezone = mContext.getSharedPreferences(Constants.PREF_NAME, 0).getString(Constants.LAST_SYNC_TIME_ZONE, "");
        if(Calendar.getInstance().getTimeInMillis()-lastSync > SYNC_INTERVAL
                || !TimeZone.getDefault().getID().equals(lasttimezone)     ) {
            //We haven't synched for a while, let's sync now !
            Log.i(TAG,"*** Sync started ! ***");
            getDailyTrackerInfo(true);
        }
        else
        {
            //here sync StepandGoal for good user experience
            Log.i(TAG,"*** Sync step count and goal ***");
            getStepsAndGoal();
        }
    }

    /**
     When the sync process is finished, le't refresh the date of sync
     */
    private void syncFinished() {
        Log.i(TAG,"*** Sync finished ***");
        mContext.getSharedPreferences(Constants.PREF_NAME, 0).edit().putLong(Constants.LAST_SYNC, Calendar.getInstance().getTimeInMillis()).commit();
        mContext.getSharedPreferences(Constants.PREF_NAME, 0).edit().putString(Constants.LAST_SYNC_TIME_ZONE, TimeZone.getDefault().getID()).commit();
        //tell history to refresh
    }

    private void setRtc() {
        sendRequest(new SetRtcNevoRequest(mContext));
    }

    @Override
    public void  getDailyTrackerInfo(boolean syncAll)
    {
        if(syncAll){
            sendRequest(new ReadDailyTrackerInfoNevoRequest(mContext));
        } else if(!mSyncAllFlag){
            getDailyTracker(0);
        }
    }

    private void  getDailyTracker(int trackerno)
    {
        sendRequest(new ReadDailyTrackerNevoRequest(mContext, trackerno));
    }

    @Override
    public void setGoal(Goal goal) {
        sendRequest(new SetGoalNevoRequest(mContext,goal));
    }

    @Override
    public void setAlarm(List<Alarm> list) {
        sendRequest(new SetAlarmNevoRequest(mContext,list));
    }

    @Override
    public void getStepsAndGoal() {
        sendRequest(new GetStepsGoalNevoRequest(mContext));
    }

    @Override
    public void getBatteryLevel()
    {
        sendRequest(new GetBatteryLevelNevoRequest(mContext));
    }

    /**
     @ledpattern, define Led light pattern, 0 means off all led, 0xFFFFFF means light on all led( include color and white)
     0x7FF means light on all white led (bit0~bit10), 0x3F0000 means light on all color led (bit16~bit21)
     other value, light on the related led
     @motorOnOff, vibrator true or flase
     */
    @Override
    public void findDevice()
    {
        sendRequest(new LedLightOnOffNevoRequest(mContext,0x3F0000,false));
    }

    @Override
	public Context getContext() {		
		return mContext;
	}

    @Override
    public void setSyncControllerListenser(OnSyncControllerListener syncControllerListenser) {
        mOnSyncControllerListener.set(syncControllerListenser);
    }

    @Override
    public boolean isConnected() {
        return mConnectionController.isConnected();
    }

    @Override
    public String getFirmwareVersion() {
        return mConnectionController.getFirmwareVersion();
    }

    @Override
    public String getSoftwareVersion() {
        return mConnectionController.getSoftwareVersion();
    }
    @Override
    public void forgetDevice() {
        mConnectionController.forgetSavedAddress();
        getContext().getSharedPreferences(Constants.PREF_NAME, 0).edit().putBoolean(Constants.FIRST_FLAG,true).commit();
    }

    @Override
    public void onException(NevoException e) {
            e.accept(this);
        if(mOnSyncControllerListener.notEmpty()){
            mOnSyncControllerListener.get().connectionStateChanged(false);
        }
    }


    /**
     * ============The following block is used only to have a safe context in order to display a popup============
     */

    @Override
    public void showMessage(final int titleID, final int msgID) {
        if(mLocalService!=null && mVisible){
            mLocalService.PopupMessage(titleID, msgID);
        }
    }

    private boolean mVisible = false;

    @Override
    public void setVisible(boolean isVisible) {
        mVisible = isVisible;
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

    @Override
    public void firmwareVersionReceived(Constants.DfuFirmwareTypes whichfirmware, String version) {
        if(mOnSyncControllerListener.notEmpty()) mOnSyncControllerListener.get().firmwareVersionReceived(whichfirmware,version);
    }

    @Override
    public Void visit(QuickBTUnBindNevoException e) {
        return null;
    }

    @Override
    public Void visit(BLEConnectTimeoutException e) {
        mTimeOutcount = mTimeOutcount + 1;
        //when reconnect is more than 3, popup message to user to reopen bluetooth or restart smartphone
        if (mTimeOutcount  == 3) {
            mTimeOutcount = 0;
            showMessage(e.getWarningMessageTitle(), e.getWarningMessageId());
        }
        return null;
    }

    @Override
    public Void visit(final BLENotSupportedException e) {
        try {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(), e.getWarningMessageId(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e2) {

        }
        return null;
    }

    @Override
    public Void visit(final BLEUnstableException e) {
        try {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(), e.getWarningMessageId(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e3) {
        }
        return null;
    }

    @Override
    public Void visit(final BluetoothDisabledException e) {
        try {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(), e.getWarningMessageId(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e1) {

        }
        return null;
    }

    @Override
    public Void visit(QuickBTSendTimeoutException e) {
        return null;
    }

    @Override
    public Void visit(NevoException e) {
        return null;
    }

    /*inner class , static type, @link:http://stackoverflow.com/questions/10305261/broadcastreceiver-cant-instantiate-class-no-empty-constructor */
    static public class LocalService extends Service
    {
        private AlertDialog mAlertDialog = null;
        BroadcastReceiver myReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(Intent.ACTION_SCREEN_ON))
                {
                    Log.i("LocalService","Screen On");
                    if(!ConnectionController.Singleton.getInstance(context).isConnected())
                    {
                        ConnectionController.Singleton.getInstance(context).newScan();
                    }
                }
                else if(intent.getAction().equals(BluetoothDevice.ACTION_ACL_CONNECTED))
                {
                    Log.i("LocalService","Low level BT connected");
                    if(!ConnectionController.Singleton.getInstance(context).isConnected())
                    {
                        ConnectionController.Singleton.getInstance(context).newScan();
                    }
                }
            }
        };

        @Override
        public void onCreate() {
            super.onCreate();
            registerReceiver(myReceiver,new IntentFilter(Intent.ACTION_SCREEN_ON));
            registerReceiver(myReceiver,new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED));
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            unregisterReceiver(myReceiver);
        }

        @Override
        public IBinder onBind(Intent intent) {
            return new LocalBinder();
        }

        private void PopupMessage(final int titleID, final int msgID)
        {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                @Override
                public void run() {
                    if(mAlertDialog !=null && mAlertDialog.isShowing())
                    {
                        return;
                    }
                    AlertDialog.Builder ab = new AlertDialog.Builder(LocalService.this, AlertDialog.THEME_HOLO_LIGHT)
                            .setPositiveButton(getResources().getString(R.string.ble_connection_timeout_help), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Uri uri = Uri.parse(getResources().getString(R.string.ble_connecttimeout_url));
                                    Intent it = new Intent(Intent.ACTION_VIEW, uri);
                                    it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(it);

                                }
                            })
                            .setNegativeButton(getResources().getString(R.string.ble_connection_timeout_cancel), new DialogInterface.OnClickListener() {
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
            public void findCellPhone()
            {
                LocalService.this.findCellPhone();
            }
        }
        //when nevo paired cellphone, press twice A key, will invoke "findCellPhone"
        //start vibrate
        //light screen on
        //play music ???
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        private void findCellPhone()
        {
            Vibrator vibrator = (Vibrator) LocalService.this.getSystemService(Context.VIBRATOR_SERVICE);
            long[] pattern = {1,2000,1000,2000,1000,2000,1000};
            if(vibrator.hasVibrator())
            {
                vibrator.cancel();
            }
            vibrator.vibrate(pattern,-1);

            PowerManager pm = (PowerManager) LocalService.this.getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
                    | PowerManager.SCREEN_BRIGHT_WAKE_LOCK,"bright");
            wl.acquire();
            wl.release();

            //play build-in music  in Raw
            PlayFromRawFile();
        }
        private  void PlayFromRawFile()
        {
            final MediaPlayer play = MediaPlayer.create(this, com.medcorp.nevo.R.raw.music);
            final long currentTime = System.currentTimeMillis();
            play.setAudioStreamType(AudioManager.STREAM_MUSIC);
            AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume , 0);
            if(play.isPlaying())
            {
                play.stop();
            }
            play.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    //music duration is 2s, repeat 3 times
                    if (System.currentTimeMillis() - currentTime < 6000)
                    {
                        play.start();
                    }
                }
            });
            play.start();
        }
    }
}
