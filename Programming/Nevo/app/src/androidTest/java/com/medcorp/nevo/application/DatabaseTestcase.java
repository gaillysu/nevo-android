package com.medcorp.nevo.application;

import android.test.AndroidTestCase;
import android.util.Log;

import com.medcorp.nevo.database.entry.SleepDatabaseHelper;
import com.medcorp.nevo.database.entry.StepsDatabaseHelper;
import com.medcorp.nevo.database.entry.UserDatabaseHelper;
import com.medcorp.nevo.model.Sleep;
import com.medcorp.nevo.model.Steps;
import com.medcorp.nevo.model.User;

import junit.framework.Assert;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by gaillysu on 15/11/27.
 */
public class DatabaseTestcase extends AndroidTestCase {
    private static final String TAG = "DatabaseTestcase";

    private UserDatabaseHelper userDB;
    private StepsDatabaseHelper  stepsDB;
    private SleepDatabaseHelper sleepDB;

    //every login, simlator different user by id
    private User user;

    //sample data:
    final int age = 20;
    final int totalstep = 1001;
    final int totalsleep = 443;


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        userDB = new UserDatabaseHelper(getContext());
        stepsDB = new StepsDatabaseHelper(getContext());
        sleepDB = new SleepDatabaseHelper(getContext());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    @Override
    public void testAndroidTestCaseSetupProperly() {
        super.testAndroidTestCaseSetupProperly();
        try {
            doUpdate();
            doGet();
            doDelete();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

    }


    private  Date getDateFromDate(Date date)
    {
        Calendar calBeginning = new GregorianCalendar();
        calBeginning.setTime(date);
        calBeginning.set(Calendar.HOUR_OF_DAY, 0);
        calBeginning.set(Calendar.MINUTE, 0);
        calBeginning.set(Calendar.SECOND, 0);
        calBeginning.set(Calendar.MILLISECOND, 0);
        Date today = calBeginning.getTime();
        return today;
    }

    public  void doUpdate() throws Throwable
    {
        Log.i(TAG, "called testUpdate.");

        user = new User("","Tom",1,new Date().getTime(),age,75,175,new Date().getTime(),"");
        userDB.update(user);

        Steps steps = new Steps(-1,user.getId(),new Date().getTime());
        steps.setDate(getDateFromDate(new Date()).getTime());
        steps.setSteps(totalstep);
        steps.setGoal(10000);
        stepsDB.update(steps);

        Sleep sleep = new Sleep(-1,user.getId(),new Date().getTime());
        sleep.setDate(getDateFromDate(new Date()).getTime());
        sleep.setTotalSleepTime(totalsleep);
        sleep.setTotalWakeTime(0);
        sleep.setTotalLightTime(243);
        sleep.setTotalDeepTime(200);
        sleep.setHourlySleep("[60, 60, 60, 60, 60, 60, 60, 23, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]");
        sleep.setHourlyWake("[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]");
        sleep.setHourlyLight("[20, 39, 41, 20, 39, 41, 20, 23, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]");
        sleep.setHourlyDeep("[40, 21, 19, 40, 21, 19, 40, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]");
        sleep.setSleepQuality(100 * (sleep.getTotalLightTime() + sleep.getTotalDeepTime() / sleep.getTotalSleepTime()));
        sleepDB.update(sleep);

    }

    public  void doDelete() throws Throwable
    {
        Log.i(TAG,"called testDelete.");
        userDB.remove(user.getId(), null);
        stepsDB.remove(user.getId(),getDateFromDate(new Date()));
        sleepDB.remove(user.getId(),getDateFromDate(new Date()));
    }

    public  void doGet() throws Throwable
    {
        Log.i(TAG, "called testGet.");
        User theuser =  userDB.get(user.getId(), null);

        assertEquals(user.getId(),theuser.getId());
        assertEquals(user.getAge(), theuser.getAge());

        Steps steps = stepsDB.get(theuser.getId(),getDateFromDate(new Date()));
        Sleep sleep = sleepDB.get(theuser.getId(),getDateFromDate(new Date()));

        assertEquals(steps.getSteps(),totalstep);
        assertEquals(sleep.getTotalSleepTime(),totalsleep);

    }
}
