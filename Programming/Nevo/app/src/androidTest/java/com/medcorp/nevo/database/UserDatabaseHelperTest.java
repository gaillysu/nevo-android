package com.medcorp.nevo.database;

import android.test.AndroidTestCase;

import com.medcorp.nevo.database.entry.UserDatabaseHelper;
import com.medcorp.nevo.database.entry.iEntryDatabaseHelper;
import com.medcorp.nevo.model.User;

import net.medcorp.library.ble.util.Optional;

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

    public void testAdd()
    {
        Optional<User> thisUser1 = db.add(addUser);
        assertEquals(false, thisUser1.isEmpty());
        assertEquals(true, thisUser1.get().getId() != -1);
        Optional<User> thisUser2 = db.get(thisUser1.get().getId(),null);

        assertEquals(addUser.getId(), thisUser2.get().getId());
        assertEquals(addUser.getFirstName(), thisUser2.get().getFirstName());
        assertEquals(addUser.getLastName(),thisUser2.get().getLastName());
        assertEquals(addUser.getAge(),thisUser2.get().getAge());
    }
    public void testRemove()
    {
        Optional<User> thisUser1 = db.add(removeUser);
        assertEquals(false, thisUser1.isEmpty());
        assertEquals(true, thisUser1.get().getId()!= -1);
        assertEquals(true, db.remove(thisUser1.get().getId(), null));
        Optional<User> thisUser2 = db.get(thisUser1.get().getId(),null);
        assertEquals(true, thisUser2.isEmpty());
    }

    public void testUpdate()
    {

        //add new user to update
        Optional<User> updatedUser = db.add(updateUser);
        assertEquals(false, updatedUser.isEmpty());
        updateUser = updatedUser.get();

        updateUser.setFirstName("werewr");
        updateUser.setLastName("ertretwq11");
        updateUser.setAge(100);

        assertEquals(true, db.update(updateUser));

        Optional<User> thisUser2 = db.get(updateUser.getId(),null);
        assertEquals(false, thisUser2.isEmpty());

        assertEquals(updateUser.getId(),thisUser2.get().getId());
        assertEquals(updateUser.getFirstName(), thisUser2.get().getFirstName());
        assertEquals(updateUser.getLastName(), thisUser2.get().getLastName());
        assertEquals(updateUser.getAge(),thisUser2.get().getAge());

    }

}