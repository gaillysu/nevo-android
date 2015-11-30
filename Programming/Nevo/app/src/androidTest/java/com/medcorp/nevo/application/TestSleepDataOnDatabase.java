package com.medcorp.nevo.application;

import android.test.AndroidTestCase;

import com.medcorp.nevo.database.entry.SleepDatabaseHelper;
import com.medcorp.nevo.model.Sleep;
import com.medcorp.nevo.model.SleepData;
import com.medcorp.nevo.util.SleepDataHandler;
import com.medcorp.nevo.util.SleepSorter;

import java.util.Collections;
import java.util.List;

/**
 * Created by karl-john on 30/11/15.
 */
public class TestSleepDataOnDatabase extends AndroidTestCase
{

    private SleepDatabaseHelper helper;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        helper =new SleepDatabaseHelper(getContext());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testFirst(){
        List<Sleep> sleepList = helper.getAll();
        assertEquals(9, sleepList.size());
        SleepSorter sorter = new SleepSorter();
        Collections.sort(sleepList, sorter);
        SleepDataHandler handler = new SleepDataHandler(sleepList);
        handler.setLoggingOn(true);
        List<SleepData> sleepDataList = handler.getSleepData();
        assertEquals(7,sleepDataList.size());

    }
}
