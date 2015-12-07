package com.medcorp.nevo.database;

import android.test.AndroidTestCase;
import android.util.Log;

import com.medcorp.nevo.ble.util.Optional;
import com.medcorp.nevo.database.entry.UserDatabaseHelper;
import com.medcorp.nevo.database.entry.iEntryDatabaseHelper;
import com.medcorp.nevo.model.User;

import java.util.List;

/**
 * Created by karl-john on 1/12/15.
 */
public class UserDatabaseHelperTest extends AndroidTestCase {

    private iEntryDatabaseHelper<User> db;
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
        List<User> userList = db.convertToNormalList(db.getAll());
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
        Optional<User> userOptional= db.get(newJoe.getId(),null);
        if (userOptional.notEmpty()){
            User updatedJoe = userOptional.get();
            assertEquals(newJoe.getFirstName(),updatedJoe.getFirstName());
            assertEquals(newJoe.getAge(),updatedJoe.getAge());
            assertEquals(newJoe.getBirthday(),updatedJoe.getBirthday());
            assertEquals(newJoe.getHeight(),updatedJoe.getHeight());
            assertEquals(newJoe.getSex(),updatedJoe.getSex());
            assertEquals(newJoe.getWeight(),updatedJoe.getWeight());
            assertEquals(newJoe.getLastName(),updatedJoe.getLastName());

        }
                Log.w("Karl", "remove user");
        assertEquals(true,db.remove(newJoe.getId(),null));
    }

    public void testAdd()
    {
        Optional<User> thisUser1 = db.add(addUser);
        assertEquals(true, thisUser1!=null);
        assertEquals(true, thisUser1.get().getId()!= -1);
        Optional<User> thisUser2 = db.get(thisUser1.get().getId(),null);

        assertEquals(thisUser1.get().getId(),thisUser2.get().getId());
        assertEquals(thisUser1.get().getFirstName(), thisUser2.get().getFirstName());
        assertEquals(thisUser1.get().getLastName(),thisUser2.get().getLastName());
        assertEquals(thisUser1.get().getAge(),thisUser2.get().getAge());
    }
    public void testRemove()
    {
        Optional<User> thisUser1 = db.add(removeUser);
        assertEquals(true, thisUser1!=null);
        assertEquals(true, thisUser1.get().getId()!= -1);
        assertEquals(true, db.remove(thisUser1.get().getId(), null));
        Optional<User> thisUser2 = db.get(thisUser1.get().getId(),null);
        assertEquals(true, thisUser2 == null);
    }

    public void testUpdate()
    {
        int userID = 1;
        Optional<User> thisUser1 = db.get(userID,null);
        if(thisUser1 == null) return;

        updateUser.setId(thisUser1.get().getId());
        assertEquals(true, db.update(updateUser));

        Optional<User> thisUser2 = db.get(updateUser.getId(),null);
        assertEquals(true, thisUser2 == null);

        assertEquals(updateUser.getId(),thisUser2.get().getId());
        assertEquals(updateUser.getFirstName(), thisUser2.get().getFirstName());
        assertEquals(updateUser.getLastName(), thisUser2.get().getLastName());
        assertEquals(updateUser.getAge(),thisUser2.get().getAge());

    }

}