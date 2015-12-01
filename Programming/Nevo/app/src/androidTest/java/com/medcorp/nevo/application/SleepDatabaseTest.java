package com.medcorp.nevo.application;

import android.test.AndroidTestCase;

import com.medcorp.nevo.database.entry.SleepDatabaseHelper;

/**
 * Created by Karl on 11/27/15.
 */
public class SleepDatabaseTest extends AndroidTestCase {


    private SleepDatabaseHelper helper;
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        helper = new SleepDatabaseHelper(getContext());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testSleepData(){
        assertEquals(5,5);
    }
}
