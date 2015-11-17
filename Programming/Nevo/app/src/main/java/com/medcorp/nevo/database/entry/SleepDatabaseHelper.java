package com.medcorp.nevo.database.entry;

import com.medcorp.nevo.application.ApplicationModel;
import com.medcorp.nevo.database.DatabaseHelper;
import com.medcorp.nevo.database.Sleep;
import com.medcorp.nevo.database.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by karl-john on 17/11/15.
 */
public class SleepDatabaseHelper implements BaseEntryDatabaseHelper<Sleep> {

    // instance of the database goes here as private variable
    private DatabaseHelper mDatabaseHelper;

    public SleepDatabaseHelper() {
        // Open the database & initlialize
        mDatabaseHelper = DatabaseHelper.getInstance(ApplicationModel.getApplicationModel());
    }

    @Override
    public boolean add(Sleep object) {
        int result = -1;
        try {
            result = mDatabaseHelper.getSleepDao().create(object);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result>=0;
    }

    @Override
    public boolean update(Sleep object) {
        int result = -1;
        try {
            result = mDatabaseHelper.getSleepDao().update(object);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result>=0;
    }

    @Override
    public boolean remove(int id) {
        int result = -1;
        try {
            result = mDatabaseHelper.getSleepDao().deleteById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result>=0;
    }

    @Override
    public Sleep get(int id) {
        List<Sleep> user = new ArrayList<Sleep>();
        try {
            user = mDatabaseHelper.getSleepDao().queryBuilder().where().eq(Sleep.fID,id).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user.isEmpty()?null:user.get(0);
    }

    @Override
    public List<Sleep> getAll() {
        List<Sleep> user = new ArrayList<Sleep>();
        try {
            user = mDatabaseHelper.getSleepDao().queryBuilder().query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user.isEmpty()?null:user;
    }
}
