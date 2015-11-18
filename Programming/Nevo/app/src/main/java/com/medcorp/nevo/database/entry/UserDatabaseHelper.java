package com.medcorp.nevo.database.entry;

import com.medcorp.nevo.application.ApplicationModel;
import com.medcorp.nevo.database.DatabaseHelper;
import com.medcorp.nevo.database.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by karl-john on 17/11/15.
 */
public class UserDatabaseHelper implements BaseEntryDatabaseHelper<User> {

    // instance of the database goes here as private variable
    private DatabaseHelper mDatabaseHelper;

    public UserDatabaseHelper() {
        // Open the database & initlialize
        mDatabaseHelper = DatabaseHelper.getInstance(ApplicationModel.getApplicationModel());
    }

    @Override
    public boolean add(User object) {
        int result = -1;
        try {
            result = mDatabaseHelper.getUserDao().create(object);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result>=0;
    }

    @Override
    public boolean update(User object) {
        int result = -1;
        try {
            result = mDatabaseHelper.getUserDao().update(object);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result>=0;
    }

    @Override
    public boolean remove(int id) {
        int result = -1;
        try {
            result = mDatabaseHelper.getUserDao().deleteById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result>=0;
    }

    @Override
    public User get(int id) {
        List<User> user = new ArrayList<User>();
        try {
            user = mDatabaseHelper.getUserDao().queryBuilder().where().eq(User.fID,id).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user.isEmpty()?null:user.get(0);
    }

    @Override
    public List<User> getAll() {
        List<User> user = new ArrayList<User>();
        try {
            user = mDatabaseHelper.getUserDao().queryBuilder().query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user.isEmpty()?null:user;
    }
}
