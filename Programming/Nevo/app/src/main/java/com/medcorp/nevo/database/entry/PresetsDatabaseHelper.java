package com.medcorp.nevo.database.entry;

import android.content.Context;

import com.medcorp.nevo.database.DatabaseHelper;
import com.medcorp.nevo.database.dao.PresetDAO;
import com.medcorp.nevo.model.Preset;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by karl-john on 17/11/15.
 */
public class PresetsDatabaseHelper implements iEntryDatabaseHelper<Preset> {

    private DatabaseHelper databaseHelper;

    public PresetsDatabaseHelper(Context context) {
        databaseHelper = DatabaseHelper.getInstance(context);
    }

    @Override
    public Preset add(Preset object) {
        try {
            PresetDAO res  = databaseHelper.getPresetDao().createIfNotExists(convertToDao(object));
            if(res != null)
            {
                return convertToNormal(res);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean update(Preset object) {
        int result = -1;
        try {
            List<PresetDAO> presetDAOList = databaseHelper.getPresetDao().queryBuilder().where().eq(PresetDAO.iDString, object.getId()).query();
            if(presetDAOList.isEmpty()) return add(object)!=null;
            PresetDAO presetDAO = convertToDao(object);
            presetDAO.setID(presetDAOList.get(0).getID());
            result = databaseHelper.getPresetDao().update(presetDAO);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result>=0;
    }

    @Override
    public boolean remove(int presetId,Date date) {
        try {
            List<PresetDAO> presetDAOList = databaseHelper.getPresetDao().queryBuilder().where().eq(PresetDAO.iDString, presetId).query();
            if(!presetDAOList.isEmpty()) databaseHelper.getPresetDao().delete(presetDAOList);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Preset get(int presetId,Date date) {
        List<Preset> presetList = new ArrayList<Preset>();
        try {
            List<PresetDAO> presetDAOList = databaseHelper.getPresetDao().queryBuilder().where().eq(PresetDAO.iDString, presetId).query();
            for(PresetDAO presetDAO: presetDAOList) {
                presetList.add(convertToNormal(presetDAO));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return presetList.isEmpty()?null: presetList.get(0);
    }

    @Override
    public List<Preset> getAll() {
        List<Preset> presetList = new ArrayList<Preset>();
        try {
            List<PresetDAO> presetDAOList  = databaseHelper.getPresetDao().queryBuilder().query();
            for(PresetDAO presetDAO: presetDAOList) {
                presetList.add(convertToNormal(presetDAO));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return presetList;
    }

    private PresetDAO convertToDao(Preset preset){
        PresetDAO presetDAO = new PresetDAO();
        presetDAO.setID(preset.getId());
        presetDAO.setLabel(preset.getLabel());
        presetDAO.setSteps(preset.getSteps());
        return new PresetDAO();
    }

    private Preset convertToNormal(PresetDAO presetDAO){
        return new Preset(presetDAO.getID(),presetDAO.getLabel(),presetDAO.isEnabled(), presetDAO.getSteps());
    }
}