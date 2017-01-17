package com.medcorp.application;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.IntentSender;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.FitnessStatusCodes;
import com.medcorp.R;
import com.medcorp.ble.controller.OtaControllerImpl;
import com.medcorp.ble.controller.SyncController;
import com.medcorp.ble.controller.SyncControllerImpl;
import com.medcorp.ble.model.color.LedLamp;
import com.medcorp.ble.model.goal.NumberOfStepsGoal;
import com.medcorp.cloud.CloudSyncManager;
import com.medcorp.cloud.validic.ValidicOperation;
import com.medcorp.database.entry.AlarmDatabaseHelper;
import com.medcorp.database.entry.GoalDatabaseHelper;
import com.medcorp.database.entry.LedLampDatabase;
import com.medcorp.database.entry.SleepDatabaseHelper;
import com.medcorp.database.entry.SolarDatabaseHelper;
import com.medcorp.database.entry.StepsDatabaseHelper;
import com.medcorp.database.entry.UserDatabaseHelper;
import com.medcorp.event.LocationChangedEvent;
import com.medcorp.event.SetSunriseAndSunsetTimeRequestEvent;
import com.medcorp.event.bluetooth.LittleSyncEvent;
import com.medcorp.event.bluetooth.OnSyncEvent;
import com.medcorp.event.bluetooth.PositionAddressChangeEvent;
import com.medcorp.event.google.api.GoogleApiClientConnectionFailedEvent;
import com.medcorp.event.google.api.GoogleApiClientConnectionSuspendedEvent;
import com.medcorp.event.google.fit.GoogleFitUpdateEvent;
import com.medcorp.event.med.MedAddRoutineRecordEvent;
import com.medcorp.event.med.MedAddSleepRecordEvent;
import com.medcorp.event.med.MedReadMoreRoutineRecordsModelEvent;
import com.medcorp.event.med.MedReadMoreSleepRecordsModelEvent;
import com.medcorp.event.validic.ValidicAddRoutineRecordEvent;
import com.medcorp.event.validic.ValidicAddSleepRecordEvent;
import com.medcorp.event.validic.ValidicCreateUserEvent;
import com.medcorp.event.validic.ValidicDeleteRoutineRecordEvent;
import com.medcorp.event.validic.ValidicDeleteSleepRecordModelEvent;
import com.medcorp.event.validic.ValidicException;
import com.medcorp.event.validic.ValidicReadMoreRoutineRecordsModelEvent;
import com.medcorp.event.validic.ValidicReadMoreSleepRecordsModelEvent;
import com.medcorp.event.validic.ValidicUpdateRoutineRecordsModelEvent;
import com.medcorp.googlefit.GoogleFitManager;
import com.medcorp.googlefit.GoogleFitStepsDataHandler;
import com.medcorp.googlefit.GoogleFitTaskCounter;
import com.medcorp.googlefit.GoogleHistoryUpdateTask;
import com.medcorp.location.LocationController;
import com.medcorp.model.Alarm;
import com.medcorp.model.Goal;
import com.medcorp.model.Sleep;
import com.medcorp.model.SleepData;
import com.medcorp.model.Solar;
import com.medcorp.model.Steps;
import com.medcorp.model.User;
import com.medcorp.network.listener.ResponseListener;
import com.medcorp.network.med.manager.MedManager;
import com.medcorp.network.med.model.MedRoutineRecordWithID;
import com.medcorp.network.med.model.MedSleepRecordWithID;
import com.medcorp.network.validic.model.ValidicReadMoreSleepRecordsModel;
import com.medcorp.network.validic.model.ValidicRoutineRecordModelBase;
import com.medcorp.network.validic.model.ValidicSleepRecordModelBase;
import com.medcorp.network.validic.model.ValidicUser;
import com.medcorp.util.CalendarWeekUtils;
import com.medcorp.util.Common;
import com.medcorp.util.Preferences;
import com.medcorp.view.ToastHelper;

import net.medcorp.library.ble.controller.OtaController;
import net.medcorp.library.ble.util.Optional;
import net.medcorp.library.worldclock.WorldClockDatabaseHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Karl on 10/15/15.
 */
public class ApplicationModel extends Application {


    /*

    BatteryLevelPacket
    DailyStepsPacket
    DailyTrackerInfoPacket
     */

    public final int GOOGLE_FIT_OATH_RESULT = 1001;
    private SyncController syncController;
    private OtaController otaController;
    private StepsDatabaseHelper stepsDatabaseHelper;
    private SleepDatabaseHelper sleepDatabaseHelper;
    private AlarmDatabaseHelper alarmDatabaseHelper;
    private GoalDatabaseHelper goalDatabaseHelper;
    private UserDatabaseHelper userDatabaseHelper;
    private SolarDatabaseHelper solarDatabaseHelper;
    private boolean firmwareUpdateAlertDailog = false;
    //if it is -1, means mcu version hasn't be read
    private int mcuFirmwareVersion = -1;
    private int bleFirmwareVersion = -1;
    private GoogleFitManager googleFitManager;
    private GoogleFitTaskCounter googleFitTaskCounter;
    private MedManager validicMedManager;
    private CloudSyncManager cloudSyncManager;
    private User nevoUser;
    private WorldClockDatabaseHelper worldClockDatabaseHelper;
    private LedLampDatabase ledDataBase;
    private LocationController locationController;
    private Address address;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        EventBus.getDefault().register(this);
        syncController = new SyncControllerImpl(this);
        otaController = new OtaControllerImpl(this);
        stepsDatabaseHelper = new StepsDatabaseHelper(this);
        sleepDatabaseHelper = new SleepDatabaseHelper(this);
        alarmDatabaseHelper = new AlarmDatabaseHelper(this);
        goalDatabaseHelper = new GoalDatabaseHelper(this);
        userDatabaseHelper = new UserDatabaseHelper(this);
        solarDatabaseHelper = new SolarDatabaseHelper(this);
        validicMedManager = new MedManager(this);
        cloudSyncManager = new CloudSyncManager(this);
        ledDataBase = new LedLampDatabase(this);
        locationController = new LocationController(this);

        worldClockDatabaseHelper = new WorldClockDatabaseHelper(this);
        worldClockDatabaseHelper.setupWorldClock();
        Optional<User> user = userDatabaseHelper.getLoginUser();
        if (Preferences.getisInitAlarm(this) && getAllAlarm().size() == 0) {
            Preferences.startInitAlarm(this, false);
            Alarm defAlarm;
            for (int i = 0; i < 2; i++) {
                if (i == 0) {
                    defAlarm = new Alarm(21, 0, (byte) (0), getString(R.string.def_alarm_one), (byte) 0, (byte) 7);
                } else {
                    defAlarm = new Alarm(8, 0, (byte) (0), getString(R.string.def_alarm_two), (byte) 1, (byte) 0);
                }
                addAlarm(defAlarm);
            }
        }
        if (user.isEmpty()) {
            nevoUser = new User(0);
            nevoUser.setNevoUserID("0");
            //"0" means anonymous user login
        } else {
            nevoUser = user.get();
        }
        updateGoogleFit();
    }

    @Subscribe
    public void onEvent(OnSyncEvent event) {
        if (event.getStatus() == OnSyncEvent.SYNC_EVENT.STOPPED) {
            updateGoogleFit();
            getCloudSyncManager().launchSyncWeekly(nevoUser, getNeedSyncSteps(nevoUser.getNevoUserID()), getNeedSyncSleep(nevoUser.getNevoUserID()));
        }
    }

    @Subscribe
    public void onEvent(LittleSyncEvent event) {
        if (event.isSuccess()) {
            final Steps steps = getDailySteps(nevoUser.getNevoUserID(), Common.removeTimeFromDate(new Date()));
            getCloudSyncManager().launchSyncDaily(nevoUser, steps);
        }
    }

    @Subscribe
    public void onEvent(SetSunriseAndSunsetTimeRequestEvent event) {
        if (event.getStatus() == SetSunriseAndSunsetTimeRequestEvent.STATUS.START) {
            getLocationController().startUpdateLocation();
        } else if (event.getStatus() == SetSunriseAndSunsetTimeRequestEvent.STATUS.SUCCESS) {
            getLocationController().stopLocation();
        } else if (event.getStatus() == SetSunriseAndSunsetTimeRequestEvent.STATUS.FAILED) {
            //TODO how to do it
            Log.w("ApplicationModel", "setSunriseAndSunset got failed.");
        }
    }

    public MedManager getNetworkManage() {
        return validicMedManager;
    }

    public WorldClockDatabaseHelper getWorldClockDatabaseHelper() {
        return worldClockDatabaseHelper;
    }

    public LocationController getLocationController() {
        return locationController;
    }

    public StepsDatabaseHelper getStepsHelper() {
        return stepsDatabaseHelper;
    }

    public SolarDatabaseHelper getSolarDatabaseHelper() {
        return solarDatabaseHelper;
    }

    public SyncController getSyncController() {
        return syncController;
    }

    public OtaController getOtaController() {
        return otaController;
    }

    public void startConnectToWatch(boolean forceScan) {
        syncController.startConnect(forceScan);
    }

    public boolean isWatchConnected() {
        return syncController.isConnected();
    }

    public void blinkWatch() {
        syncController.findDevice();
    }

    public void getBatteryLevelOfWatch() {
        syncController.getBatteryLevel();
    }

    public String getWatchSoftware() {
        return syncController.getSoftwareVersion();
    }

    public String getWatchFirmware() {
        return syncController.getFirmwareVersion();
    }

    public void setGoal(Goal goal) {
        syncController.setGoal(new NumberOfStepsGoal(goal.getSteps()));
    }

    public void setAlarm(List<Alarm> list) {
        syncController.setAlarm(list, false);
    }

    public void forgetDevice() {
        syncController.forgetDevice();
    }

    public List<Steps> getAllSteps() {
        return stepsDatabaseHelper.convertToNormalList(stepsDatabaseHelper.getAll(nevoUser.getNevoUserID()));
    }

    public List<Alarm> getAllAlarm() {
        return alarmDatabaseHelper.convertToNormalList(alarmDatabaseHelper.getAll());
    }

    public void saveNevoUser(User user) {
        userDatabaseHelper.update(user);
    }

    public void saveDailySteps(Steps steps) {
        stepsDatabaseHelper.update(steps);
    }

    public void removeUser(User user) {
        userDatabaseHelper.remove(user.getNevoUserID(), new Date(user.getCreatedDate()));
    }

    public List<Solar> getThisWeekSolar(String userId, Date date) {
        List<Solar> thisWeekSolar = new ArrayList<>();
        CalendarWeekUtils calendar = new CalendarWeekUtils(date);
        for (long start = calendar.getWeekStartDate().getTime(); start <=
                calendar.getWeekEndDate().getTime(); start += 24 * 60 * 60 * 1000L) {
            Optional<Solar> optional = solarDatabaseHelper.get(userId, new Date(start));
            if (optional.notEmpty()) {
                thisWeekSolar.add(optional.get());
            } else {
                thisWeekSolar.add(new Solar(new Date(start), new Date(start), getNevoUser().getId(), "", 0));
            }
        }
        return thisWeekSolar;
    }

    public List<Solar> getLastWeekSolar(String userId, Date date) {
        List<Solar> lastWeekSolar = new ArrayList<>();
        CalendarWeekUtils calendar = new CalendarWeekUtils(date);
        for (long start = calendar.getLastWeekStart().getTime(); start <=
                calendar.getLastWeekEnd().getTime(); start += 24 * 60 * 60 * 1000L) {
            Optional<Solar> optional = solarDatabaseHelper.get(userId, new Date(start));
            if (optional.notEmpty()) {
                lastWeekSolar.add(optional.get());
            } else {
                lastWeekSolar.add(new Solar(new Date(start), new Date(start), getNevoUser().getId(), "", 0));
            }
        }
        return lastWeekSolar;
    }

    public List<Solar> getLastMonthSolar(String userId, Date date) {
        List<Solar> lastMonthSolar = new ArrayList<>();
        CalendarWeekUtils calendar = new CalendarWeekUtils(date);
        for (long start = calendar.getMonthStartDate().getTime(); start <=
                date.getTime(); start += 24 * 60 * 60 * 1000L) {
            Optional<Solar> optional = solarDatabaseHelper.get(userId, new Date(start));
            if (optional.notEmpty()) {
                lastMonthSolar.add(optional.get());
            } else {
                lastMonthSolar.add(new Solar(new Date(start), new Date(start), getNevoUser().getId(), "", 0));
            }
        }
        return lastMonthSolar;
    }

    public List<SleepData> getThisWeekSleep(String userId, Date date) {

        List<SleepData> thisWeekSleep = new ArrayList<>();
        CalendarWeekUtils calendar = new CalendarWeekUtils(date);

        for (long start = calendar.getWeekStartDate().getTime(); start <=
                calendar.getWeekEndDate().getTime(); start += 24 * 60 * 60 * 1000L) {
            Optional<Sleep> todaySleep = sleepDatabaseHelper.get(userId, new Date(start));
            if (todaySleep.notEmpty()) {
                Sleep dailySleep = todaySleep.get();
                SleepData sleepData = new SleepData(dailySleep.getTotalDeepTime()
                        , dailySleep.getTotalLightTime(), dailySleep.getTotalWakeTime(),
                        start, dailySleep.getStart(), dailySleep.getEnd());
                thisWeekSleep.add(sleepData);
            } else {
                SleepData sleepData = new SleepData(0, 0, 0, start);
                thisWeekSleep.add(sleepData);
            }
        }
        return thisWeekSleep;
    }


    public List<SleepData> getLastWeekSleep(String userId, Date date) {
        List<SleepData> lastWeekSleep = new ArrayList<>(3);
        CalendarWeekUtils calendar = new CalendarWeekUtils(date);
        for (long start = calendar.getLastWeekStart().getTime(); start <=
                calendar.getLastWeekEnd().getTime(); start += 24 * 60 * 60 * 1000L) {

            Optional<Sleep> todaySleep = sleepDatabaseHelper.get(userId, new Date(start));
            if (todaySleep.notEmpty()) {
                Sleep dailySleep = todaySleep.get();
                SleepData sleepData = new SleepData(dailySleep.getTotalDeepTime()
                        , dailySleep.getTotalLightTime(), dailySleep.getTotalWakeTime(),
                        new DateTime(start).getMillis(), dailySleep.getStart(), dailySleep.getEnd());
                lastWeekSleep.add(sleepData);
            } else {
                SleepData sleepData = new SleepData(0, 0, 0, start, 0, 0);
                lastWeekSleep.add(sleepData);
            }
        }
        return lastWeekSleep;
    }

    public List<SleepData> getLastMonthSleep(String userId, Date date) {
        List<SleepData> lastMonth = new ArrayList<>(3);
        CalendarWeekUtils calendar = new CalendarWeekUtils(date);
        for (long start = calendar.getMonthStartDate().getTime(); start <=
                date.getTime(); start += 24 * 60 * 60 * 1000L) {
            Optional<Sleep> todaySleep = sleepDatabaseHelper.get(userId, new Date(start));
            if (todaySleep.notEmpty()) {
                Sleep dailySleep = todaySleep.get();
                SleepData sleepData = new SleepData(dailySleep.getTotalDeepTime()
                        , dailySleep.getTotalLightTime(), dailySleep.getTotalWakeTime(),
                        start, dailySleep.getStart(), dailySleep.getEnd());
                lastMonth.add(sleepData);
            } else {
                SleepData sleepData = new SleepData(0, 0, 0, start, 0, 0);
                lastMonth.add(sleepData);
            }
        }
        return lastMonth;
    }

    public List<Steps> getThisWeekSteps(String userId, Date date) {
        List<Steps> thisWeek = new ArrayList<>();
        CalendarWeekUtils calendar = new CalendarWeekUtils(date);
        for (long start = calendar.getWeekStartDate().getTime(); start <=
                calendar.getWeekEndDate().getTime(); start += 24 * 60 * 60 * 1000L) {
            thisWeek.add(getDailySteps(userId, new Date(start)));
        }
        return thisWeek;
    }

    public List<Steps> getLastWeekSteps(String userId, Date date) {
        List<Steps> lastWeekSteps = new ArrayList<>();
        CalendarWeekUtils calendar = new CalendarWeekUtils(date);
        for (long start = calendar.getLastWeekStart().getTime(); start <=
                calendar.getLastWeekEnd().getTime(); start += 24 * 60 * 60 * 1000L) {
            lastWeekSteps.add(getDailySteps(userId, new Date(start)));
        }
        return lastWeekSteps;
    }

    public List<Steps> getLastMonthSteps(String userId, Date date) {
        List<Steps> lastMonthSteps = new ArrayList<>();
        CalendarWeekUtils calendar = new CalendarWeekUtils(date);
        for (long start = calendar.getMonthStartDate().getTime(); start <=
                date.getTime(); start += 24 * 60 * 60 * 1000L) {
            lastMonthSteps.add(getDailySteps(userId, new Date(start)));
        }
        return lastMonthSteps;
    }


    public Steps getDailySteps(String userId, Date date) {
        Steps steps = null;
        Date dateStart = CalendarWeekUtils.getDayStartTime(date);
        Optional<Steps> step = stepsDatabaseHelper.get(userId, dateStart);
        if (step.notEmpty()) {
            steps = step.get();
        } else {
            steps = new Steps(date.getTime());
            steps.setDate(date.getTime());
            steps.setCreatedDate(date.getTime());
        }
        return steps;
    }

    public Sleep[] getDailySleep(String userId, Date todayDate) {
        Date yesterdayDate = new Date(todayDate.getTime() - 24 * 60 * 60 * 1000l);

        Optional<Sleep> todaySleep = sleepDatabaseHelper.get(userId, todayDate);
        Optional<Sleep> yesterdaySleep = sleepDatabaseHelper.get(userId, yesterdayDate);

        //use yesterday and today data to analysis sleep,pls refer to SleepDataHandler class
        if (yesterdaySleep.notEmpty() && todaySleep.notEmpty()) {
            return new Sleep[]{todaySleep.get(), yesterdaySleep.get()};
        }
        //use today data to analysis sleep
        if (todaySleep.notEmpty() && yesterdaySleep.isEmpty()) {
            return new Sleep[]{todaySleep.get()};
        }
        //use yesterday data (after 18:00) to analysis sleep
        if (yesterdaySleep.notEmpty() && todaySleep.isEmpty()) {
            return new Sleep[]{yesterdaySleep.get()};
        }
        //NO data sleep
        Sleep noDataSleep = new Sleep(todayDate.getTime());
        noDataSleep.setDate(Common.removeTimeFromDate(todayDate).getTime());
        return new Sleep[]{noDataSleep};
    }

    public void saveDailySleep(Sleep sleep) {
        sleepDatabaseHelper.update(sleep);
    }

    public List<Steps> getNeedSyncSteps(String userId) {
        return stepsDatabaseHelper.getNeedSyncSteps(userId);
    }

    public boolean isFoundInLocalSteps(int activity_id) {
        return stepsDatabaseHelper.isFoundInLocalSteps(activity_id);
    }

    public boolean isFoundInLocalSteps(Date date, String userID) {
        return stepsDatabaseHelper.isFoundInLocalSteps(date, userID);
    }

    public boolean isFoundInLocalSleep(int activity_id) {
        return sleepDatabaseHelper.isFoundInLocalSleep(activity_id);
    }

    public boolean isFoundInLocalSleep(Date date, String userID) {
        return sleepDatabaseHelper.isFoundInLocalSleep(date, userID);
    }

    public void saveStepsFromValidic(ValidicRoutineRecordModelBase routine) {
        Date createDate = Common.getLocalDateFromUTCTimestamp(routine.getTimestamp(), routine.getUtc_offset());

        Steps steps = new Steps(createDate.getTime());
        steps.setDate(Common.removeTimeFromDate(createDate).getTime());
        steps.setSteps((int) routine.getSteps());
        steps.setNevoUserID(getNevoUser().getNevoUserID());
        steps.setCloudRecordID(routine.get_id());
        steps.setiD(Integer.parseInt(routine.getActivity_id()));
        if (routine.getExtras() != null) {
            steps.setGoal(routine.getExtras().getGoal());
        } else {
            steps.setGoal(7000);
        }
        saveDailySteps(steps);
    }

    public void saveStepsFromMed(MedRoutineRecordWithID routine, Date createDate) {
        Steps steps = new Steps(createDate.getTime());
        steps.setDate(Common.removeTimeFromDate(createDate).getTime());
        try {
            JSONArray hourlyArray = new JSONArray(routine.getSteps());
            int totalSteps = 0;
            for (int i = 0; i < hourlyArray.length(); i++) {
                totalSteps += hourlyArray.optInt(i);
            }
            steps.setHourlySteps(routine.getSteps());
            steps.setSteps(totalSteps);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        steps.setDistance((int) routine.getDistance());
        steps.setCalories(routine.getCalories());
        steps.setWalkDuration(routine.getActive_time());
        steps.setNevoUserID(routine.getUid() + "");
        steps.setCloudRecordID(routine.getId() + "");
        steps.setGoal(10000);
        saveDailySteps(steps);
    }

    public void saveSleepFromValidic(ValidicSleepRecordModelBase validicSleepRecord) {
        Date createDate = Common.getLocalDateFromUTCTimestamp(validicSleepRecord.getTimestamp(), validicSleepRecord.getUtc_offset());

        Sleep sleep = new Sleep(createDate.getTime());
        sleep.setiD(Integer.parseInt(validicSleepRecord.getActivity_id()));
        sleep.setDate(Common.removeTimeFromDate(createDate).getTime());
        if (validicSleepRecord.getExtras() != null) {
            int lightSleep = 0;
            int deepSleep = 0;
            int wake = 0;
            List<Integer> hourlySleepList = new ArrayList<>();

            sleep.setHourlyWake(validicSleepRecord.getExtras().getHourlyWake());
            sleep.setHourlyLight(validicSleepRecord.getExtras().getHourlyLight());
            sleep.setHourlyDeep(validicSleepRecord.getExtras().getHourlyDeep());

            try {
                JSONArray hourlyWake = new JSONArray(sleep.getHourlyWake());
                for (int i = 0; i < hourlyWake.length(); i++) {
                    wake += Integer.parseInt(hourlyWake.getString(i));
                    hourlySleepList.add(Integer.parseInt(hourlyWake.getString(i)));
                }

                JSONArray hourlyLight = new JSONArray(sleep.getHourlyLight());
                for (int i = 0; i < hourlyLight.length(); i++) {
                    lightSleep += Integer.parseInt(hourlyLight.getString(i));
                    hourlySleepList.set(i, hourlySleepList.get(i) + Integer.parseInt(hourlyLight.getString(i)));
                }

                JSONArray hourlyDeep = new JSONArray(sleep.getHourlyDeep());
                for (int i = 0; i < hourlyDeep.length(); i++) {
                    deepSleep += Integer.parseInt(hourlyDeep.getString(i));
                    hourlySleepList.set(i, hourlySleepList.get(i) + Integer.parseInt(hourlyDeep.getString(i)));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            sleep.setHourlySleep(hourlySleepList.toString());
            sleep.setTotalSleepTime(wake + deepSleep + lightSleep);
            sleep.setTotalWakeTime(wake);
            sleep.setTotalLightTime(lightSleep);
            sleep.setTotalDeepTime(deepSleep);
        }
        //firstly reset sleep start/end time is 0, it means the day hasn't been calculate sleep analysis.
        sleep.setStart(0);
        sleep.setEnd(0);
        sleep.setNevoUserID(getNevoUser().getNevoUserID());
        sleep.setCloudRecordID(validicSleepRecord.get_id());
        try {
            sleep.setRemarks(new JSONObject().put("date", new SimpleDateFormat("yyyy-MM-dd").format(new Date(sleep.getDate()))).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        saveDailySleep(sleep);
    }

    public void saveSleepFromMed(MedSleepRecordWithID medSleepRecordWithID, Date createDate) {
        Sleep sleep = new Sleep(createDate.getTime());
        sleep.setDate(Common.removeTimeFromDate(createDate).getTime());

        sleep.setHourlyWake(medSleepRecordWithID.getWake_time());
        sleep.setHourlyLight(medSleepRecordWithID.getLight_sleep());
        sleep.setHourlyDeep(medSleepRecordWithID.getDeep_sleep());

        int lightSleep = 0;
        int deepSleep = 0;
        int wake = 0;
        List<Integer> hourlySleepList = new ArrayList<Integer>();
        try {
            JSONArray hourlyWake = new JSONArray(sleep.getHourlyWake());
            for (int i = 0; i < hourlyWake.length(); i++) {
                wake += Integer.parseInt(hourlyWake.getString(i));
                hourlySleepList.add(Integer.parseInt(hourlyWake.getString(i)));
            }

            JSONArray hourlyLight = new JSONArray(sleep.getHourlyLight());
            for (int i = 0; i < hourlyLight.length(); i++) {
                lightSleep += Integer.parseInt(hourlyLight.getString(i));
                hourlySleepList.set(i, hourlySleepList.get(i) + Integer.parseInt(hourlyLight.getString(i)));
            }

            JSONArray hourlyDeep = new JSONArray(sleep.getHourlyDeep());
            for (int i = 0; i < hourlyDeep.length(); i++) {
                deepSleep += Integer.parseInt(hourlyDeep.getString(i));
                hourlySleepList.set(i, hourlySleepList.get(i) + Integer.parseInt(hourlyDeep.getString(i)));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        sleep.setHourlySleep(hourlySleepList.toString());
        sleep.setTotalSleepTime(wake + deepSleep + lightSleep);
        sleep.setTotalWakeTime(wake);
        sleep.setTotalLightTime(lightSleep);
        sleep.setTotalDeepTime(deepSleep);
        sleep.setStart(0);
        sleep.setEnd(0);
        sleep.setNevoUserID(getNevoUser().getNevoUserID());
        //we must set CloudRecordID here, avoid doing sync repeatly
        sleep.setCloudRecordID(medSleepRecordWithID.getId() + "");
        try {
            sleep.setRemarks(new JSONObject().put("date", new SimpleDateFormat("yyyy-MM-dd").format(new Date(sleep.getDate()))).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        saveDailySleep(sleep);
    }

    public List<Sleep> getNeedSyncSleep(String userid) {
        return sleepDatabaseHelper.getNeedSyncSleep(userid);
    }

    public Alarm addAlarm(Alarm alarm) {
        return alarmDatabaseHelper.add(alarm).get();
    }

    public boolean updateAlarm(Alarm alarm) {
        return alarmDatabaseHelper.update(alarm);
    }

    public Alarm getAlarmById(int id) {
        return alarmDatabaseHelper.get(id).isEmpty() ? null : alarmDatabaseHelper.get(id).get(0).get();
    }

    public boolean deleteAlarm(Alarm alarm) {
        return alarmDatabaseHelper.remove(alarm.getId());
    }

    public List<Goal> getAllGoal() {
        return goalDatabaseHelper.convertToNormalList(goalDatabaseHelper.getAll());
    }

    public Goal addGoal(Goal goal) {
        return goalDatabaseHelper.add(goal).get();
    }

    public boolean updateGoal(Goal goal) {
        return goalDatabaseHelper.update(goal);
    }

    public Goal getGoalById(int id) {
        return goalDatabaseHelper.get(id).isEmpty() ? null : goalDatabaseHelper.get(id).get(0).get();
    }

    public boolean deleteAlarm(Goal goal) {
        return goalDatabaseHelper.remove(goal.getId());
    }

    /**
     * user LedLamp Database
     *
     * @return
     */
    public List<LedLamp> getAllLedLamp() {
        return ledDataBase.convertToNormalList(ledDataBase.getAll());
    }

    public LedLamp getSelectLamp(int rid) {
        return ledDataBase.get(rid).isEmpty() ? null : ledDataBase.get(rid).get(0).get();
    }

    public LedLamp addLedLamp(LedLamp ledLamp) {
        return ledDataBase.add(ledLamp).get();
    }

    public boolean upDataLedLamp(LedLamp ledLamp) {
        return ledDataBase.update(ledLamp);
    }

    public boolean removeLedLamp(int id) {
        return ledDataBase.remove(id);
    }

    public void getPositionLocal(final Location mLocation) {
        if (mLocation == null) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Geocoder ge = new Geocoder(ApplicationModel.this);
                    List<Address> addList = ge.getFromLocation(mLocation.getLatitude(), mLocation.getLongitude(), 1);
                    if (addList != null && addList.size() > 0) {
                        Preferences.saveLocation(ApplicationModel.this, addList.get(0));
                        EventBus.getDefault().post(new PositionAddressChangeEvent(addList.get(0)));
                        if (Preferences.getPositionCity(ApplicationModel.this) == null) {
                            Preferences.savePositionCountry(ApplicationModel.this, addList.get(0).getCountryName());
                            Preferences.savePositionCity(ApplicationModel.this, addList.get(0).getLocality());
                            Preferences.saveHomeCityCalender(ApplicationModel.this, Calendar.getInstance().getTimeZone().getID());
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public LedLamp getUserSelectLedLamp(int color) {
        LedLamp ledlamp = new LedLamp();
        ledlamp.setColor(getResources().getColor(R.color.red_normal));
        ledlamp.setName(getString(R.string.notification_def_name));

        List<LedLamp> allLedLamp = getAllLedLamp();
        for (LedLamp lamp : allLedLamp) {
            if (lamp.getColor() == color) {
                return lamp;
            }
        }
        return ledlamp;
    }

    public boolean isBluetoothOn() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter.isEnabled()) {
            return true;
        }
        return false;
    }

    public void initGoogleFit(AppCompatActivity appCompatActivity) {
        if (Preferences.isGoogleFitSet(this)) {
            googleFitTaskCounter = new GoogleFitTaskCounter(3);
            googleFitManager = new GoogleFitManager(this);
            if (appCompatActivity != null) {
                googleFitManager.setActivityForResults(appCompatActivity);
            }
            googleFitManager.connect();
        }
    }

    public void disconnectGoogleFit() {
        if (googleFitManager != null) {
            googleFitManager.disconnect();
        }
    }

    @Subscribe
    public void onEvent(LocationChangedEvent locationChangedEvent) {
        getPositionLocal(locationChangedEvent.getLocation());
    }

    @Subscribe
    public void onEvent(GoogleApiClientConnectionFailedEvent event) {
        if (event.getConnectionResult().getErrorCode() == ConnectionResult.SIGN_IN_REQUIRED ||
                event.getConnectionResult().getErrorCode() == FitnessStatusCodes.NEEDS_OAUTH_PERMISSIONS) {
            try {
                if (googleFitManager.getActivity() != null) {
                    event.getConnectionResult().startResolutionForResult(googleFitManager.getActivity(), GOOGLE_FIT_OATH_RESULT);
                }
            } catch (IntentSender.SendIntentException e) {
                ToastHelper.showShortToast(ApplicationModel.this, R.string.google_fit_could_not_login);
            }
        } else {
            ToastHelper.showShortToast(ApplicationModel.this, R.string.google_fit_connecting);
        }
    }

    @Subscribe
    public void onEvent(GoogleApiClientConnectionSuspendedEvent event) {
        if (event.getState() == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
            ToastHelper.showShortToast(ApplicationModel.this, R.string.google_fit_network_lost);
        } else if (event.getState() == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
            ToastHelper.showShortToast(ApplicationModel.this, R.string.google_fit_service_disconnected);
        } else {
            ToastHelper.showShortToast(ApplicationModel.this, R.string.google_fit_unknown_network);
        }
    }

    @Subscribe
    public void onEvent(GoogleFitUpdateEvent event) {
        if (event.isSuccess()) {
            googleFitTaskCounter.incrementSuccessAndFinish();
            if (googleFitTaskCounter.allSucces()) {
                ToastHelper.showLongToast(ApplicationModel.this, "Updated Google Fit");
                googleFitTaskCounter.reset();
            }
        } else {
            googleFitTaskCounter.incrementFinish();
            if (googleFitTaskCounter.areTasksDone()) {
                ToastHelper.showLongToast(ApplicationModel.this, "Couldn't updated Google Fit");
                googleFitTaskCounter.reset();
            }
        }
    }

    public void updateGoogleFit() {
        if (Preferences.isGoogleFitSet(this)) {
            initGoogleFit(null);
            GoogleFitStepsDataHandler dataHandler = new GoogleFitStepsDataHandler(getAllSteps(), ApplicationModel.this);
            new GoogleHistoryUpdateTask(googleFitManager).execute(dataHandler.getStepsDataSet());
            new GoogleHistoryUpdateTask(googleFitManager).execute(dataHandler.getCaloriesDataSet());
            new GoogleHistoryUpdateTask(googleFitManager).execute(dataHandler.getDistanceDataSet());
        }
    }

    public CloudSyncManager getCloudSyncManager() {
        return cloudSyncManager;
    }

    public LedLampDatabase getLedDataBase() {
        return ledDataBase;
    }

    public User getNevoUser() {
        return nevoUser;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public void createValidicUser(String pin, ResponseListener<ValidicUser> responseListener) {
        ValidicOperation.getInstance(this).createValidicUser(nevoUser, pin, responseListener);
    }


    @Subscribe
    public void onValidicAddRoutineRecordEvent(ValidicAddRoutineRecordEvent
                                                       validicAddRoutineRecordEvent) {
        saveDailySteps(validicAddRoutineRecordEvent.getSteps());

    }

    @Subscribe
    public void onValidicAddSleepRecordEvent(ValidicAddSleepRecordEvent
                                                     validicAddSleepRecordEvent) {
        saveDailySleep(validicAddSleepRecordEvent.getSleep());
    }

    @Subscribe
    public void onMedAddRoutineRecordEvent(MedAddRoutineRecordEvent medAddRoutineRecordEvent) {
        saveDailySteps(medAddRoutineRecordEvent.getSteps());

    }

    @Subscribe
    public void onMedAddSleepRecordEvent(MedAddSleepRecordEvent medAddSleepRecordEvent) {
        saveDailySleep(medAddSleepRecordEvent.getSleep());
    }

    @Subscribe
    public void onValidicCreateUserEvent(ValidicCreateUserEvent validicCreateUserEvent) {
        saveNevoUser(validicCreateUserEvent.getUser());
        getSyncController().getDailyTrackerInfo(true);
        getCloudSyncManager().launchSyncAll(nevoUser, getNeedSyncSteps(nevoUser.getNevoUserID()), getNeedSyncSleep(nevoUser.getNevoUserID()));
    }

    @Subscribe
    public void onValidicDeleteSleepRecordModelEvent(ValidicDeleteSleepRecordModelEvent
                                                             validicDeleteSleepRecordModelEvent) {
        sleepDatabaseHelper.remove(validicDeleteSleepRecordModelEvent.getUserId() + "", validicDeleteSleepRecordModelEvent.getDate());
    }

    @Subscribe
    public void onValidicException(ValidicException validicException) {
        Log.w("Karl", "Exception occured!");
        validicException.getException().printStackTrace();
    }

    @Subscribe
    public void onValidicReadMoreRoutineRecordsModelEvent
            (ValidicReadMoreRoutineRecordsModelEvent validicReadMoreRoutineRecordsModelEvent) {
        for (ValidicRoutineRecordModelBase routine : validicReadMoreRoutineRecordsModelEvent.getValidicReadMoreRoutineRecordsModel().getRoutine()) {
            int activity_id = Integer.parseInt(routine.getActivity_id());
            // if activity_id not exist in local Steps table, save it
            if (!isFoundInLocalSteps(activity_id)) {
                saveStepsFromValidic(routine);
            }
        }
    }

    @Subscribe
    public void onValidicReadMoreSleepRecordsModelEvent(ValidicReadMoreSleepRecordsModelEvent
                                                                validicReadMoreSleepRecordsModelEvent) {
        ValidicReadMoreSleepRecordsModel validicReadMoreSleepRecordsModel = validicReadMoreSleepRecordsModelEvent.getValidicReadMoreSleepRecordsModel();
        for (ValidicSleepRecordModelBase sleep : validicReadMoreSleepRecordsModel.getSleep()) {
            int activity_id = Integer.parseInt(sleep.getActivity_id());
            //if activity_id not exist in local Sleep table, save it
            if (isFoundInLocalSleep(activity_id)) {
                saveSleepFromValidic(sleep);
            }
        }
    }

    @Subscribe
    public void onMedReadMoreRoutineRecordsModelEvent(MedReadMoreRoutineRecordsModelEvent
                                                              medReadMoreRoutineRecordsModelEvent) {

        if (medReadMoreRoutineRecordsModelEvent.getMedReadMoreRoutineRecordsModel().getSteps() == null || medReadMoreRoutineRecordsModelEvent.getMedReadMoreRoutineRecordsModel().getSteps().length == 0) {
            return;
        }
        for (MedRoutineRecordWithID routine : medReadMoreRoutineRecordsModelEvent.getMedReadMoreRoutineRecordsModel().getSteps()) {
            try {
                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(routine.getDate().getDate());
                // if not exist in local Steps table, save it
                if (!isFoundInLocalSteps(date, routine.getUid() + "")) {
                    saveStepsFromMed(routine, date);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    @Subscribe
    public void onMedReadMoreSleepRecordsModelEvent(MedReadMoreSleepRecordsModelEvent
                                                            medReadMoreSleepRecordsModelEvent) {

        if (medReadMoreSleepRecordsModelEvent.getMedReadMoreSleepRecordsModel().getSleep() == null || medReadMoreSleepRecordsModelEvent.getMedReadMoreSleepRecordsModel().getSleep().length == 0) {
            return;
        }
        for (MedSleepRecordWithID sleep : medReadMoreSleepRecordsModelEvent.getMedReadMoreSleepRecordsModel().getSleep()) {
            try {
                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(sleep.getDate().getDate());
                // if not exist in local Sleep table, save it
                if (!isFoundInLocalSleep(date, sleep.getUid() + "")) {
                    saveSleepFromMed(sleep, date);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    @Subscribe
    public void onValidicUpdateRoutineRecordsModelEvent(ValidicUpdateRoutineRecordsModelEvent
                                                                validicUpdateRoutineRecordsModelEvent) {
        saveDailySteps(validicUpdateRoutineRecordsModelEvent.getSteps());

    }

    @Subscribe
    public void onValidicDeleteRoutineRecordEvent(ValidicDeleteRoutineRecordEvent
                                                          validicDeleteRoutineRecordEvent) {
        stepsDatabaseHelper.remove(validicDeleteRoutineRecordEvent.getUserId() + "", validicDeleteRoutineRecordEvent.getDate());
    }

}