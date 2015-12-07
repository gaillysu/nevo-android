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
    private User addUser;
    private User removeUser;
    private User updateUser;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        db = new UserDatabaseHelper(getContext());
        dummyUser = new User("Karl","Chow", 1, 946728000000l, 20, 70, 180, 946728000000l,"");

        addUser = new User("KarlAdd","Chow", 1, 946728000000l, 20, 70, 180, 946728000000l,"");
        removeUser = new User("KarlRemove","Chow", 1, 946728000000l, 20, 70, 180, 946728000000l,"");
        updateUser = new User("KarlUpdate","Chow", 1, 946728000000l, 20, 70, 180, 946728000000l,"");
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

    public void testAdd()
    {
        User thisUser1 = db.add(addUser);
        assertEquals(true, thisUser1!=null);
        assertEquals(true, thisUser1.getId()!= -1);
        User thisUser2 = db.get(thisUser1.getId(),null);

        assertEquals(thisUser1.getId(),thisUser2.getId());
        assertEquals(thisUser1.getFirstName(),thisUser2.getFirstName());
        assertEquals(thisUser1.getLastName(),thisUser2.getLastName());
        assertEquals(thisUser1.getAge(),thisUser2.getAge());
    }
    public void testRemove()
    {
        User thisUser1 = db.add(removeUser);
        assertEquals(true, thisUser1!=null);
        assertEquals(true, thisUser1.getId()!= -1);
        assertEquals(true, db.remove(thisUser1.getId(), null));
        User thisUser2 = db.get(thisUser1.getId(),null);
        assertEquals(true, thisUser2==null);
    }

    public void testUpdate()
    {
        int userID = 1;
        User thisUser1 = db.get(userID,null);
        if(thisUser1 == null) return;

        updateUser.setId(thisUser1.getId());
        assertEquals(true, db.update(updateUser));

        User thisUser2 = db.get(updateUser.getId(),null);
        assertEquals(true, thisUser2==null);

        assertEquals(updateUser.getId(),thisUser2.getId());
        assertEquals(updateUser.getFirstName(), thisUser2.getFirstName());
        assertEquals(updateUser.getLastName(),thisUser2.getLastName());
        assertEquals(updateUser.getAge(),thisUser2.getAge());

    }

}