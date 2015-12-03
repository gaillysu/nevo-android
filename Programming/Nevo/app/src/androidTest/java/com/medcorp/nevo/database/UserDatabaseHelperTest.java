package com.medcorp.nevo.database;

import android.test.AndroidTestCase;
import android.util.Log;

import com.medcorp.nevo.database.entry.UserDatabaseHelper;
import com.medcorp.nevo.model.User;

import junit.framework.Assert;

import java.util.List;

/**
 * Created by karl-john on 1/12/15.
 */
public class UserDatabaseHelperTest extends AndroidTestCase {

    private UserDatabaseHelper db;
    private User dummyUser;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        db = new UserDatabaseHelper(getContext());
        dummyUser = new User("Karl","Chow", 1, 946728000000l, 20, 70, 180, 946728000000l,"");

    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

    }

    public void testFunctions(){
        Log.w("Karl", "add user");
        assertEquals(true, db.add(dummyUser)!=null);
        Log.w("Karl","get user");
        List<User> userList = db.getAll();
        User newJoe = null;
        for (User user: userList){
            if (user.getLastName().equals(dummyUser.getLastName())){
                newJoe = user;
            }
        }
        assertNotNull(newJoe);
        newJoe.setFirstName("Karl");
        newJoe.setAge(10);
        newJoe.setBirthday(946728000100l);
        newJoe.setHeight(200);
        newJoe.setSex(100);
        newJoe.setWeight(120);
        newJoe.setLastName("Chow");
        Log.w("Karl", "update user");
        assertEquals(true, db.update(newJoe));
        User updatedJoe = db.get(newJoe.getId(),null);
        assertEquals(newJoe.getFirstName(),updatedJoe.getFirstName());
        assertEquals(newJoe.getAge(),updatedJoe.getAge());
        assertEquals(newJoe.getBirthday(),updatedJoe.getBirthday());
        assertEquals(newJoe.getHeight(),updatedJoe.getHeight());
        assertEquals(newJoe.getSex(),updatedJoe.getSex());
        assertEquals(newJoe.getWeight(),updatedJoe.getWeight());
        assertEquals(newJoe.getLastName(),updatedJoe.getLastName());
        Log.w("Karl", "remove user");
        assertEquals(true,db.remove(newJoe.getId(),null));
    }

}