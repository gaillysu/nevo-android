package com.medcorp.nevo.database.entry;

import com.medcorp.nevo.application.ApplicationModel;
import com.medcorp.nevo.database.DatabaseHelper;
import com.medcorp.nevo.database.Heartbeat;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by karl-john on 17/11/15.
 */
public class HeartbeatDatabaseHelper implements BaseEntryDatabaseHelper<Heartbeat> {

    // instance of the database goes here as private variable
    private DatabaseHelper mDatabaseHelper;

    public HeartbeatDatabaseHelper() {
        // Open the database & initlialize
        mDatabaseHelper = DatabaseHelper.getInstance(ApplicationModel.getApplicationModel());
    }

    @Override
    public boolean add(Heartbeat object) {
        int result = -1;
        try {
            result = mDatabaseHelper.getHeartbeatDao().create(object);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result>=0;
    }

    @Override
    public boolean update(Heartbeat object) {
        int result = -1;
        try {
            result = mDatabaseHelper.getHeartbeatDao().update(object);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result>=0;
    }

    @Override
    public boolean remove(int id) {
        int result = -1;
        try {
            result = mDatabaseHelper.getHeartbeatDao().deleteById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result>=0;
    }

    @Override
    public Heartbeat get(int id) {
        List<Heartbeat> user = new ArrayList<Heartbeat>();
        try {
            user = mDatabaseHelper.getHeartbeatDao().queryBuilder().where().eq(Heartbeat.fID,id).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user.isEmpty()?null:user.get(0);
    }

    @Override
    public List<Heartbeat> getAll() {
        List<Heartbeat> user = new ArrayList<Heartbeat>();
        try {
            user = mDatabaseHelper.getHeartbeatDao().queryBuilder().query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user.isEmpty()?null:user;
    }
}
