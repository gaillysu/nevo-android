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
import com.medcorp.nevo.database.dao.AlarmDAO;
import com.medcorp.nevo.database.dao.HeartbeatDAO;
import com.medcorp.nevo.database.dao.IDailyHistory;
import com.medcorp.nevo.database.dao.PresetDAO;
import com.medcorp.nevo.database.dao.SleepDAO;
import com.medcorp.nevo.database.dao.StepsDAO;
import com.medcorp.nevo.database.dao.UserDAO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by gaillysu on 15/8/11.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "nevowatch.db";
    private static final int DATABASE_VERSION = 3;

    private  Dao<IDailyHistory,Integer> dailyhistoryDao = null;
    private  Dao<UserDAO,Integer> userDao = null;
    private  Dao<SleepDAO,Integer> sleepDao = null;
    private  Dao<StepsDAO,Integer> stepsDao = null;
    private  Dao<HeartbeatDAO,Integer> heartbeatDAO = null;
    private  Dao<AlarmDAO,Integer> alarmDao = null;
    private  Dao<PresetDAO,Integer> presetsDAO = null;


    //Classic singleton
    private static DatabaseHelper instance = null;

    public static DatabaseHelper getInstance(Context ctx) {
        if(null == instance )
        {
            instance = new DatabaseHelper(ctx);
        }
        return instance;
    }
    //END - Classic singleton

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
    }

    @Override
    public void onCreate(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, IDailyHistory.class);
            TableUtils.createTable(connectionSource, UserDAO.class);
            TableUtils.createTable(connectionSource, SleepDAO.class);
            TableUtils.createTable(connectionSource, StepsDAO.class);
            TableUtils.createTable(connectionSource, HeartbeatDAO.class);
            TableUtils.createTable(connectionSource, AlarmDAO.class);
            TableUtils.createTable(connectionSource, PresetDAO.class);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Unable to create datbases", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource, int oldVer, int newVer) {
        try {
            TableUtils.dropTable(connectionSource, IDailyHistory.class, true);
            TableUtils.dropTable(connectionSource, UserDAO.class, true);
            TableUtils.dropTable(connectionSource, SleepDAO.class, true);
            TableUtils.dropTable(connectionSource, StepsDAO.class, true);
            TableUtils.dropTable(connectionSource, HeartbeatDAO.class, true);
            TableUtils.dropTable(connectionSource, AlarmDAO.class, true);
            TableUtils.dropTable(connectionSource, PresetDAO.class, true);
            onCreate(sqliteDatabase, connectionSource);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Unable to upgrade database from version " + oldVer + " to new "
                    + newVer, e);
        }
    }

    public Dao<UserDAO, Integer> getUserDao() throws SQLException {
        if (userDao == null)
            userDao = getDao(UserDAO.class);

        return userDao;
    }

    public Dao<SleepDAO, Integer> getSleepDao() throws SQLException {
        if (sleepDao == null)
            sleepDao = getDao(SleepDAO.class);

        return sleepDao;
    }

    public Dao<StepsDAO, Integer> getStepsDao() throws SQLException {
        if (stepsDao == null)
            stepsDao = getDao(StepsDAO.class);

        return stepsDao;
    }

    public Dao<HeartbeatDAO, Integer> getHeartbeatDao() throws SQLException {
        if (heartbeatDAO == null)
            heartbeatDAO = getDao(HeartbeatDAO.class);
        return heartbeatDAO;
    }

    public Dao<AlarmDAO, Integer> getAlarmDao() throws SQLException {
        if (alarmDao== null)
            alarmDao= getDao(AlarmDAO.class);
        return alarmDao;
    }

    public Dao<PresetDAO, Integer> getPresetDao() throws SQLException {
        if (presetsDAO== null)
            presetsDAO= getDao(PresetDAO.class);
        return presetsDAO;
    }

    public static void outPutDatabase(Context context)
    {
        //TODO best to put into config.xml
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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param string: [0, 2 ,3, 60, 23], start with '[', end with ']'
     * @return int[]{0,2,3,60,23}
     */
    //TODO wtf is this?
    public static int[] string2IntArray(String string)
    {
        String s = string;
        if(string.startsWith("[") && string.endsWith("]"))
            s = string.substring(1,string.length()-1);
        else return new int[0];
        String[] temp = s.split(",");
        int[] ret = new int[temp.length];
        for(int i=0;i<temp.length;i++) ret[i] =  Integer.parseInt(temp[i].trim());
        return ret;
    }
}
