package com.medcorp.nevo.database.entry;

import com.medcorp.nevo.application.ApplicationModel;
import com.medcorp.nevo.database.DatabaseHelper;
import com.medcorp.nevo.database.dao.AlarmDAO;
import com.medcorp.nevo.model.Alarm;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by karl-john on 17/11/15.
 */
public class AlarmDatabaseHelper implements iEntryDatabaseHelper<Alarm> {

    private DatabaseHelper databaseHelper;

    public AlarmDatabaseHelper() {
        databaseHelper = DatabaseHelper.getInstance(ApplicationModel.getApplicationModel());
    }

    @Override
    public boolean add(Alarm object) {
        int result = -1;
        try {
            result = databaseHelper.getAlarmDao().create(convertToDao(object));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result>=0;
    }

    @Override
    public boolean update(Alarm object) {
        int result = -1;
        try {
            List<AlarmDAO> alarmDAOList = databaseHelper.getAlarmDao().queryBuilder().where().eq(AlarmDAO.iDString, object.getId()).query();
            if(alarmDAOList.isEmpty()) return add(object);
            AlarmDAO alarmDAO = convertToDao(object);
            alarmDAO.setID(alarmDAOList.get(0).getID());
            result = databaseHelper.getAlarmDao().update(alarmDAO);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result>=0;
    }

    @Override
    public boolean remove(int alarmId,Date date) {
        try {
            List<AlarmDAO> alarmDAOList = databaseHelper.getAlarmDao().queryBuilder().where().eq(AlarmDAO.iDString, alarmId).query();
            if(!alarmDAOList.isEmpty()) databaseHelper.getAlarmDao().delete(alarmDAOList);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Alarm  get(int alarmId,Date date) {
        List<Alarm> alarmList = new ArrayList<Alarm>();
        try {
            List<AlarmDAO> alarmDAOList = databaseHelper.getAlarmDao().queryBuilder().where().eq(AlarmDAO.iDString, alarmId).query();
            for(AlarmDAO alarmDAO: alarmDAOList) {
                alarmList.add(convertToNormal(alarmDAO));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return alarmList.isEmpty()?null: alarmList.get(0);
    }

    @Override
    public List<Alarm> getAll() {
        List<Alarm> alarmList = new ArrayList<Alarm>();
        try {
            List<AlarmDAO> alarmDAOList  = databaseHelper.getAlarmDao().queryBuilder().query();
            for(AlarmDAO alarmDAO: alarmDAOList) {
                alarmList.add(convertToNormal(alarmDAO));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return alarmList;
    }

    private AlarmDAO convertToDao(Alarm alarm){
        AlarmDAO alarmDAO = new AlarmDAO();
        alarmDAO.setAlarm(alarm.getHour() + ":" + alarm.getMinute());
        alarmDAO.setID(alarm.getId());
        alarmDAO.setLabel(alarm.getLabel());
        alarmDAO.setEnabled(alarm.isEnable());
        return new AlarmDAO();
    }

    private Alarm convertToNormal(AlarmDAO alarmDAO){
        String[] splittedAlarmStrings = alarmDAO.getAlarm().split(":");
        int hour = Integer.parseInt(splittedAlarmStrings[0]);
        int minutes = Integer.parseInt(splittedAlarmStrings[1]);
        return new Alarm(alarmDAO.getID(),hour,minutes,alarmDAO.isEnabled(), alarmDAO.getLabel());
    }
}