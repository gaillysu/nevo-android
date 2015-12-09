package com.medcorp.nevo.database;

import android.test.AndroidTestCase;

import com.medcorp.nevo.ble.util.Optional;
import com.medcorp.nevo.database.entry.PresetsDatabaseHelper;
import com.medcorp.nevo.model.Preset;

/**
 * Created by gaillysu on 15/12/8.
 */
public class PresetsDatabaseHelperTest  extends AndroidTestCase {

    private PresetsDatabaseHelper db;
    private Preset addPreset;
    private Preset updatePreset;
    private Preset removePreset;


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        db = new PresetsDatabaseHelper(getContext());
        addPreset = new Preset("Low",false,5000);
        updatePreset = new Preset("Normal",true,10000);
        removePreset = new Preset("Player",false,20000);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testAdd()
    {
        Optional<Preset> thisPreset1 = db.add(addPreset);
        assertEquals(false,thisPreset1.isEmpty());
        addPreset = thisPreset1.get();

        Optional<Preset> thisPreset2 = db.get(addPreset.getId(),null);
        assertEquals(false,thisPreset2.isEmpty());

        assertEquals(addPreset.getLabel(),thisPreset2.get().getLabel());
        assertEquals(addPreset.getSteps(),thisPreset2.get().getSteps());
        assertEquals(addPreset.isStatus(),thisPreset2.get().isStatus());

    }
    public void testUpdate()
    {
        Optional<Preset> thisPreset1 = db.add(updatePreset);
        assertEquals(false,thisPreset1.isEmpty());
        updatePreset = thisPreset1.get();

        updatePreset.setStatus(!updatePreset.isStatus());
        updatePreset.setLabel("34terwfgw");
        updatePreset.setSteps((int) (Math.random() * 10000));

        assertEquals(true, db.update(updatePreset));

        Optional<Preset> thisPreset2 = db.get(updatePreset.getId(),null);
        assertEquals(false,thisPreset2.isEmpty());

        assertEquals(thisPreset2.get().isStatus(),updatePreset.isStatus());
        assertEquals(thisPreset2.get().getLabel(),updatePreset.getLabel());
        assertEquals(thisPreset2.get().getSteps(),updatePreset.getSteps());

    }
    public void testRemove()
    {
        Optional<Preset> thisPreset1 = db.add(removePreset);
        assertEquals(false,thisPreset1.isEmpty());
        removePreset = thisPreset1.get();

        assertEquals(true,db.remove(removePreset.getId(),null));

        Optional<Preset> thisPreset2 = db.get(removePreset.getId(),null);
        assertEquals(true,thisPreset2.isEmpty());

    }
}

