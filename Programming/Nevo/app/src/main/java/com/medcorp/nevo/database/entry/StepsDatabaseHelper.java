package com.medcorp.nevo.database.entry;

import android.content.Context;

import com.medcorp.nevo.ble.util.Optional;
import com.medcorp.nevo.database.DatabaseHelper;
import com.medcorp.nevo.database.dao.StepsDAO;
import com.medcorp.nevo.model.Steps;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by karl-john on 17/11/15.
 */
public class StepsDatabaseHelper implements iEntryDatabaseHelper<Steps> {

    private DatabaseHelper databaseHelper;

    public StepsDatabaseHelper(Context context) {
        databaseHelper = DatabaseHelper.getInstance(context);
    }

    @Override
    public Optional<Steps> add(Steps object) {
        Optional<Steps> stepsOptional = new Optional<>();
        try {
            StepsDAO stepsDAO = databaseHelper.getStepsDao().createIfNotExists(convertToDao(object));
            if(stepsDAO != null)
            {
                stepsOptional.set(convertToNormal(stepsDAO));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stepsOptional;
    }

    @Override
    public boolean update(Steps object) {

        int result = -1;
        try {
            List<StepsDAO> stepsDAOList = databaseHelper.getStepsDao().queryBuilder().where().eq(StepsDAO.fUserID, object.getUserID()).and().eq(StepsDAO.fDate, object.getDate()).query();
            if(stepsDAOList.isEmpty()) return add(object)!=null;
            StepsDAO daoobject = convertToDao(object);
            daoobject.setID(stepsDAOList.get(0).getID());
            result = databaseHelper.getStepsDao().update(daoobject);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result>=0;
    }

    @Override
    public boolean remove(int userId,Date date) {
        try {
            List<StepsDAO> stepsDAOList = databaseHelper.getStepsDao().queryBuilder().where().eq(StepsDAO.fUserID, userId).and().eq(StepsDAO.fDate,date.getTime()).query();
            if(!stepsDAOList.isEmpty())
            {
                return databaseHelper.getStepsDao().delete(stepsDAOList)>=0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Optional<Steps>> get(int userId) {
        return null;
    }

    @Override
    public Optional<Steps>  get(int userId,Date date) {
        List<Optional<Steps> > stepsList = new ArrayList<Optional<Steps> >();
        try {
            List<StepsDAO> stepsDAOList = databaseHelper.getStepsDao().queryBuilder().where().eq(StepsDAO.fUserID, userId).and().eq(StepsDAO.fDate, date.getTime()).query();
            for(StepsDAO stepsDAO : stepsDAOList){
                Optional<Steps> stepsOptional = new Optional<>();
                stepsOptional.set(convertToNormal(stepsDAO));
                stepsList.add(stepsOptional);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stepsList.isEmpty()? new Optional<Steps>() : stepsList.get(0);
    }

    @Override
    public List<Optional<Steps> > getAll() {
        List<Optional<Steps> > stepsList = new ArrayList<Optional<Steps> >();
        try {
            List<StepsDAO> stepsDAOList = databaseHelper.getStepsDao().queryBuilder().query();
            for(StepsDAO stepsDAO : stepsDAOList){
                Optional<Steps> stepsOptional = new Optional<>();
                stepsOptional.set(convertToNormal(stepsDAO));
                stepsList.add(stepsOptional);
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
        stepsDao.setWalkDistance(steps.getWalkDistance());
        stepsDao.setRunDistance(steps.getRunDistance());
        stepsDao.setWalkDuration(steps.getWalkDuration());
        stepsDao.setRunDuration(steps.getRunDuration());
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
        Steps steps = new Steps(stepsDAO.getCreatedDate());
        steps.setUserID(stepsDAO.getUserID());
        steps.setiD(stepsDAO.getID());
        steps.setDate(stepsDAO.getDate());
        steps.setSteps(stepsDAO.getSteps());
        steps.setWalkSteps(stepsDAO.getWalkSteps());
        steps.setRunSteps(stepsDAO.getRunSteps());
        steps.setDistance(stepsDAO.getDistance());
        steps.setWalkDistance(stepsDAO.getWalkDistance());
        steps.setRunDistance(stepsDAO.getRunDistance());
        steps.setWalkDuration(stepsDAO.getWalkDuration());
        steps.setRunDuration(stepsDAO.getRunDuration());
        steps.setCalories(stepsDAO.getCalories());
        steps.setHourlySteps(stepsDAO.getHourlySteps());
        steps.setHourlyDistance(stepsDAO.getHourlyDistance());
        steps.setHourlyCalories(stepsDAO.getHourlyCalories());
        steps.setInZoneTime(stepsDAO.getInZoneTime());
        steps.setOutZoneTime(stepsDAO.getOutZoneTime());
        steps.setNoActivityTime(stepsDAO.getNoActivityTime());
        steps.setGoal(stepsDAO.getGoal());
        steps.setRemarks(stepsDAO.getRemarks());
        return steps;
    }

    @Override
    public List<Steps> convertToNormalList(List<Optional<Steps>> optionals) {
        List<Steps> stepsList = new ArrayList<>();
        for (Optional<Steps> stepsOptional: optionals) {
            if (stepsOptional.notEmpty()){
                stepsList.add(stepsOptional.get());
            }
        }
        return stepsList;
    }
}
