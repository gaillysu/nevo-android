package com.medcorp.nevo.database.entry;

import com.medcorp.nevo.application.ApplicationModel;
import com.medcorp.nevo.database.DatabaseHelper;
import com.medcorp.nevo.database.Steps;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by karl-john on 17/11/15.
 */
public class StepsDatabaseHelper implements BaseEntryDatabaseHelper<Steps> {

    // instance of the database goes here as private variable
    private DatabaseHelper mDatabaseHelper;

    public StepsDatabaseHelper() {
        // Open the database & initlialize
        mDatabaseHelper = DatabaseHelper.getInstance(ApplicationModel.getApplicationModel());
    }

    @Override
    public boolean add(Steps object) {
        int result = -1;
        try {
            result = mDatabaseHelper.getStepsDao().create(object);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result>=0;
    }

    @Override
    public boolean update(Steps object) {
        int result = -1;
        try {
            result = mDatabaseHelper.getStepsDao().update(object);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result>=0;
    }

    @Override
    public boolean remove(int id) {
        int result = -1;
        try {
            result = mDatabaseHelper.getStepsDao().deleteById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result>=0;
    }

    @Override
    public Steps get(int id) {
        List<Steps> user = new ArrayList<Steps>();
        try {
            user = mDatabaseHelper.getStepsDao().queryBuilder().where().eq(Steps.fID,id).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user.isEmpty()?null:user.get(0);
    }

    @Override
    public List<Steps> getAll() {
        List<Steps> user = new ArrayList<Steps>();
        try {
            user = mDatabaseHelper.getStepsDao().queryBuilder().query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user.isEmpty()?null:user;
    }
}
