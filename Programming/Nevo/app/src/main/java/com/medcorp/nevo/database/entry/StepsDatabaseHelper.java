package com.medcorp.nevo.database.entry;

import com.j256.ormlite.dao.Dao;
import com.medcorp.nevo.application.ApplicationModel;
import com.medcorp.nevo.database.DatabaseHelper;
import com.medcorp.nevo.database.dao.StepsDAO;
import com.medcorp.nevo.database.dao.UserDAO;
import com.medcorp.nevo.model.Steps;
import com.medcorp.nevo.model.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by karl-john on 17/11/15.
 */
public class StepsDatabaseHelper implements iEntryDatabaseHelper<Steps> {

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
            result = mDatabaseHelper.getStepsDao().create(convertToDao(object));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result>=0;
    }

    @Override
    public boolean update(Steps object) {

        int result = -1;
        try {
            List<StepsDAO> stepsDAOList = mDatabaseHelper.getStepsDao().queryBuilder().where().eq(StepsDAO.fUserID, object.getUserID()).and().eq(StepsDAO.fDate,object.getDate()).query();
            if(stepsDAOList.isEmpty()) return add(object);
            StepsDAO daoobject = convertToDao(object);
            daoobject.setID(stepsDAOList.get(0).getID());
            result = mDatabaseHelper.getStepsDao().update(daoobject);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result>=0;
    }

    @Override
    public boolean remove(int userid,Date date) {
        try {
            List<StepsDAO> stepsDAOList = mDatabaseHelper.getStepsDao().queryBuilder().where().eq(StepsDAO.fUserID, userid).and().eq(StepsDAO.fDate,date.getTime()).query();
            if(!stepsDAOList.isEmpty())mDatabaseHelper.getStepsDao().delete(stepsDAOList);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Steps get(int userid,Date date) {
        List<Steps> stepsList = new ArrayList<Steps>();
        try {
            List<StepsDAO> stepsDAOList = mDatabaseHelper.getStepsDao().queryBuilder().where().eq(StepsDAO.fUserID, userid).and().eq(StepsDAO.fDate,date.getTime()).query();
            for(StepsDAO stepsDAO : stepsDAOList){
                stepsList.add(convertToNormal(stepsDAO));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stepsList.isEmpty()?null:stepsList.get(0);
    }

    @Override
    public List<Steps> getAll() {
        List<Steps> stepsList = new ArrayList<Steps>();
        try {
            List<StepsDAO> stepsDAOList = mDatabaseHelper.getStepsDao().queryBuilder().query();
            for(StepsDAO stepsDAO : stepsDAOList){
                stepsList.add(convertToNormal(stepsDAO));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stepsList;
    }

    private StepsDAO convertToDao(Steps steps){
        StepsDAO stepsDao = new StepsDAO();
        stepsDao.setUserID(steps.getUserID());
        stepsDao.setCreatedDate(steps.getCreatedDate());
        stepsDao.setDate(steps.getDate());
        stepsDao.setSteps(steps.getSteps());
        stepsDao.setWalkSteps(steps.getWalkSteps());
        stepsDao.setRunSteps(steps.getRunSteps());
        stepsDao.setDistance(steps.getDistance());
        stepsDao.setCalories(steps.getCalories());
        stepsDao.setHourlySteps(steps.getHourlySteps());
        stepsDao.setHourlyDistance(steps.getHourlyDistance());
        stepsDao.setHourlyCalories(steps.getHourlyCalories());
        stepsDao.setInZoneTime(steps.getInZoneTime());
        stepsDao.setOutZoneTime(steps.getOutZoneTime());
        stepsDao.setNoActivityTime(steps.getNoActivityTime());
        stepsDao.setGoal(steps.getGoal());
        stepsDao.setRemarks(steps.getRemarks());
        return stepsDao;
    }

    private Steps convertToNormal(StepsDAO stepsDAO){
        Steps steps = new Steps(stepsDAO.getID(),stepsDAO.getUserID(),stepsDAO.getCreatedDate());
        steps.setDate(stepsDAO.getDate());
        steps.setSteps(stepsDAO.getSteps());
        steps.setWalkSteps(stepsDAO.getWalkSteps());
        steps.setRunSteps(stepsDAO.getRunSteps());
        steps.setDistance(stepsDAO.getDistance());
        steps.setCalories(stepsDAO.getCalories());
        steps.setHourlySteps(stepsDAO.getHourlySteps());
        steps.setHourlyDistance(stepsDAO.getHourlyDistance());
        steps.setHourlyCalories(stepsDAO.getHourlyCalories());
        steps.setInZoneTime(stepsDAO.getInZoneTime());
        steps.setOutZoneTime(stepsDAO.getOutZoneTime());
        steps.setNoActivityTime(stepsDAO.getNoActivityTime());
        steps.setGoal(stepsDAO.getGoal());
        return steps;
    }
}