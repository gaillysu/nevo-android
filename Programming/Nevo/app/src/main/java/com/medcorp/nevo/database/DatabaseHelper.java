package com.medcorp.nevo.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.medcorp.nevo.R;
import com.medcorp.nevo.model.DailySleep;
import com.medcorp.nevo.ble.util.QueuedMainThreadHandler;
import com.medcorp.nevo.model.DailyHistory;
import com.medcorp.nevo.model.DailySteps;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by gaillysu on 15/8/11.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "nevowatch.db";
    private static final int DATABASE_VERSION = 1;

    private  Dao<IDailyHistory,Integer> mDailyhistoryDao = null;
    private  Dao<User,Integer> mUserDao = null;
    private  Dao<Sleep,Integer> mSleepDao = null;
    private  Dao<Steps,Integer> mStepsDao = null;
    private  Dao<Heartbeat,Integer> mHeartbeatDao = null;

    //Classic singleton
    private static DatabaseHelper sInstance = null;
    public static DatabaseHelper getInstance(Context ctx) {
        if(null == sInstance )
        {
            sInstance = new DatabaseHelper(ctx);
        }
        return sInstance;
    }
    //END - Classic singleton

    /**
     * Please use getInstance instead.
     * Because it is much safer to have only one instance of OpenHelper
     * But I couldn't just put this constructor private since it is a requirement of OrmLiteSqlite
     * @param  context
     */
    @Deprecated
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
    }

    @Override
    public void onCreate(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource) {
        try {

            TableUtils.createTable(connectionSource, IDailyHistory.class);
            TableUtils.createTable(connectionSource,User.class);
            TableUtils.createTable(connectionSource, Sleep.class);
            TableUtils.createTable(connectionSource, Steps.class);
            TableUtils.createTable(connectionSource, Heartbeat.class);

        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Unable to create datbases", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource, int oldVer, int newVer) {
        try {

            TableUtils.dropTable(connectionSource, IDailyHistory.class, true);
            TableUtils.dropTable(connectionSource, User.class, true);
            TableUtils.dropTable(connectionSource, Sleep.class, true);
            TableUtils.dropTable(connectionSource, Steps.class, true);
            TableUtils.dropTable(connectionSource, Heartbeat.class, true);

            onCreate(sqliteDatabase, connectionSource);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Unable to upgrade database from version " + oldVer + " to new "
                    + newVer, e);
        }
    }

    public Dao<IDailyHistory, Integer> getDailyHistoryDao() throws SQLException {
        if (mDailyhistoryDao == null)
            mDailyhistoryDao = getDao(IDailyHistory.class);
        return mDailyhistoryDao;
    }

    public Dao<User, Integer> getUserDao() throws SQLException {
        if (mUserDao == null)
            mUserDao = getDao(User.class);

        return mUserDao;
    }

    public Dao<Sleep, Integer> getSleepDao() throws SQLException {
        if (mSleepDao == null)
            mSleepDao = getDao(Sleep.class);

        return mSleepDao;
    }

    public Dao<Steps, Integer> getStepsDao() throws SQLException {
        if (mStepsDao == null)
            mStepsDao = getDao(Steps.class);

        return mStepsDao;
    }

    public Dao<Heartbeat, Integer> getHeartbeatDao() throws SQLException {
        if (mHeartbeatDao == null)
            mHeartbeatDao = getDao(Heartbeat.class);

        return mHeartbeatDao;
    }

    /**
     * create or update one record.
     * @param dailyhistory
     * @throws SQLException
     */
    public void SaveDailyHistory(final IDailyHistory dailyhistory) throws SQLException {

        QueuedMainThreadHandler.getInstance(QueuedMainThreadHandler.QueueType.LocalDatabase).post(new Runnable() {
            @Override
            public void run() {

                List <IDailyHistory> list = null;
                try {
                    list = getDailyHistoryDao().queryBuilder().orderBy("created", true).where().eq("created",dailyhistory.getCreated()).query();

                    if(list!=null && list.size()>0) {
                        dailyhistory.setTrainingID(list.get(0).getTrainingID());
                        getDailyHistoryDao().update(dailyhistory);
                    }
                    else {
                        getDailyHistoryDao().create(dailyhistory);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    try {
                        throw e;
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                }finally {
                    QueuedMainThreadHandler.getInstance(QueuedMainThreadHandler.QueueType.LocalDatabase).next();
                }
            }
        });
    }

    public static void outPutDatabase(Context context)
    {
        Log.v("DBTEST", "START");
        File dbOut = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/nevowatch.db");
        File dbIn = new File("/data/data/"+context.getPackageName()+"/databases/nevowatch.db");
        try {
            FileInputStream in = new FileInputStream(dbIn);
            FileOutputStream out = new FileOutputStream(dbOut);
            byte[] buffer = new byte[1024];
            int read;
            while((read = in.read(buffer)) != -1){
                out.write(buffer, 0, read);
            }
            in.close();
            out.flush();
            out.close();
            Log.v("DBTEST", "end successfully,"+dbOut.getAbsolutePath());
        } catch (FileNotFoundException e) {
            Log.v("DBTEST", e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.v("DBTEST", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     *
     * @param string: [0, 2 ,3, 60, 23], start with '[', end with ']'
     * @return int[]{0,2,3,60,23}
     */
    public static int[] string2IntArray(String string)
    {
        //Log.i("DatabaseHelper","string2IntArray: "+string);
        String s = string;
        if(string.startsWith("[") && string.endsWith("]"))
            s = string.substring(1,string.length()-1);
        else return new int[0];
        String[] temp = s.split(",");
        int[] ret = new int[temp.length];
        for(int i=0;i<temp.length;i++) ret[i] =  Integer.parseInt(temp[i].trim());
        return ret;
    }

    /**
     *
     * @param date: a date for some day
     * @return the day 's sleep analysis result
     * include sleep start/end time, noon sleep time
     */
    public JSONObject getSleepZone(Date date)
    {
        JSONObject json = new JSONObject();
        List<Long> days = new ArrayList<Long>();

        //trim date 's HHMMSS, set the day starts from 00:00:00 (database field require so)
        Calendar calBeginning = new GregorianCalendar();
        calBeginning.setTime(date);
        calBeginning.set(Calendar.HOUR_OF_DAY, 0);
        calBeginning.set(Calendar.MINUTE, 0);
        calBeginning.set(Calendar.SECOND, 0);
        calBeginning.set(Calendar.MILLISECOND, 0);
        Date today = calBeginning.getTime();

        long start = today.getTime();     //this day
        long end =  start - 24*60*60*1000;// last day
        days.add(start);
        days.add(end);

        int i=0;
        int[] hourlySleepTime;
        int[] hourlyWakeTime;
        int[] hourlyLightTime;
        int[] hourlyDeepTime;

        int[] hourlySleepTime2;
        int[] hourlyWakeTime2;
        int[] hourlyLightTime2;
        int[] hourlyDeepTime2;

        List<Integer> mergeWakeTime = new ArrayList<Integer>();
        List<Integer> mergeLightTime = new ArrayList<Integer>();
        List<Integer> mergeDeepTime = new ArrayList<Integer>();

        int offset = 0;
        long sleepstart =0;
        long sleepend =0;

        long reststart = 0;
        long restend = 0;

        int theDayCount = 0;

        try {
            //List<IDailyHistory> list = DatabaseHelper.getInstance(mCtx).getDailyHistoryDao().queryBuilder().orderBy("created", false).where().le("created",start).and().ge("created", end).query();
            List <IDailyHistory> list = getDailyHistoryDao().queryBuilder().orderBy("created", false).where().in("created", days).query();

            if(list.size()==1){
                //only the today's sleep [0~23],such as the first record in the datebase
                if(list.get(0).getCreated()==start)
                {
                    int m =0,n =0;

                    hourlySleepTime = string2IntArray(list.get(0).getHourlySleepTime());
                    hourlyWakeTime = string2IntArray(list.get(0).getHourlyWakeTime());
                    hourlyLightTime = string2IntArray(list.get(0).getHourlyLightTime());
                    hourlyDeepTime = string2IntArray(list.get(0).getHourlDeepTime());

                    for(i=0;i<hourlySleepTime.length;i++)
                    {
                        if(hourlySleepTime[i]>0) break;
                    }
                    //find out
                    if(i!=hourlySleepTime.length) {
                        m = i;
                        sleepstart = start + ((i + 1) * 60 - hourlySleepTime[i]) * 60 * 1000;

                        n = hourlySleepTime.length -1;
                        for(i=m+1;i<hourlySleepTime.length;i++)
                        {
                            //find out the new end index 'n'
                            if(hourlySleepTime[i]==0) {n = i - 1;break;}
                        }
                        if(m == n)
                            sleepend = sleepstart + hourlySleepTime[n] * 60 * 1000;
                        else {
                            sleepend = start + (n * 60 + hourlySleepTime[n]) * 60 * 1000;
                        }

                        for(int k=m;k<=n;k++)
                        {
                            mergeWakeTime.add(hourlyWakeTime[k]);
                            mergeLightTime.add(hourlyLightTime[k]);
                            mergeDeepTime.add(hourlyDeepTime[k]);
                        }
                    }
                }
                //only yesterday's sleep [20~23],perhaps no sync today sleep[0~12]
                else if(list.get(0).getCreated()==end)
                {
                    int m =0,n =0;

                    hourlySleepTime = string2IntArray(list.get(0).getHourlySleepTime());
                    hourlyWakeTime = string2IntArray(list.get(0).getHourlyWakeTime());
                    hourlyLightTime = string2IntArray(list.get(0).getHourlyLightTime());
                    hourlyDeepTime = string2IntArray(list.get(0).getHourlDeepTime());

                    offset = 0;

                    for (i = hourlySleepTime.length - 1; i >= 20; i--) {
                        if (hourlySleepTime[i] == 0) break;
                        else{
                            //if sleep is broken at someone hour, this hour sleep time>0 and <60
                            offset += 60;//hourlySleepTime[i];
                            mergeWakeTime.add(0,hourlyWakeTime[i]);
                            mergeLightTime.add(0,hourlyLightTime[i]);
                            mergeDeepTime.add(0,hourlyDeepTime[i]);
                        }
                    }
                    //find out
                    if(offset>0)
                    {
                        offset = offset - (60-hourlySleepTime[i+1]);
                        sleepend = start; // this day
                        sleepstart = start - offset * 60 * 1000; // yesterday
                    }
                }
            }
            //today and yesterday all got sync.
            else if(list.size()==2)
            {
                //check today firstly, calculator sleep end time
                hourlySleepTime = string2IntArray(list.get(0).getHourlySleepTime());
                hourlyWakeTime = string2IntArray(list.get(0).getHourlyWakeTime());
                hourlyLightTime = string2IntArray(list.get(0).getHourlyLightTime());
                hourlyDeepTime = string2IntArray(list.get(0).getHourlDeepTime());

                offset = 0;
                for(i=0;i<hourlySleepTime.length;i++)
                {
                    //if today 's 00:00 has no sleep, should check yesterday's sleep data[20:00~23:00]
                    //if these yesterday sleep data also is zero, should recalculator today's sleep data
                    //for example, I like stay up late, always go to sleep after 1:00, this case shouldn't discard.
                    //so I add some new code to fix this case.
                    if(hourlySleepTime[i]==0) break;
                    else
                    {
                        //if sleep is broken at someone hour, this hour sleep time>0 and <60
                        offset += 60;//hourlySleepTime[i];
                        mergeWakeTime.add(hourlyWakeTime[i]);
                        mergeLightTime.add(hourlyLightTime[i]);
                        mergeDeepTime.add(hourlyDeepTime[i]);
                    }
                }
                if(i==0) {
                    //today perhaps has no sleep, only calculator yesterday
                    sleepend = start;
                }
                else if(i>14)
                {
                    //full day have sleep, means more times start/end sleep tracking
                    //MAX 14 hrs , when > 14hrs, nevo will stop sleep tracking
                    sleepend = start + 14*60*60*1000;
                }else {
                    offset = offset - (60-hourlySleepTime[i-1]);
                    sleepend = start+ offset * 60 *1000;
                }

                //check yesterday, calculator sleep start time
                hourlySleepTime2 = string2IntArray(list.get(1).getHourlySleepTime());
                hourlyWakeTime2 = string2IntArray(list.get(1).getHourlyWakeTime());
                hourlyLightTime2 = string2IntArray(list.get(1).getHourlyLightTime());
                hourlyDeepTime2 = string2IntArray(list.get(1).getHourlDeepTime());

                //only calculator yesterday 20~23
                offset = 0;
                for(i=hourlySleepTime2.length-1;i>=20;i--)
                {
                    if(hourlySleepTime2[i]==0) break;
                    else
                    {
                        //if sleep is broken at someone hour, this hour sleep time>0 and <60
                        offset += 60;//hourlySleepTime2[i];
                        mergeWakeTime.add(0,hourlyWakeTime2[i]);
                        mergeLightTime.add(0,hourlyLightTime2[i]);
                        mergeDeepTime.add(0,hourlyDeepTime2[i]);
                    }
                }
                //find out
                if(offset>0)
                {
                    offset = offset - (60-hourlySleepTime2[i+1]);
                    sleepstart = start - offset * 60 *1000;
                }
                else
                {
                    //no yesterday's sleep data, no use it
                    sleepstart = start;
                    //NEW CODE for stay up late.
                    //if also no today's sleep data, recalculator today 's sleep data, such as today's sleep from 1:00~10:00
                    if(sleepend == start)
                    {
                        int m =0,n =0;
                        for(i=0;i<hourlySleepTime.length;i++)
                        {
                            if(hourlySleepTime[i]>0) break;
                        }
                        //find out
                        if(i!=hourlySleepTime.length) {
                            m = i;
                            sleepstart = start + ((i + 1) * 60 - hourlySleepTime[i]) * 60 * 1000;

                            n = hourlySleepTime.length -1;
                            for(i=m+1;i<hourlySleepTime.length;i++)
                            {
                                //find out the new end index 'n'
                                if(hourlySleepTime[i]==0) {n = i - 1;break;}
                            }
                            if(m == n)
                                sleepend = sleepstart + hourlySleepTime[n] * 60 * 1000;
                            else {
                                sleepend = start + (n * 60 + hourlySleepTime[n]) * 60 * 1000;
                            }

                            for(int k=m;k<=n;k++)
                            {
                                mergeWakeTime.add(hourlyWakeTime[k]);
                                mergeLightTime.add(hourlyLightTime[k]);
                                mergeDeepTime.add(hourlyDeepTime[k]);
                            }
                        }

                    }
                }


                //update database for this day 's sleep duration,
                //use it for showing or calculator weekly/monthly sleep data.
                //here perhaps you fell intresting your noon sleep, such as at 12:00 ~ 14:00
                //not full zero at 12:00~14:00, means has noon short sleep.
                //good sleep, you can see the noon sleep duration.
                if(hourlySleepTime[12]>0 || hourlySleepTime[13]>0) {
                    restend = start + (13 * 60 + hourlySleepTime[13]) * 60 * 1000;
                    reststart = start + (13 * 60 - hourlySleepTime[12]) * 60 * 1000;
                    list.get(0).setReststartDateTime(reststart);
                    list.get(0).setRestendDateTime(restend);

                    json.put("startRestDateTime",reststart);
                    json.put("endRestDateTime",restend);
                }
                //end calculator noon sleep
            }
            json.put("startDateTime",sleepstart);
            json.put("endDateTime",sleepend);
            json.put("mergeHourlyWakeTime",mergeWakeTime.toString());
            json.put("mergeHourlyLightTime",mergeLightTime.toString());
            json.put("mergeHourlyDeepTime",mergeDeepTime.toString());

            //update to database
            if(list.size()>0){
                list.get(0).setStartDateTime(sleepstart);
                list.get(0).setEndDateTime(sleepend);
                SaveDailyHistory(list.get(0));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     *@param thedate: one day
     * @return the given date‘s daily record or null
     */
    public DailyHistory getDailyHistory(Date thedate)
    {
        List<Long> days = new ArrayList<Long>();
        //set theDay from 00:00:00
        Calendar calBeginning = new GregorianCalendar();
        calBeginning.setTime(thedate);
        calBeginning.set(Calendar.HOUR_OF_DAY, 0);
        calBeginning.set(Calendar.MINUTE, 0);
        calBeginning.set(Calendar.SECOND, 0);
        calBeginning.set(Calendar.MILLISECOND, 0);
        Date theday = calBeginning.getTime();
        days.add(theday.getTime());
        try {
            List<IDailyHistory> history = getDailyHistoryDao().queryBuilder().orderBy("created", false).where().in("created",days).query();
            if(!history.isEmpty()) return history.get(0).getDailyHistory();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new DailyHistory(new Date());
    }

    /**
     *
     * @return all records of daily history, ordered by date ascending, such as Oct 18,19...
     */
    public List<DailyHistory> getAllDailyHistory()
    {
        List<DailyHistory> dailyHistories = new ArrayList<DailyHistory>();
        try {
            List<IDailyHistory> iDailyHistories = getDailyHistoryDao().queryBuilder().orderBy("created", true).query();
            for (IDailyHistory iDailyHistory: iDailyHistories) {
                dailyHistories.add(iDailyHistory.getDailyHistory());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<DailyHistory>();
    }

    public DailySteps getSteps(Date date){
        DailyHistory history = getDailyHistory(date);
        return new DailySteps(history);
    }

    public List<DailySteps> getAllSteps(){
        List <DailyHistory> dailyHistories = getAllDailyHistory();
        List<DailySteps> dailySteps = new ArrayList<DailySteps>();
        for (DailyHistory history: dailyHistories) {
            dailySteps.add(new DailySteps(history));
        }
        return dailySteps;
    }

    public DailySleep getSleep(Date date){
        DailyHistory history = getDailyHistory(date);
        return new DailySleep(history);
    }

    public List<DailySleep> getAllSleep(){
        List <DailyHistory> dailyHistories = getAllDailyHistory();
        List<DailySleep> dailySleeps = new ArrayList<DailySleep>();
        for (DailyHistory history: dailyHistories) {
            dailySleeps.add(new DailySleep(history));
        }
        return dailySleeps;
    }
 }
