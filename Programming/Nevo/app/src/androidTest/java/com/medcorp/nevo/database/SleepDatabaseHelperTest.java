package com.medcorp.nevo.database;

import android.test.AndroidTestCase;

import com.medcorp.nevo.ble.util.Optional;
import com.medcorp.nevo.database.entry.SleepDatabaseHelper;
import com.medcorp.nevo.database.entry.UserDatabaseHelper;
import com.medcorp.nevo.database.entry.iEntryDatabaseHelper;
import com.medcorp.nevo.model.Sleep;
import com.medcorp.nevo.model.User;
import com.medcorp.nevo.util.Common;

import java.util.Date;

/**
 * Created by Karl on 12/7/15.
 */
public class SleepDatabaseHelperTest extends AndroidTestCase {

    private SleepDatabaseHelper db;
    private UserDatabaseHelper  dbUser;

    //assume one user login and make it owner all sleep data
    private User loginUser;

    private Sleep  addSleep;
    private Sleep  updateSleep;
    private Sleep  removeSleep;
    private Date   today;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        dbUser = new UserDatabaseHelper(getContext());
        loginUser = new User("Karl","Chow", 1, 946728000000l, 20, 70, 180, 946728000000l,"");

        Optional<User> thisuser = dbUser.add(loginUser);
        assertEquals(false,thisuser.isEmpty());
        //set user ID as a login user
        loginUser.setId(thisuser.get().getId());

        db = new SleepDatabaseHelper(getContext());

        //this is today's data, today format is YYYYMMDD 00:00:00
        today = Common.getDateFromDate(new Date());

        // sample data
        addSleep = new Sleep(new Date().getTime(), today.getTime(),480,60,360,60,"","","","",0,0,0,"");
        //here must set which one owner this data
        addSleep.setUserID(loginUser.getId());

        updateSleep = new Sleep(new Date().getTime(), today.getTime(),490,60,370,60,"","","","",0,0,0,"");
        updateSleep.setUserID(loginUser.getId());

        removeSleep = new Sleep(new Date().getTime(), today.getTime(),500,60,380,60,"","","","",0,0,0,"");
        removeSleep.setUserID(loginUser.getId());

    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testAdd()
    {
        //add sample data
        Optional<Sleep> thisSleep1 = db.add(addSleep);
        assertEquals(false,thisSleep1.isEmpty());

        //read sample data
        Optional<Sleep> thisSleep2 = db.get(loginUser.getId(),today);
        assertEquals(false, thisSleep2.isEmpty());

        //compare data
        assertEquals(addSleep.getTotalSleepTime(), thisSleep2.get().getTotalSleepTime());
    }

    public void testUpdate()
    {

        Optional<Sleep> thisSleep1 = db.add(updateSleep);
        assertEquals(false, thisSleep1.isEmpty());
        updateSleep = thisSleep1.get();


        updateSleep.setTotalSleepTime((int) (Math.random()*10000));

        assertEquals(true, db.update(updateSleep));

        //read data
        Optional<Sleep> thisSleep2 = db.get(loginUser.getId(),today);
        assertEquals(false, thisSleep2.isEmpty());

        //compare data
        assertEquals(updateSleep.getTotalSleepTime(), thisSleep2.get().getTotalSleepTime());
    }

    public void testRemove()
    {
        //add sample data
        Optional<Sleep> thisSleep1 = db.add(removeSleep);
        assertEquals(false, thisSleep1.isEmpty());

        //make sure it is saved ok
        Optional<Sleep> thisSleep2 = db.get(loginUser.getId(),today);
        assertEquals(false,thisSleep2.isEmpty());
        assertEquals(removeSleep.getTotalSleepTime(),thisSleep2.get().getTotalSleepTime());

        //remove it
        assertEquals(true, db.remove(loginUser.getId(), today));

        //read it again,check result
        Optional<Sleep> thisSleep3 = db.get(loginUser.getId(),today);
        assertEquals(true, thisSleep3.isEmpty());
    }
}
