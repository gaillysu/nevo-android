package com.medcorp.nevo.database;

import android.test.AndroidTestCase;

import com.medcorp.database.entry.AlarmDatabaseHelper;
import com.medcorp.model.Alarm;

import net.medcorp.library.ble.util.Optional;

/**
 * Created by gaillysu on 15/12/8.
 */
public class AlarmDatabaseHelperTest extends AndroidTestCase {

    private AlarmDatabaseHelper db;
    private Alarm addAlarm;
    private Alarm updateAlarm;
    private Alarm removeAlarm;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        db = new AlarmDatabaseHelper(getContext());
        addAlarm = new Alarm(7,30, (byte) 1,"wake up alarm",(byte)0,(byte)0);
        updateAlarm = new Alarm(12,30, (byte) 1,"lunch time",(byte)0,(byte)0);
        removeAlarm = new Alarm(22,30, (byte) 0,"sleep time",(byte)0,(byte)0);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testAdd()
    {
        Optional<Alarm> thisAlarm1 = db.add(addAlarm);
        assertEquals(false,thisAlarm1.isEmpty());

        //here must set "addAlarm" for get the alarm ID that is generated by database.
        addAlarm = thisAlarm1.get();

        Optional<Alarm> thisAlarm2 = db.get(addAlarm.getId()).get(0);
        assertEquals(false, thisAlarm2.isEmpty());

        assertEquals(addAlarm.getWeekDay(),thisAlarm2.get().getWeekDay());
        assertEquals(addAlarm.getLabel(),thisAlarm2.get().getLabel());
        assertEquals(addAlarm.getMinute(),thisAlarm2.get().getMinute());
        assertEquals(addAlarm.getHour(),thisAlarm2.get().getHour());

    }
    public void testUpdate()
    {
        Optional<Alarm> thisAlarm1 = db.add(updateAlarm);
        assertEquals(false,thisAlarm1.isEmpty());
        updateAlarm = thisAlarm1.get();

        updateAlarm.setWeekDay((byte) (updateAlarm.getWeekDay()>0?0:1));
        updateAlarm.setLabel("wetew5234t");
        updateAlarm.setHour(updateAlarm.getHour() + 1);
        updateAlarm.setMinute(updateAlarm.getMinute() + 1);
        assertEquals(true, db.update(updateAlarm));

        Optional<Alarm> thisAlarm2 = db.get(updateAlarm.getId()).get(0);
        assertEquals(false, thisAlarm2.isEmpty());

        assertEquals(thisAlarm2.get().getWeekDay(),updateAlarm.getWeekDay());
        assertEquals(thisAlarm2.get().getLabel(),updateAlarm.getLabel());
        assertEquals(thisAlarm2.get().getHour(),updateAlarm.getHour());
        assertEquals(thisAlarm2.get().getMinute(),updateAlarm.getMinute());


    }
    public void testRemove()
    {
        Optional<Alarm> thisAlarm1 = db.add(removeAlarm);
        assertEquals(false,thisAlarm1.isEmpty());
        removeAlarm = thisAlarm1.get();

        assertEquals(true,db.remove(removeAlarm.getId()));

        Optional<Alarm> thisAlarm2 = db.get(removeAlarm.getId()).get(0);
        assertEquals(true,thisAlarm2.isEmpty());
    }

}