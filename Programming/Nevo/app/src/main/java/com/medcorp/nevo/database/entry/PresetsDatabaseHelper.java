package com.medcorp.nevo.database.entry;

import android.content.Context;

import com.medcorp.nevo.ble.util.Optional;
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
    public Optional<Preset> add(Preset object) {
        Optional<Preset> presetOptional = new Optional<>();
        try {
            PresetDAO res  = databaseHelper.getPresetDao().createIfNotExists(convertToDao(object));
            if(res != null)
            {
                    presetOptional.set(convertToNormal(res));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return presetOptional;
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
            if(!presetDAOList.isEmpty())
            {
              return  databaseHelper.getPresetDao().delete(presetDAOList)>=0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Optional<Preset>> get(int presetId) {
        List<Optional<Preset>> presetList = new ArrayList<Optional<Preset>>();
        try {
            List<PresetDAO> presetDAOList = databaseHelper.getPresetDao().queryBuilder().where().eq(PresetDAO.iDString, presetId).query();
            for(PresetDAO presetDAO: presetDAOList) {
                Optional<Preset> presetOptional = new Optional<>();
                presetOptional.set(convertToNormal(presetDAO));
                presetList.add(presetOptional);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return presetList;
    }

    @Override
    public Optional<Preset> get(int presetId,Date date) {
        List<Optional<Preset>> presetList = get(presetId);
        return presetList.isEmpty()? new Optional<Preset>(): presetList.get(0);
    }

    @Override
    public List<Optional<Preset>> getAll() {
        List<Optional<Preset>> presetList = new ArrayList<Optional<Preset>>();
        try {
            List<PresetDAO> presetDAOList  = databaseHelper.getPresetDao().queryBuilder().query();
            for(PresetDAO presetDAO: presetDAOList) {
                Optional<Preset> presetOptional = new Optional<>();
                presetOptional.set(convertToNormal(presetDAO));
                presetList.add(presetOptional);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return presetList;
    }

    private PresetDAO convertToDao(Preset preset){
        PresetDAO presetDAO = new PresetDAO();
        presetDAO.setLabel(preset.getLabel());
        presetDAO.setSteps(preset.getSteps());
        presetDAO.setEnabled(preset.isStatus());
        return presetDAO;
    }

    private Preset convertToNormal(PresetDAO presetDAO){
        Preset preset =  new Preset(presetDAO.getLabel(),presetDAO.isEnabled(), presetDAO.getSteps());
        preset.setId(presetDAO.getID());
        return preset;
    }


    @Override
    public List<Preset> convertToNormalList(List<Optional<Preset>> optionals) {
        List<Preset> presetList = new ArrayList<>();
        for (Optional<Preset> presetOptional: optionals) {
            if (presetOptional.notEmpty()){
                presetList.add(presetOptional.get());
            }
        }
        return presetList;
    }
}