package com.medcorp.nevo.database;

import android.test.AndroidTestCase;

import com.medcorp.nevo.database.entry.SleepDatabaseHelper;

/**
 * Created by Karl on 12/7/15.
 */
public class SleepDatabaseHelperTest extends AndroidTestCase {

    private SleepDatabaseHelper db;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        db = new SleepDatabaseHelper(getContext());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
