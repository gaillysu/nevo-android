package com.medcorp.nevo.database.entry;

import com.j256.ormlite.dao.Dao;
import com.medcorp.nevo.application.ApplicationModel;
import com.medcorp.nevo.database.DatabaseHelper;
import com.medcorp.nevo.database.dao.SleepDAO;
import com.medcorp.nevo.model.Sleep;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by karl-john on 17/11/15.
 */
public class SleepDatabaseHelper implements iEntryDatabaseHelper<Sleep> {

    // instance of the database goes here as private variable
    private DatabaseHelper databaseHelper;

    public SleepDatabaseHelper() {
        // Open the database & initlialize
        databaseHelper = DatabaseHelper.getInstance(ApplicationModel.getApplicationModel());
    }

    @Override
    public boolean add(Sleep object) {
        int result = -1;
        try {
            result = databaseHelper.getSleepDao().create(convertToDao(object));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result>=0;
    }

    @Override
    public boolean update(Sleep object) {
        Dao.CreateOrUpdateStatus result = null;
        try {
            result = databaseHelper.getSleepDao().createOrUpdate(convertToDao(object));
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean remove(int id) {
        int result = -1;
        try {
            result = databaseHelper.getSleepDao().deleteById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result>=0;
    }

    @Override
    public Sleep get(int id) {
        List<Sleep> sleepList = new ArrayList<Sleep>();
        try {
            List<SleepDAO> sleepDAOList = databaseHelper.getSleepDao().queryBuilder().where().eq(SleepDAO.fID, id).query();
            for (SleepDAO userDao: sleepDAOList) {
                sleepList.add(convertToNormal(userDao));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sleepList.isEmpty()?null:sleepList.get(0);
    }

    @Override
    public List<Sleep> getAll() {
        List<Sleep> sleepList = new ArrayList<Sleep>();

        try {
            List<SleepDAO> sleepDAOList = databaseHelper.getSleepDao().queryBuilder().query();
            for (SleepDAO userDao: sleepDAOList) {
                sleepList.add(convertToNormal(userDao));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sleepList.isEmpty()?null:sleepList;
    }

    private SleepDAO convertToDao(Sleep sleep){
        SleepDAO sleepDAO = new SleepDAO();
        sleepDAO.setCreatedDate(sleep.getCreatedDate());
        sleepDAO.setDate(sleep.getDate());
        sleepDAO.setEnd(sleep.getEnd());
        sleepDAO.setHourlyDeep(sleep.getHourlyDeep());
        sleepDAO.setHourlyLight(sleep.getHourlyLight());
        sleepDAO.setHourlySleep(sleep.getHourlySleep());
        sleepDAO.setHourlyWake(sleep.getHourlyWake());
        sleepDAO.setID(sleep.getiD());
        sleepDAO.setRemarks(sleep.getRemarks());
        sleepDAO.setSleepQuality(sleep.getSleepQuality());
        sleepDAO.setStart(sleep.getStart());
        sleepDAO.setTotalDeepTime(sleep.getTotalDeepTime());
        sleepDAO.setTotalLightTime(sleep.getTotalLightTime());
        sleepDAO.setTotalSleepTime(sleep.getTotalSleepTime());
        sleepDAO.setTotalWakeTime(sleep.getTotalWakeTime());
        sleepDAO.setUserID(sleep.getUserID());
        return sleepDAO;
    }

    private Sleep convertToNormal(SleepDAO sleepDAO){
        Sleep sleep = new Sleep(sleepDAO.getID(), sleepDAO.getUserID(), sleepDAO.getCreatedDate());
        sleep.setDate(sleepDAO.getDate());
        sleep.setEnd(sleepDAO.getEnd());
        sleep.setHourlyDeep(sleepDAO.getHourlyDeep());
        sleep.setHourlyLight(sleepDAO.getHourlyLight());
        sleep.setHourlySleep(sleepDAO.getHourlySleep());
        sleep.setHourlyWake(sleepDAO.getHourlyWake());
        sleep.setRemarks(sleepDAO.getRemarks());
        sleep.setSleepQuality(sleepDAO.getSleepQuality());
        sleep.setStart(sleepDAO.getStart());
        sleep.setTotalDeepTime(sleepDAO.getTotalDeepTime());
        sleep.setTotalLightTime(sleepDAO.getTotalLightTime());
        sleep.setTotalSleepTime(sleepDAO.getTotalSleepTime());
        sleep.setTotalWakeTime(sleepDAO.getTotalWakeTime());
        return sleep;
    }
}
