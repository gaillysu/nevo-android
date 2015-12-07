package com.medcorp.nevo.application;

import android.test.AndroidTestCase;
import android.util.Log;

import com.medcorp.nevo.ble.util.Optional;
import com.medcorp.nevo.database.entry.AlarmDatabaseHelper;
import com.medcorp.nevo.database.entry.SleepDatabaseHelper;
import com.medcorp.nevo.database.entry.StepsDatabaseHelper;
import com.medcorp.nevo.database.entry.UserDatabaseHelper;
import com.medcorp.nevo.model.Alarm;
import com.medcorp.nevo.model.Sleep;
import com.medcorp.nevo.model.Steps;
import com.medcorp.nevo.model.User;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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



    private Optional<Alarm>[] alarm = new Optional[3];



    //sample data:
    int age = 20; // insert "user" table
    int goal = 10000;//insert "steps" table
    int totalstep = 1001; //insert "steps" table
    int totalsleep = 443; //insert "sleep" table


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Optional<Alarm> optionalAlarm = new Optional<>();
        Optional<Alarm> optionalAlarm2 = new Optional<>();
        Optional<Alarm> optionalAlarm3 = new Optional<>();
        Alarm alarm = new Alarm(6,30,true,"breakfast time");
        Alarm alarm2 = new Alarm(12,30,true,"lunch time");
        Alarm alarm3 = new Alarm(18,30,true,"supper time");
        optionalAlarm.set(alarm);
        optionalAlarm2.set(alarm2);
        optionalAlarm3.set(alarm3);

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
        Optional<User> userOptional = userDB.add(user);
        assertNotNull(userOptional.get());
        assertEquals(false, userOptional.isEmpty());

        Steps steps = new Steps(new Date().getTime());
        steps.setDate(getDateFromDate(new Date()).getTime());
        steps.setSteps(totalstep);
        steps.setGoal(goal);
        Optional<Steps> stepsOptional =stepsDB.add(steps);
        assertNotNull(stepsOptional.get());

        Sleep sleep = new Sleep(new Date().getTime());
        sleep.setDate(getDateFromDate(new Date()).getTime());
        sleep.setTotalSleepTime(totalsleep);
        Optional<Sleep>  sleepOptional = sleepDB.add(sleep);
        assertNotNull(sleep);

        alarm[0] = alarmDB.add(alarm[0].get());
        assertNotNull(alarm[0]);
        alarm[1] = alarmDB.add(alarm[1].get());
        assertNotNull(alarm[1]);
        alarm[2] = alarmDB.add(alarm[2].get());
        assertNotNull(alarm[2]);
        assertEquals(true, alarm[2].notEmpty());
        assertNotNull(alarm[2].get());


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

        Steps steps = new Steps(new Date().getTime());
        steps.setDate(getDateFromDate(new Date()).getTime());
        steps.setSteps(totalstep);
        steps.setGoal(goal);
        stepsDB.update(steps);

        Sleep sleep = new Sleep(new Date().getTime());
        sleep.setDate(getDateFromDate(new Date()).getTime());
        sleep.setTotalSleepTime(totalsleep);
        sleepDB.update(sleep);

        assertNotNull(alarm[0]);
        assertNotNull(alarm[1]);
        assertNotNull(alarm[2]);

        Log.i(TAG, "disable all alarms");
        alarm[0].get().setEnable(false);
        alarm[1].get().setEnable(false);
        alarm[2].get().setEnable(false);

        alarmDB.update(alarm[0].get());
        alarmDB.update(alarm[1].get());
        alarmDB.update(alarm[2].get());

        Log.i(TAG, "end test Update() function");
    }

    private  void doRemove()
    {
        Log.i(TAG, "start test remove() function.");
        userDB.remove(user.getId(), null);
        stepsDB.remove(user.getId(),getDateFromDate(new Date()));
        sleepDB.remove(user.getId(),getDateFromDate(new Date()));
        alarmDB.remove(alarm[0].get().getId(),null);
        alarmDB.remove(alarm[1].get().getId(),null);
        alarmDB.remove(alarm[2].get().getId(),null);
        Log.i(TAG, "end test remove() function.");
    }

    private  void doGet()
    {
        Log.i(TAG, "start test get() function.");
        Optional<User> theuser =  userDB.get(user.getId(), null);
        assertNotNull(theuser.get());
        assertEquals(false,theuser.isEmpty());

        assertEquals(user.getId(), theuser.get().getId());
        assertEquals(user.getAge(), theuser.get().getAge());

        Optional<Steps> stepsOptional = stepsDB.get(theuser.get().getId(),getDateFromDate(new Date()));
        Optional<Sleep> sleepOptional = sleepDB.get(theuser.get().getId(),getDateFromDate(new Date()));

        assertNotNull(stepsOptional.get());
        assertEquals(false, stepsOptional.isEmpty());
        assertNotNull(sleepOptional.get());
        assertEquals(false,sleepOptional.isEmpty());

        Steps steps = stepsOptional.get();
        Sleep sleep = sleepOptional.get();

        assertEquals(steps.getSteps(),totalstep);
        assertEquals(steps.getGoal(),goal);
        assertEquals(sleep.getTotalSleepTime(),totalsleep);

        Optional<Alarm>  thealarm[] = new Optional[3];

        thealarm[0] = alarmDB.get(alarm[0].get().getId(),null);
        thealarm[1] = alarmDB.get(alarm[1].get().getId(),null);
        thealarm[2] = alarmDB.get(alarm[2].get().getId(),null);
        assertNotNull(thealarm[0]);
        assertNotNull(thealarm[1]);
        assertNotNull(thealarm[2]);

        assertEquals(thealarm[0].get().getId(), alarm[0].get().getId());
        assertEquals(thealarm[0].get().getLabel(), alarm[0].get().getLabel());
        assertEquals(thealarm[0].get().getHour(), alarm[0].get().getHour());
        assertEquals(thealarm[0].get().getMinute(), alarm[0].get().getMinute());
        assertEquals(thealarm[0].get().isEnable(), alarm[0].get().isEnable());

        assertEquals(thealarm[1].get().getId(), alarm[1].get().getId());
        assertEquals(thealarm[1].get().getLabel(), alarm[1].get().getLabel());
        assertEquals(thealarm[1].get().getHour(), alarm[1].get().getHour());
        assertEquals(thealarm[1].get().getMinute(), alarm[1].get().getMinute());
        assertEquals(thealarm[1].get().isEnable() , alarm[1].get().isEnable());

        assertEquals(thealarm[2].get().getId(), alarm[2].get().getId());
        assertEquals(thealarm[2].get().getLabel(), alarm[2].get().getLabel());
        assertEquals(thealarm[2].get().getHour(), alarm[2].get().getHour());
        assertEquals(thealarm[2].get().getMinute(), alarm[2].get().getMinute());
        assertEquals(thealarm[2].get().isEnable() , alarm[2].get().isEnable());

        Log.i(TAG, "end test get() function.");
    }
}
