package com.medcorp.nevo.database;

import android.test.AndroidTestCase;

import com.medcorp.nevo.database.entry.UserDatabaseHelper;
import com.medcorp.nevo.model.User;

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
        dummyUser = new User("John","Doe", 1, 946728000000l, 20, 70, 180, 946728000000l,"");
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testAddUser(){

    }

    public void testGetUser(){

    }

    public void testUpdateUser(){

    }

    public void testRemoveUser(){

    }

}
