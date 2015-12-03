package com.medcorp.nevo.application;

import android.test.AndroidTestCase;
import android.util.Log;

import com.medcorp.nevo.database.entry.AlarmDatabaseHelper;
import com.medcorp.nevo.database.entry.SleepDatabaseHelper;
import com.medcorp.nevo.database.entry.StepsDatabaseHelper;
import com.medcorp.nevo.database.entry.UserDatabaseHelper;
import com.medcorp.nevo.model.Alarm;
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
    private AlarmDatabaseHelper alarmDB;

    //every test, simlator different user to login
    private User user;

    private Alarm[] alarm = {new Alarm(0,6,30,true,"breakfast time"),
            new Alarm(1,12,30,true,"lunch time"),
            new Alarm(2,18,30,true,"supper time")};

    //sample data:
    int age = 20; // insert "user" table
    int goal = 10000;//insert "steps" table
    int totalstep = 1001; //insert "steps" table
    int totalsleep = 443; //insert "sleep" table


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        userDB = new UserDatabaseHelper(getContext());
        stepsDB = new StepsDatabaseHelper(getContext());
        sleepDB = new SleepDatabaseHelper(getContext());
        alarmDB = new AlarmDatabaseHelper(getContext());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
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

    /**
     * test order: add-->get-->update-->get--delete
     */
    public void testFunctions()
    {
        Log.i(TAG, "start all test...");
        doAdd();
        doGet();
        doUpdate();
        doGet();
        doRemove();
        Log.i(TAG, "end all test.");
    }

    private  void doAdd() {
        Log.i(TAG, "start test Add() function");

        user = new User("","Tom",1,new Date().getTime(),age,75,175,new Date().getTime(),"");
        user = userDB.add(user);
        assertNotNull(user);

        Steps steps = new Steps(-1,user.getId(),new Date().getTime());
        steps.setDate(getDateFromDate(new Date()).getTime());
        steps.setSteps(totalstep);
        steps.setGoal(goal);
        steps = stepsDB.add(steps);
        assertNotNull(steps);

        Sleep sleep = new Sleep(-1,user.getId(),new Date().getTime());
        sleep.setDate(getDateFromDate(new Date()).getTime());
        sleep.setTotalSleepTime(totalsleep);
        sleep = sleepDB.add(sleep);
        assertNotNull(sleep);

        alarm[0] = alarmDB.add(alarm[0]);
        assertNotNull(alarm[0]);
        alarm[1] = alarmDB.add(alarm[1]);
        assertNotNull(alarm[1]);
        alarm[2] = alarmDB.add(alarm[2]);
        assertNotNull(alarm[2]);

        Log.i(TAG, "end test Add() function");

    }


    private  void doUpdate()
    {
        Log.i(TAG, "start test Update() function");

        assertNotNull(user);
        assertTrue(user.getId() >= 0);

        Log.i(TAG, "double the values of age,totalstep,goal,totalsleep");

        age = 2*age;
        totalstep = 2*totalstep;
        goal = 2*goal;
        totalsleep = 2*totalstep;

        user.setAge(age);
        userDB.update(user);

        Steps steps = new Steps(-1,user.getId(),new Date().getTime());
        steps.setDate(getDateFromDate(new Date()).getTime());
        steps.setSteps(totalstep);
        steps.setGoal(goal);
        stepsDB.update(steps);

        Sleep sleep = new Sleep(-1,user.getId(),new Date().getTime());
        sleep.setDate(getDateFromDate(new Date()).getTime());
        sleep.setTotalSleepTime(totalsleep);
        sleepDB.update(sleep);

        assertNotNull(alarm[0]);
        assertNotNull(alarm[1]);
        assertNotNull(alarm[2]);

        Log.i(TAG, "disable all alarms");
        alarm[0].setEnable(false);
        alarm[1].setEnable(false);
        alarm[2].setEnable(false);

        alarmDB.update(alarm[0]);
        alarmDB.update(alarm[1]);
        alarmDB.update(alarm[2]);

        Log.i(TAG, "end test Update() function");
    }

    private  void doRemove()
    {
        Log.i(TAG, "start test remove() function.");
        userDB.remove(user.getId(), null);
        stepsDB.remove(user.getId(),getDateFromDate(new Date()));
        sleepDB.remove(user.getId(),getDateFromDate(new Date()));
        alarmDB.remove(alarm[0].getId(),null);
        alarmDB.remove(alarm[1].getId(),null);
        alarmDB.remove(alarm[2].getId(),null);
        Log.i(TAG, "end test remove() function.");
    }

    private  void doGet()
    {
        Log.i(TAG, "start test get() function.");
        User theuser =  userDB.get(user.getId(), null);

        assertEquals(user.getId(),theuser.getId());
        assertEquals(user.getAge(), theuser.getAge());

        Steps steps = stepsDB.get(theuser.getId(),getDateFromDate(new Date()));
        Sleep sleep = sleepDB.get(theuser.getId(),getDateFromDate(new Date()));

        assertEquals(steps.getSteps(),totalstep);
        assertEquals(steps.getGoal(),goal);
        assertEquals(sleep.getTotalSleepTime(),totalsleep);

        Alarm thealarm[]={null,null,null};

        thealarm[0] = alarmDB.get(alarm[0].getId(),null);
        thealarm[1] = alarmDB.get(alarm[1].getId(),null);
        thealarm[2] = alarmDB.get(alarm[2].getId(),null);
        assertNotNull(thealarm[0]);
        assertNotNull(thealarm[1]);
        assertNotNull(thealarm[2]);

        assertEquals(thealarm[0].getId(), alarm[0].getId());
        assertEquals(thealarm[0].getLabel(), alarm[0].getLabel());
        assertEquals(thealarm[0].getHour(), alarm[0].getHour());
        assertEquals(thealarm[0].getMinute(), alarm[0].getMinute());
        assertEquals(thealarm[0].isEnable(), alarm[0].isEnable());

        assertEquals(thealarm[1].getId(), alarm[1].getId());
        assertEquals(thealarm[1].getLabel(), alarm[1].getLabel());
        assertEquals(thealarm[1].getHour(), alarm[1].getHour());
        assertEquals(thealarm[1].getMinute(), alarm[1].getMinute());
        assertEquals(thealarm[1].isEnable() , alarm[1].isEnable());

        assertEquals(thealarm[2].getId(), alarm[2].getId());
        assertEquals(thealarm[2].getLabel(), alarm[2].getLabel());
        assertEquals(thealarm[2].getHour(), alarm[2].getHour());
        assertEquals(thealarm[2].getMinute(), alarm[2].getMinute());
        assertEquals(thealarm[2].isEnable() , alarm[2].isEnable());

        Log.i(TAG, "end test get() function.");
    }
}
