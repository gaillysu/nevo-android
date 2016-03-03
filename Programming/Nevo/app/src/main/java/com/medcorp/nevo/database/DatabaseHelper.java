package com.medcorp.nevo.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.medcorp.nevo.R;
import com.medcorp.nevo.database.dao.AlarmDAO;
import com.medcorp.nevo.database.dao.GoalDAO;
import com.medcorp.nevo.database.dao.HeartbeatDAO;
import com.medcorp.nevo.database.dao.IDailyHistory;
import com.medcorp.nevo.database.dao.SleepDAO;
import com.medcorp.nevo.database.dao.StepsDAO;
import com.medcorp.nevo.database.dao.UserDAO;

import java.sql.SQLException;

/**
 * Created by gaillysu on 15/8/11.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "nevowatch.db";
    private static final int DATABASE_VERSION = 4; // from 3 to 4, refactor table "Preset" to "Goal"

    private  Dao<IDailyHistory,Integer> dailyhistoryDao = null;
    private  Dao<UserDAO,Integer> userDao = null;
    private  Dao<SleepDAO,Integer> sleepDao = null;
    private  Dao<StepsDAO,Integer> stepsDao = null;
    private  Dao<HeartbeatDAO,Integer> heartbeatDAO = null;
    private  Dao<AlarmDAO,Integer> alarmDao = null;
    private  Dao<GoalDAO,Integer> goalsDAO = null;
    private  Context context;

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
        this.context = context;
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
            TableUtils.createTable(connectionSource, GoalDAO.class);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Unable to create datbases", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource, int oldVer, int newVer) {
        try {
            //when user update app from v2.0.1(database is v3) to new version (database >= v4)
            //here create new table "Goal" and add some default records, the best way is copy data from  table "Preset" to "Goal",but now I can't read the table "Preset"
            //due to the PresetDAO class has been renamed to GoalDAO class.
            if(oldVer == 3 && newVer >=4)
            {
                TableUtils.createTable(connectionSource, GoalDAO.class);
                getGoalDao().createIfNotExists(new GoalDAO(context.getString(R.string.startup_goal_light), true, 7000));
                getGoalDao().createIfNotExists(new GoalDAO(context.getString(R.string.startup_goal_moderate), true, 10000));
                getGoalDao().createIfNotExists(new GoalDAO(context.getString(R.string.startup_goal_heavy), true, 20000));
                //here return for keep user history data.
                return;
            }
            TableUtils.dropTable(connectionSource, IDailyHistory.class, true);
            TableUtils.dropTable(connectionSource, UserDAO.class, true);
            TableUtils.dropTable(connectionSource, SleepDAO.class, true);
            TableUtils.dropTable(connectionSource, StepsDAO.class, true);
            TableUtils.dropTable(connectionSource, HeartbeatDAO.class, true);
            TableUtils.dropTable(connectionSource, AlarmDAO.class, true);
            TableUtils.dropTable(connectionSource, GoalDAO.class, true);
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

    public Dao<GoalDAO, Integer> getGoalDao() throws SQLException {
        if (goalsDAO == null)
            goalsDAO = getDao(GoalDAO.class);
        return goalsDAO;
    }
}
