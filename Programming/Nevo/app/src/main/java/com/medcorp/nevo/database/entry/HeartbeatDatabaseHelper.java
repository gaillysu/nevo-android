package com.medcorp.nevo.database.entry;

import com.j256.ormlite.dao.Dao;
import com.medcorp.nevo.application.ApplicationModel;
import com.medcorp.nevo.database.DatabaseHelper;
import com.medcorp.nevo.database.dao.HeartbeatDAO;
import com.medcorp.nevo.model.Heartbeat;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by karl-john on 17/11/15.
 */
public class HeartbeatDatabaseHelper implements iEntryDatabaseHelper<Heartbeat> {

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
            result = mDatabaseHelper.getHeartbeatDao().create(convertToDao(object));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result>=0;
    }

    @Override
    public boolean update(Heartbeat object) {
        int result = -1;
        try {
            List<HeartbeatDAO> heartbeatDAOList = mDatabaseHelper.getHeartbeatDao().queryBuilder().where().eq(HeartbeatDAO.fUserID, object.getUserID()).and().eq(HeartbeatDAO.fDate,object.getDate()).query();
            if(heartbeatDAOList.isEmpty()) return add(object);
            HeartbeatDAO daoobject = convertToDao(object);
            daoobject.setID(heartbeatDAOList.get(0).getID());
            result = mDatabaseHelper.getHeartbeatDao().update(daoobject);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result>=0;
    }

    @Override
    public boolean remove(int userid,Date date) {
        try {
            List<HeartbeatDAO> heartbeatDAOList = mDatabaseHelper.getHeartbeatDao().queryBuilder().where().eq(HeartbeatDAO.fUserID, userid).and().eq(HeartbeatDAO.fDate,date.getTime()).query();
            if(!heartbeatDAOList.isEmpty()) mDatabaseHelper.getHeartbeatDao().delete(heartbeatDAOList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Heartbeat get(int userid,Date date) {
        List<Heartbeat> heartbeatList = new ArrayList<Heartbeat>();
        try {
            List<HeartbeatDAO> heartbeatDAOList = mDatabaseHelper.getHeartbeatDao().queryBuilder().where().eq(HeartbeatDAO.fUserID, userid).and().eq(HeartbeatDAO.fDate,date.getTime()).query();
            for(HeartbeatDAO heartBeatDao : heartbeatDAOList){
                heartbeatList.add(convertToNormal(heartBeatDao));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return heartbeatList.isEmpty()?null:heartbeatList.get(0);
    }

    @Override
    public List<Heartbeat> getAll() {
        List<Heartbeat> heartbeatList = new ArrayList<Heartbeat>();
        try {
            List<HeartbeatDAO> heartbeatDAOList= mDatabaseHelper.getHeartbeatDao().queryBuilder().query();
            for(HeartbeatDAO heartBeatDao : heartbeatDAOList){
                heartbeatList.add(convertToNormal(heartBeatDao));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return heartbeatList;
    }

    private HeartbeatDAO convertToDao(Heartbeat heartbeat){
        HeartbeatDAO heartbeatDAO = new HeartbeatDAO();
        heartbeatDAO.setUserID(heartbeat.getUserID());
        heartbeatDAO.setRemarks(heartbeat.getRemarks());
        heartbeatDAO.setAvgHrm(heartbeat.getAvgHrm());
        heartbeatDAO.setCreatedDate(heartbeat.getCreatedDate());
        heartbeatDAO.setDate(heartbeat.getDate());
        heartbeatDAO.setMaxHrm(heartbeat.getMaxHrm());
        return heartbeatDAO;
    }

    private Heartbeat convertToNormal(HeartbeatDAO heartbeatDAO){
        Heartbeat heartbeat = new Heartbeat(heartbeatDAO.getID(),heartbeatDAO.getUserID(),heartbeatDAO.getCreatedDate());
        heartbeat.setRemarks(heartbeatDAO.getRemarks());
        heartbeat.setAvgHrm(heartbeatDAO.getAvgHrm());
        heartbeat.setDate(heartbeatDAO.getDate());
        heartbeat.setMaxHrm(heartbeatDAO.getMaxHrm());
        return heartbeat;
    }
}
