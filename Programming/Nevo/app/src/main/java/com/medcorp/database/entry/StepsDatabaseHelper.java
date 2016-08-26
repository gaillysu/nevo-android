package com.medcorp.database.entry;

import android.content.Context;

import com.medcorp.database.DatabaseHelper;
import com.medcorp.database.dao.StepsDAO;
import com.medcorp.model.DailySteps;
import com.medcorp.model.Steps;
import com.medcorp.util.CalendarWeekUtils;
import com.medcorp.util.TimeUtil;

import net.medcorp.library.ble.util.Optional;

import org.json.JSONArray;
import org.json.JSONException;

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
            if (stepsDAO != null) {
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
            List<StepsDAO> stepsDAOList = databaseHelper.getStepsDao().queryBuilder().where().eq(StepsDAO.fNevoUserID, object.getNevoUserID()).and().eq(StepsDAO.fDate, object.getDate()).query();
            if (stepsDAOList.isEmpty())
                return add(object) != null;
            StepsDAO daoobject = convertToDao(object);
            daoobject.setID(stepsDAOList.get(0).getID());
            result = databaseHelper.getStepsDao().update(daoobject);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result >= 0;
    }

    @Override
    public boolean remove(String userId, Date date) {
        try {
            List<StepsDAO> stepsDAOList = databaseHelper.getStepsDao().queryBuilder().where().eq(StepsDAO.fNevoUserID, userId).and().eq(StepsDAO.fDate, date.getTime()).query();
            if (!stepsDAOList.isEmpty()) {
                return databaseHelper.getStepsDao().delete(stepsDAOList) >= 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Optional<Steps>> get(String userId) {
        return getAll(userId);
    }

    @Override
    public Optional<Steps> get(String userId, Date date) {
        List<Optional<Steps>> stepsList = new ArrayList<Optional<Steps>>();
        try {
            List<StepsDAO> stepsDAOList = databaseHelper.getStepsDao().queryBuilder().where().eq(StepsDAO.fNevoUserID, userId).and().eq(StepsDAO.fDate, date.getTime()).query();
            for (StepsDAO stepsDAO : stepsDAOList) {
                Optional<Steps> stepsOptional = new Optional<>();
                stepsOptional.set(convertToNormal(stepsDAO));
                stepsList.add(stepsOptional);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stepsList.isEmpty() ? new Optional<Steps>() : stepsList.get(0);
    }

    @Override
    public List<Optional<Steps>> getAll(String userId) {
        List<Optional<Steps>> stepsList = new ArrayList<Optional<Steps>>();
        try {
            List<StepsDAO> stepsDAOList = databaseHelper.getStepsDao().queryBuilder().orderBy(StepsDAO.fDate, false).where().eq(StepsDAO.fNevoUserID, userId).query();
            for (StepsDAO stepsDAO : stepsDAOList) {
                Optional<Steps> stepsOptional = new Optional<>();
                stepsOptional.set(convertToNormal(stepsDAO));
                stepsList.add(stepsOptional);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stepsList;
    }

    public DailySteps getDailySteps(String userId , Date date){
        List<Optional<Steps> >  stepsDAOList = get(userId);
        int[] hours = new int[24];
        if(stepsDAOList.size()>0){
            try {
            JSONArray jsonArray = new JSONArray(stepsDAOList.get(0).get().getHourlySteps());

                for (int i = 0; i < jsonArray.length() && i < hours.length; i++) {
                    JSONArray stepsInHour = jsonArray.optJSONArray(i);
                    for(int j=0;j<stepsInHour.length();j++)
                    {
                        hours[i] += stepsInHour.optInt(j);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return new DailySteps(TimeUtil.getTime(date).getTime(),hours,stepsDAOList.size()>0?stepsDAOList.get(0).get().getGoal():10000);
    }

    public List<DailySteps> getThisWeekSteps(String userId, Date date) {
        List<DailySteps> thisWeekSteps = new ArrayList<>();
        CalendarWeekUtils calendar = new CalendarWeekUtils(date);
        for (long start = calendar.getWeekStartDate().getTime(); start <= calendar.getWeekEndDate().getTime(); start += 24 * 60 * 60 * 1000L) {
            thisWeekSteps.add(getDailySteps(userId,new Date(start)));
        }
        return thisWeekSteps;
    }

    public List<DailySteps> getLastWeekSteps(String userId,Date date){
        List<DailySteps> lastWeekSteps = new ArrayList<>();
        CalendarWeekUtils calendar = new CalendarWeekUtils(date);
        for(long start = calendar.getLastWeekStart().getTime();start <= calendar.getLastWeekEnd().getTime();start += 24*60*60*1000L){
            lastWeekSteps.add(getDailySteps(userId,new Date(start)));
        }
        return lastWeekSteps;
    }

    public List<DailySteps> getLastMOnthSteps(String userId , Date date){
        List<DailySteps> lastMonthSteps = new ArrayList<>();
        CalendarWeekUtils calendar = new CalendarWeekUtils(date);
        for(long start = calendar.getMonthStartDate().getTime();start <= calendar.getMonthEndDate().getTime();start +=  24*60*60*1000L){
            lastMonthSteps.add(getDailySteps(userId,new Date(start)));
        }
        return lastMonthSteps;
    }

    public List<Steps> getNeedSyncSteps(String userId) {
        List<Steps> stepsList = new ArrayList<Steps>();
        try {
            List<StepsDAO> stepsDAOList = databaseHelper.getStepsDao().queryBuilder().orderBy(StepsDAO.fDate, false).where().eq(StepsDAO.fNevoUserID, userId).and().eq(StepsDAO.fValidicRecordID, "0").query();
            for (StepsDAO stepsDAO : stepsDAOList) {
                stepsList.add(convertToNormal(stepsDAO));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stepsList;
    }

    public boolean isFoundInLocalSteps(int activity_id) {
        try {
            List<StepsDAO> stepsDAOList = databaseHelper.getStepsDao().queryBuilder().where().eq(StepsDAO.fID, activity_id).query();
            return !stepsDAOList.isEmpty();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private StepsDAO convertToDao(Steps steps) {
        StepsDAO stepsDao = new StepsDAO();
        stepsDao.setID(steps.getiD());
        stepsDao.setNevoUserID(steps.getNevoUserID());
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
        stepsDao.setValidicRecordID(steps.getValidicRecordID());
        stepsDao.setDistanceGoal(steps.getDistanceGoal());
        stepsDao.setCaloriesGoal(steps.getCaloriesGoal());
        stepsDao.setActiveTimeGoal(steps.getActiveTimeGoal());
        stepsDao.setGoalReached(steps.getGoalReached());
        return stepsDao;
    }

    private Steps convertToNormal(StepsDAO stepsDAO) {
        Steps steps = new Steps(stepsDAO.getCreatedDate());
        steps.setNevoUserID(stepsDAO.getNevoUserID());
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
        steps.setValidicRecordID(stepsDAO.getValidicRecordID());
        steps.setDistanceGoal(stepsDAO.getDistanceGoal());
        steps.setCaloriesGoal(stepsDAO.getCaloriesGoal());
        steps.setActiveTimeGoal(stepsDAO.getActiveTimeGoal());
        steps.setGoalReached(stepsDAO.getGoalReached());
        return steps;
    }

    @Override
    public List<Steps> convertToNormalList(List<Optional<Steps>> optionals) {
        List<Steps> stepsList = new ArrayList<>();
        for (Optional<Steps> stepsOptional : optionals) {
            if (stepsOptional.notEmpty()) {
                stepsList.add(stepsOptional.get());
            }
        }
        return stepsList;
    }

}
