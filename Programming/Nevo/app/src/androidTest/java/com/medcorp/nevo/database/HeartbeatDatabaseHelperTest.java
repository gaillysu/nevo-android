package com.medcorp.nevo.database;

import android.test.AndroidTestCase;

import com.medcorp.nevo.ble.util.Optional;
import com.medcorp.nevo.database.entry.HeartbeatDatabaseHelper;
import com.medcorp.nevo.database.entry.UserDatabaseHelper;
import com.medcorp.nevo.model.Heartbeat;
import com.medcorp.nevo.model.User;
import com.medcorp.nevo.util.Common;

import java.util.Date;

/**
 * Created by gaillysu on 15/12/8.
 */
public class HeartbeatDatabaseHelperTest extends AndroidTestCase {

    private UserDatabaseHelper dbUser;

    //assume one user login and make it owner all sleep data
    private User loginUser;

    private HeartbeatDatabaseHelper db;
    private Heartbeat addHR;
    private Heartbeat updateHR;
    private Heartbeat removeHR;

    Date today;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        dbUser = new UserDatabaseHelper(getContext());
        loginUser = new User("Karl","Chow", 1, 946728000000l, 20, 70, 180, 946728000000l,"");

        Optional<User> thisuser = dbUser.add(loginUser);
        assertEquals(false,thisuser.isEmpty());
        //set user ID as a login user
        loginUser = thisuser.get();

        today = Common.getDateFromDate(new Date());

        db = new HeartbeatDatabaseHelper(getContext());

        addHR = new Heartbeat(new Date().getTime(),today.getTime(),120,60,"add sample");
        addHR.setUserID(loginUser.getId());

        updateHR = new Heartbeat(new Date().getTime(),today.getTime(),120,60,"update sample");
        updateHR.setUserID(loginUser.getId());

        removeHR = new Heartbeat(new Date().getTime(),today.getTime(),120,60,"remove sample");
        removeHR.setUserID(loginUser.getId());


    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    public void testAdd()
    {
        Optional<Heartbeat> thisHR1 = db.add(addHR);
        assertEquals(false,thisHR1.isEmpty());

        Optional<Heartbeat> thisHR2 = db.get(loginUser.getId(),today);
        assertEquals(false,thisHR2.isEmpty());

        assertEquals(addHR.getRemarks(),thisHR2.get().getRemarks());

    }
    public void testUpdate()
    {
        Optional<Heartbeat> thisHR1 = db.add(updateHR);
        assertEquals(false,thisHR1.isEmpty());
        updateHR = thisHR1.get();

        updateHR.setAvgHrm((int) (Math.random() * 10000));
        updateHR.setRemarks("23wetwte");

        assertEquals(true, db.update(updateHR));

        Optional<Heartbeat> thisHR2 = db.get(loginUser.getId(),today);
        assertEquals(false,thisHR2.isEmpty());

        assertEquals(thisHR2.get().getAvgHrm(),updateHR.getAvgHrm());
        assertEquals(thisHR2.get().getRemarks(),updateHR.getRemarks());

    }
    public void testRemove()
    {
        Optional<Heartbeat> thisHR1 = db.add(removeHR);
        assertEquals(false,thisHR1.isEmpty());
        removeHR = thisHR1.get();

        Optional<Heartbeat> thisHR2 = db.get(loginUser.getId(),today);
        assertEquals(false,thisHR2.isEmpty());

        assertEquals(removeHR.getRemarks(),thisHR2.get().getRemarks());

        assertEquals(true,db.remove(loginUser.getId(), today));

        Optional<Heartbeat> thisHR3 = db.get(loginUser.getId(),today);
        assertEquals(true, thisHR3.isEmpty());

    }
}
