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
import com.medcorp.nevo.application.ApplicationModel;
import com.medcorp.nevo.ble.datasource.GattAttributesDataSourceImpl;
import com.medcorp.nevo.ble.datasource.NotificationDataHelper;
import com.medcorp.nevo.ble.model.notification.CalendarNotification;
import com.medcorp.nevo.ble.model.notification.EmailNotification;
import com.medcorp.nevo.ble.model.notification.FacebookNotification;
import com.medcorp.nevo.ble.model.notification.Notification;
import com.medcorp.nevo.ble.model.notification.SmsNotification;
import com.medcorp.nevo.ble.model.notification.TelephoneNotification;
import com.medcorp.nevo.ble.model.notification.WeChatNotification;
import com.medcorp.nevo.ble.model.notification.WhatsappNotification;
import com.medcorp.nevo.ble.model.packet.DailyStepsNevoPacket;
import com.medcorp.nevo.ble.model.packet.DailyTrackerInfoNevoPacket;
import com.medcorp.nevo.ble.model.packet.DailyTrackerNevoPacket;
import com.medcorp.nevo.ble.model.packet.NevoPacket;
import com.medcorp.nevo.ble.model.request.GetBatteryLevelNevoRequest;
import com.medcorp.nevo.ble.model.request.GetStepsGoalNevoRequest;
import com.medcorp.nevo.ble.model.request.LedLightOnOffNevoRequest;
import com.medcorp.nevo.ble.model.request.ReadDailyTrackerInfoNevoRequest;
import com.medcorp.nevo.ble.model.request.ReadDailyTrackerNevoRequest;
import com.medcorp.nevo.ble.model.request.SetAlarmNevoRequest;
import com.medcorp.nevo.ble.model.request.SetCardioNevoRequest;
import com.medcorp.nevo.ble.model.request.SetGoalNevoRequest;
import com.medcorp.nevo.ble.model.request.SetNotificationNevoRequest;
import com.medcorp.nevo.ble.model.request.SetProfileNevoRequest;
import com.medcorp.nevo.ble.model.request.SetRtcNevoRequest;
import com.medcorp.nevo.ble.model.request.TestModeNevoRequest;
import com.medcorp.nevo.ble.model.request.WriteSettingNevoRequest;
import com.medcorp.nevo.database.dao.IDailyHistory;
import com.medcorp.nevo.event.BatteryEvent;
import com.medcorp.nevo.event.FindWatchEvent;
import com.medcorp.nevo.event.InitializeEvent;
import com.medcorp.nevo.event.LittleSyncEvent;
import com.medcorp.nevo.event.OnSyncEvent;
import com.medcorp.nevo.event.RequestResponseEvent;
import com.medcorp.nevo.model.Alarm;
import com.medcorp.nevo.model.Battery;
import com.medcorp.nevo.model.DailyHistory;
import com.medcorp.nevo.model.GoalBase;
import com.medcorp.nevo.model.Sleep;
import com.medcorp.nevo.model.Steps;
import com.medcorp.nevo.util.Common;
import com.medcorp.nevo.util.Preferences;

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

    private static final int SYNC_INTERVAL = 0*30*60*1000; //every half hour , do sync when connected again

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
    //IMPORT!!!!, every get connected, will do sync profile data and activity data with Nevo
    //it perhaps long time(sync activity data perhaps need long time, MAX total 7 days)
    //so before sync finished, disable setGoal/setAlarm/getGoalSteps
    //make sure  the whole received packets

    public SyncControllerImpl(Context context)
    {
        mContext = context;

        connectionController = ConnectionController.Singleton.getInstance(context, new GattAttributesDataSourceImpl(context));
        Intent intent = new Intent(mContext,LocalService.class);
        mContext.getApplicationContext().bindService(intent, mCurrentServiceConnection, Activity.BIND_AUTO_CREATE);
        EventBus.getDefault().register(this);
    }

    /*package*/void setContext(Context context) {
        if(context!=null)
            mContext = context;
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
        initNotification = init;
        Map<Notification, Integer> applicationNotificationColorMap = new HashMap<Notification, Integer>();
        NotificationDataHelper dataHelper = new NotificationDataHelper(mContext);
        Notification applicationNotification = new TelephoneNotification();
        applicationNotificationColorMap.put(dataHelper.getState(applicationNotification), Preferences.getNotificationColor(mContext,new SmsNotification()).getHexColor());
        applicationNotificationColorMap.put(dataHelper.getState(applicationNotification), Preferences.getNotificationColor(mContext, new EmailNotification()).getHexColor());
        applicationNotificationColorMap.put(dataHelper.getState(applicationNotification), Preferences.getNotificationColor(mContext, new FacebookNotification()).getHexColor());
        applicationNotificationColorMap.put(dataHelper.getState(applicationNotification), Preferences.getNotificationColor(mContext, new CalendarNotification()).getHexColor());
        applicationNotificationColorMap.put(dataHelper.getState(applicationNotification), Preferences.getNotificationColor(mContext, new WeChatNotification()).getHexColor());
        applicationNotificationColorMap.put(dataHelper.getState(applicationNotification), Preferences.getNotificationColor(mContext, new WhatsappNotification()).getHexColor());
        sendRequest(new SetNotificationNevoRequest(mContext, applicationNotificationColorMap));

    }

    @Subscribe
    public void onEvent(BLEResponseDataEvent eventData) {
        BLEResponseData data = eventData.getData();
        if (data.getType().equals(MEDRawData.TYPE))
        {
            final MEDRawData nevoData = (MEDRawData) data;
            packetsBuffer.add(nevoData);

            if((byte)0xFF == nevoData.getRawData()[0])
            {
                QueuedMainThreadHandler.getInstance(QueuedMainThreadHandler.QueueType.SyncController).next();

                NevoPacket packet = new NevoPacket(packetsBuffer);
                //if packets invaild, discard them, and reset buffer
                if(!packet.isVaildPackets())
                {
                    Log.e("Nevo Error","InVaild Packets Received!");
                    packetsBuffer.clear();

                    return;
                }

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
                    EventBus.getDefault().post(new InitializeEvent(InitializeEvent.INITIALIZE_STATUS.END));
                    //start sync notification, phone --> nevo
                    // set Local Notification setting to Nevo, when nevo 's battery removed, the
                    // Steps count is 0, and all notification is off, because Notification is very
                    // important for user, so here need use local's setting sync with nevo
                    setNotification(true);
                }
                else if((byte) SetNotificationNevoRequest.HEADER == nevoData.getRawData()[1])
                {
                    if(initNotification)
                    {
                        List<Alarm> list = ((ApplicationModel) mContext).getAllAlarm();
                        if(!list.isEmpty())
                        {
                            List<Alarm> customerAlarmList = new ArrayList<Alarm>();
                            for(Alarm alarm: list)
                            {
                                if(alarm.isEnable())
                                {
                                    customerAlarmList.add(alarm);
                                    if(customerAlarmList.size()>=SetAlarmNevoRequest.maxAlarmCount)
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
                            list.add(new Alarm(0,0, false, ""));
                            setAlarm(list, true);
                        }
                    }
                    else
                    {
                        //call setNotification() by application, here reset it true
                        initNotification = true;
                    }
                }
                else if((byte) SetAlarmNevoRequest.HEADER == nevoData.getRawData()[1])
                {
                    if(initAlarm)
                    {
                        //start sync data, nevo-->phone
                        syncActivityData();
                    }
                    else
                    {
                        //call setAlarm() by application, here reset it true
                        initAlarm = true;
                    }
                }
                else if((byte) ReadDailyTrackerInfoNevoRequest.HEADER == nevoData.getRawData()[1])
                {
                    if(!mSyncAllFlag) {
                        DailyTrackerInfoNevoPacket infopacket = packet.newDailyTrackerInfoNevoPacket();
                        mCurrentDay = 0;
                        savedDailyHistory = infopacket.getDailyTrackerInfo();
                        if (!savedDailyHistory.isEmpty()) {
                            mSyncAllFlag = true;
                            getDailyTracker(mCurrentDay);
                        }
                    }
                }
                else if((byte) ReadDailyTrackerNevoRequest.HEADER == nevoData.getRawData()[1])
                {
                    DailyTrackerNevoPacket thispacket = packet.newDailyTrackerNevoPacket();

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
                    //save current day's step count to "Steps" table
                    Date currentday = new Date();
                    Steps steps = new Steps(currentday.getTime());

                    steps.setDate(Common.removeTimeFromDate(currentday).getTime());

                    DailyStepsNevoPacket stepPacket = packet.newDailyStepsNevoPacket();
                    steps.setSteps(stepPacket.getDailySteps());
                    steps.setGoal(stepPacket.getDailyStepsGoal());
                    steps.setNevoUserID(((ApplicationModel) mContext).getNevoUser().getNevoUserID());
                    //I can't calculator these value from this packet, they should come from CMD 0x25 cmd
                    ((ApplicationModel)mContext).saveDailySteps(steps);
                    //end save
                }
                //process done(such as save local db), then notify top layer to get or refresh screen
                if (packet.getHeader() == (byte) GetStepsGoalNevoRequest.HEADER) {
                    EventBus.getDefault().post(new LittleSyncEvent(true));
                }
                else if((byte) GetBatteryLevelNevoRequest.HEADER == packet.getHeader()) {
                    EventBus.getDefault().post(new BatteryEvent(new Battery(packet.newBatteryLevelNevoPacket().getBatteryLevel())));
                }
                else if((byte) 0xF0 == packet.getHeader()) {
                    EventBus.getDefault().post(new FindWatchEvent(true));
                }
                else if((byte) SetAlarmNevoRequest.HEADER == packet.getHeader()
                        || (byte) SetNotificationNevoRequest.HEADER == packet.getHeader()
                        || (byte) SetGoalNevoRequest.HEADER == packet.getHeader()) {
                    EventBus.getDefault().post(new RequestResponseEvent(true));
                }
                packetsBuffer.clear();
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

    private void  getDailyTracker(int trackerNo) {
        sendRequest(new ReadDailyTrackerNevoRequest(mContext, trackerNo));
    }

    @Override
    public void setGoal(GoalBase goal) {
        sendRequest(new SetGoalNevoRequest(mContext,goal));
    }

    @Override
    public void setAlarm(List<Alarm> list,boolean init) {
        initAlarm = init;
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
        sendRequest(new LedLightOnOffNevoRequest(mContext,0x3F0000,true));
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
