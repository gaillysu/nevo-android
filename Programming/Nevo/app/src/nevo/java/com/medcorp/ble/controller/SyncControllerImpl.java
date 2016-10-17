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

import com.medcorp.BuildConfig;
import com.medcorp.R;
import com.medcorp.application.ApplicationModel;
import com.medcorp.ble.datasource.GattAttributesDataSourceImpl;
import com.medcorp.ble.datasource.NotificationDataHelper;
import com.medcorp.ble.model.notification.CalendarNotification;
import com.medcorp.ble.model.notification.EmailNotification;
import com.medcorp.ble.model.notification.FacebookNotification;
import com.medcorp.ble.model.notification.Notification;
import com.medcorp.ble.model.notification.SmsNotification;
import com.medcorp.ble.model.notification.TelephoneNotification;
import com.medcorp.ble.model.notification.WeChatNotification;
import com.medcorp.ble.model.notification.WhatsappNotification;
import com.medcorp.ble.model.packet.BatteryLevelPacket;
import com.medcorp.ble.model.packet.DailyStepsPacket;
import com.medcorp.ble.model.packet.DailyTrackerInfoPacket;
import com.medcorp.ble.model.packet.DailyTrackerPacket;
import com.medcorp.ble.model.packet.FindPhonePacket;
import com.medcorp.ble.model.packet.Packet;
import com.medcorp.ble.model.packet.WatchInfoPacket;
import com.medcorp.ble.model.request.FindPhoneRequest;
import com.medcorp.ble.model.request.FindWatchRequest;
import com.medcorp.ble.model.request.GetBatteryLevelRequest;
import com.medcorp.ble.model.request.GetStepsGoalRequest;
import com.medcorp.ble.model.request.LedLightOnOffRequest;
import com.medcorp.ble.model.request.ReadDailyTrackerInfoRequest;
import com.medcorp.ble.model.request.ReadDailyTrackerRequest;
import com.medcorp.ble.model.request.ReadWatchInfoRequest;
import com.medcorp.ble.model.request.SetAlarmRequest;
import com.medcorp.ble.model.request.SetAlarmWithTypeRequest;
import com.medcorp.ble.model.request.SetCardioRequest;
import com.medcorp.ble.model.request.SetGoalRequest;
import com.medcorp.ble.model.request.SetNotificationRequest;
import com.medcorp.ble.model.request.SetProfileRequest;
import com.medcorp.ble.model.request.SetRtcRequest;
import com.medcorp.ble.model.request.TestModeRequest;
import com.medcorp.ble.model.request.WriteSettingRequest;
import com.medcorp.ble.notification.NevoNotificationListener;
import com.medcorp.database.dao.IDailyHistory;
import com.medcorp.event.Timer10sEvent;
import com.medcorp.event.bluetooth.BatteryEvent;
import com.medcorp.event.bluetooth.FindWatchEvent;
import com.medcorp.event.bluetooth.InitializeEvent;
import com.medcorp.event.bluetooth.LittleSyncEvent;
import com.medcorp.event.bluetooth.OnSyncEvent;
import com.medcorp.event.bluetooth.RequestResponseEvent;
import com.medcorp.event.bluetooth.SolarConvertEvent;
import com.medcorp.model.Alarm;
import com.medcorp.model.Battery;
import com.medcorp.model.DailyHistory;
import com.medcorp.model.GoalBase;
import com.medcorp.model.Sleep;
import com.medcorp.model.Solar;
import com.medcorp.model.Steps;
import com.medcorp.model.WatchInfomation;
import com.medcorp.util.Common;
import com.medcorp.util.LinklossNotificationUtils;
import com.medcorp.util.Preferences;

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
import net.medcorp.library.ble.util.HexUtils;
import net.medcorp.library.ble.util.Optional;
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
import java.util.Timer;
import java.util.TimerTask;

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


    //start a timer to do little sync, refresh dashboard @LittleSyncEvent
    private long LITTLE_SYNC_INTERVAL = 10000L; //10s
    private Timer autoSyncTimer = null;
    private void startTimer() {
        if(autoSyncTimer!=null)autoSyncTimer.cancel();
        autoSyncTimer = new Timer();
        autoSyncTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                //here do little sync to refesh current steps
                getStepsAndGoal();
                if(BuildConfig.DEBUG) {
                    sendRequest(new TestModeRequest(mContext, TestModeRequest.MODE_F4));
                }
                //send a 10s event to refresh clock
                EventBus.getDefault().post(new Timer10sEvent());
                startTimer();
            }
        }, LITTLE_SYNC_INTERVAL);
    }

    public SyncControllerImpl(Context context)
    {
        mContext = context;
        connectionController = ConnectionController.Singleton.getInstance(context, new GattAttributesDataSourceImpl(context));
        Intent intent = new Intent(mContext,LocalService.class);
        mContext.getApplicationContext().bindService(intent, mCurrentServiceConnection, Activity.BIND_AUTO_CREATE);
        //force android Notification Manager Service to start NevoNotificationListener
        startNotificationListener();
        EventBus.getDefault().register(this);
        startTimer();
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
        sendRequest(new SetNotificationRequest(mContext, applicationNotificationColorMap));

    }

    @Override
    public WatchInfomation getWatchInfomation() {
        WatchInfomation watchInfomation = new WatchInfomation();
        watchInfomation.setWatchID((byte)Preferences.getWatchId(mContext));
        watchInfomation.setWatchModel((byte)Preferences.getWatchModel(mContext));
        return watchInfomation;
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
                Packet packet = new Packet(packetsBuffer);
                //if packets invaild, discard them, and reset buffer
                if(!packet.isVaildPackets())
                {
                    Log.e("Nevo Error","InVaild Packets Received!");
                    packetsBuffer.clear();
                    QueuedMainThreadHandler.getInstance(QueuedMainThreadHandler.QueueType.SyncController).next();
                    return;
                }

                if((byte) SetRtcRequest.HEADER == nevoData.getRawData()[1])
                {
                    //setp2:start set user profile
                    sendRequest(new SetProfileRequest(mContext,((ApplicationModel)mContext).getNevoUser()));
                }
                else if((byte) SetProfileRequest.HEADER == nevoData.getRawData()[1])
                {
                    //step3:WriteSetting
                    sendRequest(new WriteSettingRequest(mContext));
                }
                else if((byte) WriteSettingRequest.HEADER == nevoData.getRawData()[1])
                {
                    //step4:SetCardio
                    sendRequest(new SetCardioRequest(mContext));
                }

                else if((byte) SetCardioRequest.HEADER == nevoData.getRawData()[1])
                {
                    EventBus.getDefault().post(new InitializeEvent(InitializeEvent.INITIALIZE_STATUS.END));
                    //start sync notification, phone --> nevo
                    // set Local Notification setting to Nevo, when nevo 's battery removed, the
                    // Steps count is 0, and all notification is off, because Notification is very
                    // important for user, so here need use local's setting sync with nevo
                    setNotification(true);
                    //here start sync every alarm
                    //firstly clean all alarms
                    for(int i=0;i<14;i++)
                    {
                        setAlarm(new Alarm(0,0,(byte)0,"",(byte)(i<7?0:1),(byte)i));
                    }
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date());
                    List<Alarm> alarmList = ((ApplicationModel)mContext).getAllAlarm();
                    for(Alarm alarm:alarmList) {
                        if((alarm.getWeekDay() & 0x80) == 0x80)
                        {
                            //NOTICE: here we only disable it in the watch, but keep its status(on or off) in the app.
                            if (alarm.getAlarmNumber() >= 7) {
                                //discard today 's sleep alarm when it comes in active mode (BT connected)
                                if ((alarm.getWeekDay() & 0x0F) == calendar.get(Calendar.DAY_OF_WEEK)
                                        && (alarm.getHour() < calendar.get(Calendar.HOUR_OF_DAY) || (alarm.getHour() == calendar.get(Calendar.HOUR_OF_DAY) && alarm.getMinute() < calendar.get(Calendar.MINUTE)))) {
                                    continue;
                                }
                                //discard yesterday 's sleep alarm when it comes in active mode (BT connected)
                                else if ((((alarm.getWeekDay() & 0x0F) + 1) == calendar.get(Calendar.DAY_OF_WEEK))
                                        || ((alarm.getWeekDay() & 0x0F) == 7 && calendar.get(Calendar.DAY_OF_WEEK) == 1)) {
                                    continue;
                                }
                            }
                            setAlarm(alarm);
                        }
                    }
                }
                else if((byte) SetNotificationRequest.HEADER == nevoData.getRawData()[1])
                {
                    if(initNotification)
                    {
                        syncActivityData();
                    }
                }
                else if((byte) ReadDailyTrackerInfoRequest.HEADER == nevoData.getRawData()[1])
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
                else if((byte) ReadDailyTrackerRequest.HEADER == nevoData.getRawData()[1])
                {
                    DailyTrackerPacket thispacket = new DailyTrackerPacket(packet.getPackets());

                    if(savedDailyHistory.isEmpty()) {
                        mCurrentDay = 0;
                        savedDailyHistory.add(mCurrentDay, new DailyHistory(thispacket.getDate()));
                    }
                    savedDailyHistory.get(mCurrentDay).setTotalSteps(thispacket.getDailySteps());
                    savedDailyHistory.get(mCurrentDay).setHourlySteps(thispacket.getHourlySteps());

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
                    steps.setDistanceGoal(thispacket.getDistanceGoal());
                    steps.setCaloriesGoal(thispacket.getCaloriesGoal());
                    steps.setActiveTimeGoal(thispacket.getActiveTimeGoal());
                    steps.setNoActivityTime(thispacket.getInactivityTime());
                    steps.setWalkSteps(thispacket.getDailyWalkSteps());
                    steps.setRunSteps(thispacket.getDailyRunSteps());
                    steps.setWalkDistance(thispacket.getDailyWalkDistance());
                    steps.setRunDistance(thispacket.getDailyRunDistance());
                    steps.setWalkDuration(thispacket.getDailyWalkDuration());
                    steps.setRunDuration(thispacket.getDailyRunDuration());
                    Log.i(savedDailyHistory.get(mCurrentDay).getDate().toString(), "total Steps:" + steps.getSteps());
                    Log.i(savedDailyHistory.get(mCurrentDay).getDate().toString(), "Hourly Steps:" + steps.getHourlySteps());
                    Log.i(savedDailyHistory.get(mCurrentDay).getDate().toString(),"total active time: " + (steps.getWalkDuration()+steps.getRunDuration()) + ",walk time: " + steps.getWalkDuration() + ",run time: "+ steps.getRunDuration());
                    Log.i(savedDailyHistory.get(mCurrentDay).getDate().toString(),"total distance: " + steps.getDistance() +",walk distance: " + steps.getWalkDistance() + ",run distance: " + steps.getRunDistance());
                    Log.i(savedDailyHistory.get(mCurrentDay).getDate().toString(),"total calories: " + steps.getCalories());
                    try {
                        steps.setRemarks(new JSONObject().put("date", new SimpleDateFormat("yyyy-MM-dd").format(new Date(steps.getDate()))).toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //update  the day 's "steps" table
                    //no matter what value the steps is,here must save it to table,for maintaining data consistency,for example, user reinstall battery or connect another watch,we should show lastest data to user
                    ((ApplicationModel) mContext).saveDailySteps(steps);
                    //no matter what the sleep is,here must save it to table,for maintaining data consistency
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

                    //here save solar time to local database when watch ID>1
                    if(getWatchInfomation().getWatchID()>1){
                        Solar solar = new Solar(new Date(history.getCreated()));
                        solar.setDate(Common.removeTimeFromDate(solar.getCreatedDate()));
                        solar.setUserId(Integer.parseInt(((ApplicationModel) mContext).getNevoUser().getNevoUserID()));
                        solar.setTotalHarvestingTime(thispacket.getTotalSwimTime());
                        solar.setHourlyHarvestingTime(thispacket.getHourlySwimTime().toString());
                        Log.i(savedDailyHistory.get(mCurrentDay).getDate().toString(), "hourly solar time:" + solar.getHourlyHarvestingTime());
                        Log.i(savedDailyHistory.get(mCurrentDay).getDate().toString(), "total solar time:" + solar.getTotalHarvestingTime());
                        ((ApplicationModel) mContext).getSolarDatabaseHelper().update(solar);
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
                        sendRequest(new TestModeRequest(mContext,0x3F0000,false, TestModeRequest.MODE_F0));
                    }
                }
                else if((byte) TestModeRequest.MODE_F4 == packet.getHeader())
                {
                    int battery_adc = HexUtils.bytesToInt(new byte[]{packet.getPackets().get(0).getRawData()[2],packet.getPackets().get(0).getRawData()[3]});
                    int pv_adc = HexUtils.bytesToInt(new byte[]{packet.getPackets().get(0).getRawData()[4],packet.getPackets().get(0).getRawData()[5]});
                    float battery_voltage = ((battery_adc+1) * 360f / 1024)/100;
                    float pv_voltage = ((pv_adc+1) * 360f / 1024)/100;
                    Log.i(TAG,"battery_adc= " + battery_adc + ",battery_voltage= " + battery_voltage + ",pv_adc= " +pv_adc+",pv_voltage= " + pv_voltage);
                    EventBus.getDefault().post(new SolarConvertEvent(pv_adc));
                }
                else if((byte) FindPhonePacket.HEADER == packet.getHeader())
                {
                    if(mLocalService!=null) {
                        mLocalService.findCellPhone();
                    }
                    //let all color LED light on, means that find CellPhone is successful.
                    sendRequest(new TestModeRequest(mContext,0x3F0000,false, TestModeRequest.MODE_F0));
                }
                else if((byte) GetStepsGoalRequest.HEADER == nevoData.getRawData()[1])
                {
                    if (!mEnableTestMode)
                    {
                        mEnableTestMode = true;
                        sendRequest(new TestModeRequest(mContext,0,false, TestModeRequest.MODE_F0));
                    }
                    //save current day's step count to "Steps" table
                    Steps steps = ((ApplicationModel) mContext).getDailySteps(((ApplicationModel) mContext).getNevoUser().getNevoUserID(), Common.removeTimeFromDate(new Date()));
                    steps.setCreatedDate(new Date().getTime());
                    steps.setDate(Common.removeTimeFromDate(new Date()).getTime());
                    DailyStepsPacket stepPacket = new DailyStepsPacket(packet.getPackets());
                    steps.setSteps(stepPacket.getDailySteps());
                    steps.setGoal(stepPacket.getDailyStepsGoal());
                    steps.setNevoUserID(((ApplicationModel) mContext).getNevoUser().getNevoUserID());
                    //I can't calculator these value from this packet, they should come from CMD 0x25 cmd
                    ((ApplicationModel)mContext).saveDailySteps(steps);
                    //end save
                }
                //process done(such as save local db), then notify top layer to get or refresh screen
                if (packet.getHeader() == (byte) GetStepsGoalRequest.HEADER) {
                    EventBus.getDefault().post(new LittleSyncEvent(true));
                }
                else if((byte) GetBatteryLevelRequest.HEADER == packet.getHeader()) {
                    EventBus.getDefault().post(new BatteryEvent(new Battery(new BatteryLevelPacket(packet.getPackets()).getBatteryLevel())));
                }
                else if((byte) 0xF0 == packet.getHeader() || FindWatchRequest.HEADER == packet.getHeader()) {
                    EventBus.getDefault().post(new FindWatchEvent(true));
                }
                else if((byte) SetAlarmRequest.HEADER == packet.getHeader()
                        || (byte) SetNotificationRequest.HEADER == packet.getHeader()
                        || (byte) SetGoalRequest.HEADER == packet.getHeader()) {
                    EventBus.getDefault().post(new RequestResponseEvent(true));
                }
                else if((byte) ReadWatchInfoRequest.HEADER == packet.getHeader()) {
                    WatchInfoPacket watchInfoPacket = new WatchInfoPacket(packet.getPackets());
                    //save watch infomation into preference
                    Preferences.setWatchId(mContext,watchInfoPacket.getWatchID());
                    Preferences.setWatchModel(mContext,watchInfoPacket.getWatchModel());
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
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    packetsBuffer.clear();
                    //early nevo firmware doesn't support 0x27 cmd(read watch info), so I put this cmd here
                    sendRequest(new ReadWatchInfoRequest(mContext));
                    if(mLocalService.getPairedWatch().notEmpty() && mLocalService.getPairedWatch().get().equals(connectionController.getSaveAddress()))
                    {
                        //step1:set RTC
                        Log.w("Karl","SET RTC only once: got paired firstly and got connected with it");
                        setRtc();
                        //here reset this watch to avoid next invoke setRTC
                        mLocalService.resetPairedWatch();
                    }
                    else {
                        //here ignore step1(setRTC),and directly do step2:set user profile
                        sendRequest(new SetProfileRequest(mContext,((ApplicationModel)mContext).getNevoUser()));
                    }
                }
            }, 2000);
        } else {
            QueuedMainThreadHandler.getInstance(QueuedMainThreadHandler.QueueType.SyncController).clear();
            packetsBuffer.clear();
            mSyncAllFlag = false;
            mCurrentDay = 0;
        }
        if(Preferences.getLinklossNotification(mContext))
        {
            LinklossNotificationUtils.sendNotification(mContext,stateChangedEvent.isConnected());
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
        sendRequest(new SetAlarmRequest(mContext,list));
    }

    @Override
    public void setAlarm(Alarm alarm) {
        sendRequest(new SetAlarmWithTypeRequest(mContext,alarm));
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
        //if user watch is old,use 0xF0 cmd to find watch
        if(getWatchInfomation().getWatchID()<=1) {
            sendRequest(new LedLightOnOffRequest(mContext, 0x3F0000, true));
        }else {
            //light on all color LED and start vibrator 2s for saving power consume
            sendRequest(new FindWatchRequest(mContext,true));
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    sendRequest(new LedLightOnOffRequest(mContext, 0x3F0000, false));
                }
            }, 2000);
        }
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
        connectionController.unPairDevice(connectionController.getSaveAddress());
        //step3:reset MAC address and firstly run flag and big sync stamp
        connectionController.forgetSavedAddress();
        getContext().getSharedPreferences(Constants.PREF_NAME, 0).edit().putBoolean(Constants.FIRST_FLAG,true).commit();
        //when forget the watch, force a big sync when got connected again
        getContext().getSharedPreferences(Constants.PREF_NAME, 0).edit().putLong(Constants.LAST_SYNC, 0).commit();
        //reset watch ID and watch model to nevo
        Preferences.setWatchId(mContext,1);
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
        private Optional<String> pairedBleAddress = new Optional<String>();
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
                else if(intent.getAction().equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    int connectState = device.getBondState();
                    Log.i("LocalService", "Ble pair state got changed:" + connectState + ",device:" + device.getAddress());
                    if (BluetoothDevice.BOND_BONDED == connectState) {
                        pairedBleAddress.set(device.getAddress());
                    }
                    else if (BluetoothDevice.BOND_NONE == connectState) {
                        pairedBleAddress.set(null);
                    }
                }
            }
        };

        @Override
        public void onCreate() {
            super.onCreate();
            registerReceiver(myReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
            registerReceiver(myReceiver, new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED));
            registerReceiver(myReceiver, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));
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
            public Optional<String> getPairedWatch() {
                return LocalService.this.pairedBleAddress;
            }
            public void resetPairedWatch() {
                LocalService.this.pairedBleAddress.set(null);
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
