package com.medcorp.ble.controller;

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
import android.content.pm.PackageManager;
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


import com.medcorp.R;
import com.medcorp.application.ApplicationModel;
import com.medcorp.ble.datasource.GattAttributesDataSourceImpl;
import com.medcorp.ble.model.packet.*;
import com.medcorp.ble.model.request.*;
import com.medcorp.ble.notification.NevoNotificationListener;
import com.medcorp.database.dao.IDailyHistory;
import com.medcorp.event.bluetooth.BatteryEvent;
import com.medcorp.event.bluetooth.FindWatchEvent;
import com.medcorp.event.bluetooth.InitializeEvent;
import com.medcorp.event.bluetooth.LittleSyncEvent;
import com.medcorp.event.bluetooth.OnSyncEvent;
import com.medcorp.event.bluetooth.RequestResponseEvent;
import com.medcorp.model.Alarm;
import com.medcorp.model.ApplicationInfomation;
import com.medcorp.model.Battery;
import com.medcorp.model.DailyHistory;
import com.medcorp.model.GoalBase;
import com.medcorp.model.Sleep;
import com.medcorp.model.Steps;
import com.medcorp.model.WatchInfomation;
import com.medcorp.util.Common;

import net.medcorp.library.ble.controller.ConnectionController;
import net.medcorp.library.ble.event.BLEConnectionStateChangedEvent;
import net.medcorp.library.ble.event.BLEResponseDataEvent;
import net.medcorp.library.ble.exception.BLEConnectTimeoutException;
import net.medcorp.library.ble.exception.BLENotSupportedException;
import net.medcorp.library.ble.exception.BLEUnstableException;
import net.medcorp.library.ble.exception.BaseBLEException;
import net.medcorp.library.ble.exception.BluetoothDisabledException;
import net.medcorp.library.ble.exception.QuickBTSendTimeoutException;
import net.medcorp.library.ble.exception.QuickBTUnBindException;
import net.medcorp.library.ble.exception.visitor.BLEExceptionVisitor;
import net.medcorp.library.ble.model.request.BLERequestData;
import net.medcorp.library.ble.model.response.BLEResponseData;
import net.medcorp.library.ble.model.response.MEDRawData;
import net.medcorp.library.ble.util.Constants;
import net.medcorp.library.ble.util.QueuedMainThreadHandler;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class SyncControllerImpl implements SyncController, BLEExceptionVisitor<Void> {
    private final static String TAG = "SyncControllerImpl";

    private Context mContext;

    private static final int SYNC_INTERVAL = 1*30*60*1000; //every half hour , do sync when connected again

    private ConnectionController connectionController;


    private List<MEDRawData> packetsBuffer = new ArrayList<MEDRawData>();

    private List<DailyHistory> savedDailyHistory = new ArrayList<DailyHistory>();
    private int mCurrentDay = 0;
    private int mTimeOutcount = 0;
    private long mLastPressAkey = 0;
    private boolean mEnableTestMode = false;
    private boolean mSyncAllFlag = false;
    private boolean initAlarm = true;
    private boolean initNotification = true;
    private boolean isHoldRequest = false;
    //IMPORT!!!!, every get connected, will do sync profile data and activity data with Nevo
    //it perhaps long time(sync activity data perhaps need long time, MAX total 7 days)
    //so before sync finished, disable setGoal/setAlarm/getGoalSteps
    //make sure  the whole received packets

    private WatchInfomation watchInfomation;

    public SyncControllerImpl(Context context)
    {
        mContext = context;
        watchInfomation = new WatchInfomation();
        connectionController = ConnectionController.Singleton.getInstance(context, new GattAttributesDataSourceImpl(context));
        Intent intent = new Intent(mContext,LocalService.class);
        mContext.getApplicationContext().bindService(intent, mCurrentServiceConnection, Activity.BIND_AUTO_CREATE);
        //force android Notification Manager Service to start NevoNotificationListener
        startNotificationListener();
        EventBus.getDefault().register(this);
    }

    @Override
    public void startConnect(boolean forceScan) {
        if (forceScan)
        {
            connectionController.forgetSavedAddress();
        }
        connectionController.scan();
    }

    //Each packets should go through this function. It will ensure that they are properly queued and sent in order.
    @Override
    public void sendRequest(final BLERequestData request)
    {
        if(connectionController.inOTAMode()) {
            return;
        }
        if(!isConnected()) {
            return;
        }
        //when OTA mode, disable syncController send any request command to BLE
        if(isHoldRequest) {
            return;
        }
        QueuedMainThreadHandler.getInstance(QueuedMainThreadHandler.QueueType.SyncController).post(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, request.getClass().getName());
                connectionController.sendRequest(request);
            }
        });

    }

    @Override
    public void setNotification(boolean init) {
        //TODO: lunar notification use med-library ???
    }

    @Override
    public WatchInfomation getWatchInfomation() {
        return watchInfomation;
    }

    @Subscribe
    public void onEvent(BLEResponseDataEvent eventData) {
        BLEResponseData data = eventData.getData();
        if (data.getType().equals(MEDRawData.TYPE))
        {
            final MEDRawData lunarData = (MEDRawData) data;
            packetsBuffer.add(lunarData);

            if((byte)0xFF == lunarData.getRawData()[0])
            {
                Packet packet = new Packet(packetsBuffer);
                //if packets invaild, discard them, and reset buffer
                if(!packet.isVaildPackets())
                {
                    Log.e("Nevo Error","InVaild Packets Received!");
                    packetsBuffer.clear();
                    QueuedMainThreadHandler.getInstance(QueuedMainThreadHandler.QueueType.SyncController).next();
                    return;
                }

                if((byte) SetRtcRequest.HEADER == lunarData.getRawData()[1])
                {
                    //setp2:start set user profile
                    sendRequest(new SetProfileRequest(mContext,((ApplicationModel)mContext).getNevoUser()));
                }
                else if((byte) SetProfileRequest.HEADER == lunarData.getRawData()[1])
                {
                    //step3:WriteSetting
                    sendRequest(new WriteSettingRequest(mContext));
                }
                else if((byte) WriteSettingRequest.HEADER == lunarData.getRawData()[1])
                {
                    //step4:read watch infomation
                    sendRequest(new ReadWatchInfoRequest(mContext));
                }
                else if((byte) ReadWatchInfoRequest.HEADER == lunarData.getRawData()[1])
                {
                    //get watch ID and model type
                    WatchInfoPacket thispacket = new WatchInfoPacket(packet.getPackets());
                    Log.i(TAG,"get watch ID: " + thispacket.getWatchID() + ", watch model: " + thispacket.getWatchModel());
                    watchInfomation.setWatchID(thispacket.getWatchID());
                    watchInfomation.setWatchModel(thispacket.getWatchModel());

                    EventBus.getDefault().post(new InitializeEvent(InitializeEvent.INITIALIZE_STATUS.END));
                        List<Alarm> list = ((ApplicationModel) mContext).getAllAlarm();
                        if(!list.isEmpty())
                        {
                            List<Alarm> customerAlarmList = new ArrayList<Alarm>();
                            for(Alarm alarm: list)
                            {
                                if(alarm.getWeekDay()>0)
                                {
                                    customerAlarmList.add(alarm);
                                    if(customerAlarmList.size()>= SetAlarmWithTypeRequest.maxAlarmCount)
                                    {
                                        break;
                                    }
                                }
                            }
                            if(customerAlarmList.isEmpty())
                            {
                                customerAlarmList.add(list.get(0));
                            }
                            setAlarm(customerAlarmList, true);
                        }
                        else
                        {
                            list.add(new Alarm(0,0, (byte) 0, "",0,"",false));
                            setAlarm(list, true);
                        }

                }
                else if((byte) SetAlarmWithTypeRequest.HEADER == lunarData.getRawData()[1])
                {
                    if(initAlarm)
                    {
                        initAlarm = false;
                        //start sync data, nevo-->phone
                        syncActivityData();
                    }
                }
                else if((byte) ReadDailyTrackerInfoRequest.HEADER == lunarData.getRawData()[1])
                {
                    if(!mSyncAllFlag) {
                        DailyTrackerInfoPacket infopacket = new DailyTrackerInfoPacket(packet.getPackets());
                        mCurrentDay = 0;
                        savedDailyHistory = infopacket.getDailyTrackerInfo();
                        if (!savedDailyHistory.isEmpty()) {
                            mSyncAllFlag = true;
                            getDailyTracker(mCurrentDay);
                        }
                    }
                }
                else if((byte) ReadDailyTrackerRequest.HEADER == lunarData.getRawData()[1])
                {
                    DailyTrackerPacket thispacket = new DailyTrackerPacket(packet.getPackets());

                    if(savedDailyHistory.isEmpty()) {
                        mCurrentDay = 0;
                        savedDailyHistory.add(mCurrentDay, new DailyHistory(thispacket.getDate()));
                    }
                    savedDailyHistory.get(mCurrentDay).setTotalSteps(thispacket.getDailySteps());
                    savedDailyHistory.get(mCurrentDay).setHourlySteps(thispacket.getHourlySteps());
//                        Log.i(savedDailyHistory.get(mCurrentDay).getDate().toString(), "Hourly Steps:" + savedDailyHistory.get(mCurrentDay).getHourlySteps().toString());

                    savedDailyHistory.get(mCurrentDay).setTotalSleepTime(thispacket.getTotalSleepTime());
                    savedDailyHistory.get(mCurrentDay).setHourlySleepTime(thispacket.getHourlySleepTime());

                    Log.i(savedDailyHistory.get(mCurrentDay).getDate().toString(), "Daily Sleep time:" + savedDailyHistory.get(mCurrentDay).getTotalSleepTime());
                    Log.i(savedDailyHistory.get(mCurrentDay).getDate().toString(), "Hourly Sleep time:" + savedDailyHistory.get(mCurrentDay).getHourlySleepTime().toString());

                    savedDailyHistory.get(mCurrentDay).setTotalWakeTime(thispacket.getTotalWakeTime());
                    savedDailyHistory.get(mCurrentDay).setHourlyWakeTime(thispacket.getHourlyWakeTime());

                    Log.i(savedDailyHistory.get(mCurrentDay).getDate().toString(), "Daily Wake time:" + savedDailyHistory.get(mCurrentDay).getTotalWakeTime());
                    Log.i(savedDailyHistory.get(mCurrentDay).getDate().toString(), "Hourly Wake time:" + savedDailyHistory.get(mCurrentDay).getHourlyWakeTime().toString());

                    savedDailyHistory.get(mCurrentDay).setTotalLightTime(thispacket.getTotalLightTime());
                    savedDailyHistory.get(mCurrentDay).setHourlyLightTime(thispacket.getHourlyLightTime());

                    Log.i(savedDailyHistory.get(mCurrentDay).getDate().toString(), "Daily light time:" + savedDailyHistory.get(mCurrentDay).getTotalLightTime());
                    Log.i(savedDailyHistory.get(mCurrentDay).getDate().toString(), "Hourly light time:" + savedDailyHistory.get(mCurrentDay).getHourlyLightTime().toString());


                    savedDailyHistory.get(mCurrentDay).setTotalDeepTime(thispacket.getTotalDeepTime());
                    savedDailyHistory.get(mCurrentDay).setHourlDeepTime(thispacket.getHourlDeepTime());

                    Log.i(savedDailyHistory.get(mCurrentDay).getDate().toString(), "Daily deep time:" + savedDailyHistory.get(mCurrentDay).getTotalDeepTime());
                    Log.i(savedDailyHistory.get(mCurrentDay).getDate().toString(), "Hourly deep time:" + savedDailyHistory.get(mCurrentDay).getHourlDeepTime().toString());

                    savedDailyHistory.get(mCurrentDay).setTotalDist(thispacket.getTotalDist());
                    savedDailyHistory.get(mCurrentDay).setHourlyDist(thispacket.getHourlyDist());
                    savedDailyHistory.get(mCurrentDay).setTotalCalories(thispacket.getTotalCalories());
                    savedDailyHistory.get(mCurrentDay).setHourlyCalories(thispacket.getHourlyCalories());

                    IDailyHistory history = new IDailyHistory(savedDailyHistory.get(mCurrentDay));
                    //DatabaseHelper.getInstance(mContext).SaveDailyHistory(history);
                    //Log.i(TAG, savedDailyHistory.get(mCurrentDay).getDate().toString() + " successfully saved to database, created = " + history.getCreated());
                    //update steps/sleep tables
                    Steps steps = new Steps(history.getCreated());
                    steps.setDate(Common.removeTimeFromDate(savedDailyHistory.get(mCurrentDay).getDate()).getTime());
                    steps.setNevoUserID(((ApplicationModel) mContext).getNevoUser().getNevoUserID());
                    steps.setSteps(history.getSteps());
                    steps.setCalories((int) history.getCalories());
                    steps.setDistance((int) history.getDistance());
                    steps.setHourlyCalories(history.getHourlycalories());
                    steps.setHourlyDistance(history.getHourlydistance());
                    steps.setHourlySteps(history.getHourlysteps());

                    steps.setGoal(thispacket.getStepsGoal());
                    steps.setWalkSteps(thispacket.getDailyWalkSteps());
                    steps.setRunSteps(thispacket.getDailyRunSteps());
                    steps.setWalkDistance(thispacket.getDailyWalkDistance());
                    steps.setRunDistance(thispacket.getDailyRunDistance());
                    steps.setWalkDuration(thispacket.getDailyWalkDuration());
                    steps.setRunDuration(thispacket.getDailyRunDuration());
                    try {
                        steps.setRemarks(new JSONObject().put("date", new SimpleDateFormat("yyyy-MM-dd").format(new Date(steps.getDate()))).toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //update  the day 's "steps" table
                    if(steps.getSteps() !=0) {
                        ((ApplicationModel) mContext).saveDailySteps(steps);
                    }
                    if(history.getTotalSleepTime() != 0) {
                        Sleep sleep = new Sleep(history.getCreated());
                        sleep.setDate(Common.removeTimeFromDate(savedDailyHistory.get(mCurrentDay).getDate()).getTime());
                        sleep.setHourlySleep(history.getHourlySleepTime());
                        sleep.setHourlyWake(history.getHourlyWakeTime());
                        sleep.setHourlyLight(history.getHourlyLightTime());
                        sleep.setHourlyDeep(history.getHourlDeepTime());
                        sleep.setTotalSleepTime(history.getTotalSleepTime());
                        sleep.setTotalWakeTime(history.getTotalWakeTime());
                        sleep.setTotalLightTime(history.getTotalLightTime());
                        sleep.setTotalDeepTime(history.getTotalDeepTime());
                        //firstly reset sleep start/end time is 0, it means the day hasn't been calculate sleep analysis.
                        sleep.setStart(0);
                        sleep.setEnd(0);
                        sleep.setNevoUserID(((ApplicationModel) mContext).getNevoUser().getNevoUserID());
                        try {
                            sleep.setRemarks(new JSONObject().put("date", new SimpleDateFormat("yyyy-MM-dd").format(new Date(sleep.getDate()))).toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ((ApplicationModel) mContext).saveDailySleep(sleep);
                        //end update
                    }
                    mCurrentDay++;
                    if(mCurrentDay < savedDailyHistory.size() && mSyncAllFlag)
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
                /* //this is old code ,when press key down/up, watch will send key event to app. now it will send "findphone" event to app
                else if((byte) 0xF1 == lunarData.getRawData()[1] && (byte) 0x02 == packet.getPackets().get(0).getRawData()[2])
                {
                    long currentTime = System.currentTimeMillis();
                    //remove repeat press B key down within 6s
                    if(currentTime - mLastPressAkey > 6000)
                    {
                        mLastPressAkey = currentTime;
                    }
                }
                else if((byte) 0xF1 == lunarData.getRawData()[1] && (byte) 0x00 == packet.getPackets().get(0).getRawData()[2])
                {
                    long currentTime = System.currentTimeMillis();
                    if(currentTime - mLastPressAkey < 500)
                    {
                        if(mLocalService!=null) {
                            mLocalService.findCellPhone();
                            sendRequest(new FindPhoneRequest(mContext));
                        }
                    }
                }*/
                else if((byte) FindPhoneRequest.HEADER == lunarData.getRawData()[1])
                {
                    if(mLocalService!=null) {
                        mLocalService.findCellPhone();
                        //send response to watch that found out
                        sendRequest(new FindPhoneRequest(mContext));
                    }
                }
                else if((byte) GetStepsGoalRequest.HEADER == lunarData.getRawData()[1])
                {
                    //save current day's step count to "Steps" table
                    Date currentday = new Date();
                    Steps steps = new Steps(currentday.getTime());

                    steps.setDate(Common.removeTimeFromDate(currentday).getTime());

                    DailyStepsPacket stepPacket = new DailyStepsPacket(packet.getPackets());
                    steps.setSteps(stepPacket.getDailySteps());
                    steps.setGoal(stepPacket.getDailyStepsGoal());
                    steps.setNevoUserID(((ApplicationModel) mContext).getNevoUser().getNevoUserID());
                    //I can't calculator these value from this packet, they should come from CMD 0x25 cmd
                    ((ApplicationModel)mContext).saveDailySteps(steps);
                    //end save
                }
                else if((byte) NewApplicationArrivedPacket.HEADER == lunarData.getRawData()[1])
                {
                    NewApplicationArrivedPacket newApplicationArrivedPacket = new NewApplicationArrivedPacket(packet.getPackets());
                    Log.i(TAG,"new Application arrived,total:" + newApplicationArrivedPacket.getTotalApplications());
                    Log.i(TAG,"new Application arrived,ID: " + newApplicationArrivedPacket.getApplicationInfomation().getData());
                    //TODO send AddApplicationRequest ???
                    sendRequest(new AddApplicationRequest(mContext,newApplicationArrivedPacket.getApplicationInfomation()));
                }
                //process done(such as save local db), then notify top layer to get or refresh screen
                if (packet.getHeader() == (byte) GetStepsGoalRequest.HEADER) {
                    EventBus.getDefault().post(new LittleSyncEvent(true));
                }
                else if((byte) GetBatteryLevelRequest.HEADER == packet.getHeader()) {
                    EventBus.getDefault().post(new BatteryEvent(new Battery(new BatteryLevelPacket(packet.getPackets()).getBatteryLevel())));
                }
                else if((byte) FindWatchRequest.HEADER == packet.getHeader()) {
                    EventBus.getDefault().post(new FindWatchEvent(true));
                }
                else if((byte) SetAlarmWithTypeRequest.HEADER == packet.getHeader()
                        || (byte) SetNotificationRequest.HEADER == packet.getHeader()
                        || (byte) SetGoalRequest.HEADER == packet.getHeader()) {
                    EventBus.getDefault().post(new RequestResponseEvent(true));
                }
                packetsBuffer.clear();
                QueuedMainThreadHandler.getInstance(QueuedMainThreadHandler.QueueType.SyncController).next();
            }
        }
    }

    @Subscribe
    public void onEvent(BLEConnectionStateChangedEvent stateChangedEvent) {

        if(stateChangedEvent.isConnected()) {
            mEnableTestMode = false;
            mTimeOutcount = 0;
            //step1:setRTC, should defer about 2s for waiting the Callback characteristic enable Notify
            //and wait reading FW version done , then do setRTC.
            Log.w("Karl","SET RTC");
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    packetsBuffer.clear();
                    setRtc();
                }
            }, 2000);
        } else {
            QueuedMainThreadHandler.getInstance(QueuedMainThreadHandler.QueueType.SyncController).clear();
            packetsBuffer.clear();
            mSyncAllFlag = false;
        }
    }

    /**
     This function will synchronise activity data with the watch.
     It is a long process and hence shouldn't be done too often, so we save the date of previous sync.
     The watch should be emptied after all data have been saved.
     */
    private void syncActivityData() {
        long lastSync = mContext.getSharedPreferences(Constants.PREF_NAME, 0).getLong(Constants.LAST_SYNC, 0);
        String lastTimeZone = mContext.getSharedPreferences(Constants.PREF_NAME, 0).getString(Constants.LAST_SYNC_TIME_ZONE, "");
        if(Calendar.getInstance().getTimeInMillis()-lastSync > SYNC_INTERVAL
                || !TimeZone.getDefault().getID().equals(lastTimeZone) ) {
            Log.i(TAG,"*** Sync started ! ***");
            EventBus.getDefault().post(new OnSyncEvent(OnSyncEvent.SYNC_EVENT.STARTED));

            getDailyTrackerInfo(true);
        } else {
            Log.i(TAG,"*** Sync step count and goal ***");
            getStepsAndGoal();
        }
    }

    private void syncFinished() {
        Log.i(TAG,"*** Sync finished ***");
        EventBus.getDefault().post(new OnSyncEvent(OnSyncEvent.SYNC_EVENT.STOPPED));
        mContext.getSharedPreferences(Constants.PREF_NAME, 0).edit().putLong(Constants.LAST_SYNC, Calendar.getInstance().getTimeInMillis()).commit();
        mContext.getSharedPreferences(Constants.PREF_NAME, 0).edit().putString(Constants.LAST_SYNC_TIME_ZONE, TimeZone.getDefault().getID()).commit();
        //tell history to refresh
    }

    private void setRtc() {
        sendRequest(new SetRtcRequest(mContext));
    }

    @Override
    public void  getDailyTrackerInfo(boolean syncAll)
    {
        if(syncAll){
            sendRequest(new ReadDailyTrackerInfoRequest(mContext));
        } else if(!mSyncAllFlag){
            getDailyTracker(0);
        }
    }

    private void  getDailyTracker(int trackerNo) {
        sendRequest(new ReadDailyTrackerRequest(mContext, trackerNo));
    }

    @Override
    public void setGoal(GoalBase goal) {
        sendRequest(new SetGoalRequest(mContext,goal));
    }

    @Override
    public void setAlarm(List<Alarm> list,boolean init) {
        initAlarm = init;
        byte index = 0;
        for(Alarm alarm:list) {
            sendRequest(new SetAlarmWithTypeRequest(mContext, alarm,index));
            index++;
        }
    }

    @Override
    public void getStepsAndGoal() {
        sendRequest(new GetStepsGoalRequest(mContext));
    }

    @Override
    public void getBatteryLevel()
    {
        sendRequest(new GetBatteryLevelRequest(mContext));
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
        sendRequest(new FindWatchRequest(mContext));
    }

    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    public boolean isConnected() {
        return connectionController.isConnected();
    }

    @Override
    public String getFirmwareVersion() {
        return connectionController.getBluetoothVersion();
    }

    @Override
    public String getSoftwareVersion() {
        return connectionController.getSoftwareVersion();
    }
    @Override
    public void forgetDevice() {
        //step1:disconnect
        if(connectionController.isConnected())
        {
            connectionController.disconnect();
        }
        //step2:unpair this watch from system bluetooth setting
        connectionController.unPairDevice();
        //step3:reset MAC address and firstly run flag and big sync stamp
        connectionController.forgetSavedAddress();
        getContext().getSharedPreferences(Constants.PREF_NAME, 0).edit().putBoolean(Constants.FIRST_FLAG,true).commit();
        //when forget the watch, force a big sync when got connected again
        getContext().getSharedPreferences(Constants.PREF_NAME, 0).edit().putLong(Constants.LAST_SYNC, 0).commit();
    }
    @Override
    public void startNotificationListener() {
        //force android NotificationManagerService to rebind user NotificationListenerService
        // http://www.zhihu.com/question/33540416/answer/113706620
        PackageManager pm = mContext.getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName(mContext, NevoNotificationListener.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        pm.setComponentEnabledSetting(new ComponentName(mContext, NevoNotificationListener.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

    }

    @Override
    public void setHoldRequest(boolean holdRequest) {
        isHoldRequest = holdRequest;
    }

    @Override
    public boolean getHoldRequest() {
        return isHoldRequest;
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
    public Void visit(QuickBTUnBindException e) {
        return null;
    }

    @Override
    public Void visit(BLEConnectTimeoutException e) {
        mTimeOutcount = mTimeOutcount + 1;
        //when reconnect is more than 3, popup message to user to reopen bluetooth or restart smartphone
        if (mTimeOutcount  == 3) {
            mTimeOutcount = 0;
            showMessage(R.string.ble_connection_timeout_title, R.string.ble_connect_timeout);
        }
        return null;
    }

    @Override
    public Void visit(final BLENotSupportedException e) {
        try {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(), R.string.ble_not_supported, Toast.LENGTH_LONG).show();
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
                    Toast.makeText(getContext(), R.string.ble_unstable, Toast.LENGTH_LONG).show();
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
                    Toast.makeText(getContext(), R.string.ble_deactivated, Toast.LENGTH_LONG).show();
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
    public Void visit(BaseBLEException e) {
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
                    if(!ConnectionController.Singleton.getInstance(context, new GattAttributesDataSourceImpl(context)).isConnected())
                    {
                        ConnectionController.Singleton.getInstance(context,new GattAttributesDataSourceImpl(context)).scan();
                    }
                }
                else if(intent.getAction().equals(BluetoothDevice.ACTION_ACL_CONNECTED))
                {
                    Log.i("LocalService","Low level BT connected");
                    if(!ConnectionController.Singleton.getInstance(context, new GattAttributesDataSourceImpl(context)).isConnected())
                    {
                        ConnectionController.Singleton.getInstance(context, new GattAttributesDataSourceImpl(context)).scan();
                    }
                }
            }
        };

        @Override
        public void onCreate() {
            super.onCreate();
            registerReceiver(myReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
            registerReceiver(myReceiver, new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED));
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
                                    Uri uri = Uri.parse(getResources().getString(R.string.ble_connect_timeout_url));
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

        private void findCellPhone()
        {
            Vibrator vibrator = (Vibrator) LocalService.this.getSystemService(Context.VIBRATOR_SERVICE);
            long[] pattern = {1,2000,1000,2000,1000,2000,1000};
            if(vibrator.hasVibrator())
            {
                vibrator.cancel();
            }
            vibrator.vibrate(pattern, -1);

            PowerManager pm = (PowerManager) LocalService.this.getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
                    | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
            wl.acquire();
            wl.release();

            //TODO Sound is fine but not sound from dogs. Thanks.
        }
    }
}
