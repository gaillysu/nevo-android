package com.medcorp.nevo.database.entry;

import android.content.Context;

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

    private DatabaseHelper databaseHelper;

    public HeartbeatDatabaseHelper(Context context) {
        databaseHelper = DatabaseHelper.getInstance(context);
    }

    @Override
    public boolean add(Heartbeat object) {
        int result = -1;
        try {
            result = databaseHelper.getHeartbeatDao().create(convertToDao(object));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result>=0;
    }

    @Override
    public boolean update(Heartbeat object) {
        int result = -1;
        try {
            List<HeartbeatDAO> heartbeatDAOList = databaseHelper.getHeartbeatDao().queryBuilder().where().eq(HeartbeatDAO.fUserID, object.getUserID()).and().eq(HeartbeatDAO.fDate,object.getDate()).query();
            if(heartbeatDAOList.isEmpty()) return add(object);
            HeartbeatDAO daoobject = convertToDao(object);
            daoobject.setID(heartbeatDAOList.get(0).getID());
            result = databaseHelper.getHeartbeatDao().update(daoobject);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result>=0;
    }

    @Override
    public boolean remove(int userId,Date date) {
        try {
            List<HeartbeatDAO> heartbeatDAOList = databaseHelper.getHeartbeatDao().queryBuilder().where().eq(HeartbeatDAO.fUserID, userId).and().eq(HeartbeatDAO.fDate,date.getTime()).query();
            if(!heartbeatDAOList.isEmpty()) databaseHelper.getHeartbeatDao().delete(heartbeatDAOList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Heartbeat get(int userId,Date date) {
        List<Heartbeat> heartbeatList = new ArrayList<Heartbeat>();
        try {
            List<HeartbeatDAO> heartbeatDAOList = databaseHelper.getHeartbeatDao().queryBuilder().where().eq(HeartbeatDAO.fUserID, userId).and().eq(HeartbeatDAO.fDate,date.getTime()).query();
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
            List<HeartbeatDAO> heartbeatDAOList= databaseHelper.getHeartbeatDao().queryBuilder().query();
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
        heartbeatDAO.setID(heartbeat.getiD());
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
