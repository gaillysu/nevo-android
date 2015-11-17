package com.medcorp.nevo.database.entry;

import com.medcorp.nevo.model.ExampleUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by karl-john on 17/11/15.
 */
public class UserDatabaseHelper implements BaseEntryDatabaseHelper<ExampleUser> {

    // instance of the database goes here as private variable

    public UserDatabaseHelper() {
        // Open the database & initlialize
    }

    @Override
    public boolean add(ExampleUser object) {
        // Open the database for
        boolean success = false; // This should be boolean success = database.add(object);
        // Close database
        return success;
    }

    @Override
    public boolean update(ExampleUser object) {
        // Open the database for
        boolean success = false; // This should be boolean success = database.update(object);
        // Close database
        return success;
    }

    @Override
    public boolean remove(int id) {
        // Open the database for
        boolean success = false; // This should be boolean success = database.delete(object);
        // Close database
        return success;
    }

    @Override
    public ExampleUser get(int id) {
        // Open the database for
        ExampleUser user = new ExampleUser(0,null,0,0,0,null); // This should be boolean user = database.get(id);
        // Close database
        return user;
    }

    @Override
    public List<ExampleUser> getAll() {
        // Open the database for
        List<ExampleUser> users = new ArrayList<ExampleUser>(); // This should be List<EXampleUser> users = database.getAll();
        // Close database
        return users;
    }
}
