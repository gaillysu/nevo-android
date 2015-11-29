package com.medcorp.nevo.application;

/**
 * Created by Karl on 11/27/15.
 */

import android.util.Log;

import com.medcorp.nevo.model.Sleep;
import com.medcorp.nevo.model.SleepData;
import com.medcorp.nevo.util.SleepDataHandler;

import junit.framework.TestCase;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Karl on 11/27/15.
 */
public class TestSleepData extends TestCase {

    private Sleep sleepAfterTwelve;
    private Sleep sleepOnTwelve;
    private Sleep sleepBeforeTwelve;
    private Sleep sleepBeforeTwelvePt2;
    private Sleep strangeSleep;


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        sleepAfterTwelve = new Sleep(1, 1, 1448467200000l);
        sleepAfterTwelve.setHourlyWake("[0, 5, 0, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]");
        sleepAfterTwelve.setHourlyLight("[0, 10, 20, 20, 20, 40, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]");
        sleepAfterTwelve.setHourlyDeep("[0, 0, 0, 40, 40, 40, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]");

        sleepOnTwelve = new Sleep(1, 1, 1448467200000l);
        sleepOnTwelve.setHourlyWake("[5, 0, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,0]");
        sleepOnTwelve.setHourlyLight("[10, 20, 20, 20, 40, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,0]");
        sleepOnTwelve.setHourlyDeep("[0, 0, 40, 40, 40, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,0]");

        sleepBeforeTwelve = new Sleep(1, 1, 1448467200000l);
        sleepBeforeTwelve.setHourlyWake("[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,10]");
        sleepBeforeTwelve.setHourlyLight("[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,20]");
        sleepBeforeTwelve.setHourlyDeep("[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,0]");
        sleepBeforeTwelve.setDate(1448467200000l);

        sleepBeforeTwelvePt2 = new Sleep(1, 1, 1448553600000l);
        sleepBeforeTwelvePt2.setHourlyWake("[0, 0, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,0]");
        sleepBeforeTwelvePt2.setHourlyLight("[10, 00, 20, 20, 40, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,0]");
        sleepBeforeTwelvePt2.setHourlyDeep("[50, 60, 40, 40, 40, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,0]");
        sleepBeforeTwelvePt2.setDate(1448553600000l);

        strangeSleep = new Sleep(1, 1, 1448467200000l);
        strangeSleep.setHourlyWake("[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 10, 0, 0, 0, 5,0]");
        strangeSleep.setHourlyLight("[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 30, 30, 30, 10, 0,0]");
        strangeSleep.setHourlyDeep("[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 30, 30, 50, 0,0]");
        strangeSleep.setDate(1448467200000l);

    }

    @Test
    public void testEverything(){
        List<Sleep> sleepList = new ArrayList<>();
        sleepList.add(sleepAfterTwelve);
        sleepList.add(sleepOnTwelve);
        sleepList.add(sleepBeforeTwelve);
        sleepList.add(sleepBeforeTwelvePt2);
        sleepList.add(strangeSleep);
        SleepDataHandler handler = new SleepDataHandler(sleepList);
        List<SleepData> sleepDataList = handler.getSleepData();
        assertEquals(4, sleepDataList.size());
    }

    @Test
    public void testSleepAfterTwelve() {
        Log.w("Karl", "===== testSleepAfterTwelve =====");
        List<Sleep> sleepList = new ArrayList<>();
        sleepList.add(sleepAfterTwelve);
        SleepDataHandler handler = new SleepDataHandler(sleepList);
        List<SleepData> sleepDataList = handler.getSleepData();
        assertEquals(1, sleepDataList.size());
        SleepData sleepData = sleepDataList.get(0);
        assertEquals(10, sleepData.getAwake());
        assertEquals(120, sleepData.getDeepSleep());
        assertEquals(110, sleepData.getLightSleep());
        Log.w("Karl", "===== testSleepAfterTwelve =====");
    }

    @Test
    public void testSleepOnTwelve() {
        Log.w("Karl", "===== testSleepOnTwelve =====");
        List<Sleep> sleepList = new ArrayList<>();
        sleepList.add(sleepAfterTwelve);
        SleepDataHandler handler = new SleepDataHandler(sleepList);
        List<SleepData> sleepDataList = handler.getSleepData();
        assertEquals(1, sleepDataList.size());
        SleepData sleepData = sleepDataList.get(0);
        assertEquals(10, sleepData.getAwake());
        assertEquals(120, sleepData.getDeepSleep());
        assertEquals(110, sleepData.getLightSleep());
        Log.w("Karl", "===== testSleepOnTwelve =====");
    }

    @Test
    public void testSleepBeforeTwelve() {
        Log.w("Karl", "===== testSleepBeforeTwelve =====");
        List<Sleep> sleepList = new ArrayList<>();
        sleepList.add(sleepBeforeTwelve);
        sleepList.add(sleepBeforeTwelvePt2);
        SleepDataHandler handler = new SleepDataHandler(sleepList);
        List<SleepData> sleepDataList = handler.getSleepData();
        assertEquals(1, sleepDataList.size());
        SleepData sleepData = sleepDataList.get(0);
        assertEquals(15, sleepData.getAwake());
        assertEquals(230, sleepData.getDeepSleep());
        assertEquals(110, sleepData.getLightSleep());
        Log.w("Karl", "===== testSleepBeforeTwelve =====");
    }

    @Test
    public void testStrangeData() {
        Log.w("Karl", "===== testStrangeData =====");
        List<Sleep> sleepList = new ArrayList<>();
        sleepList.add(sleepBeforeTwelve);
        sleepList.add(sleepBeforeTwelvePt2);
        sleepList.add(sleepBeforeTwelve);
        SleepDataHandler handler = new SleepDataHandler(sleepList);
        List<SleepData> sleepDataList = handler.getSleepData();
        assertEquals(2, sleepDataList.size());
        SleepData sleepData = sleepDataList.get(0);
        assertEquals(15, sleepData.getAwake());
        assertEquals(230, sleepData.getDeepSleep());
        assertEquals(110, sleepData.getLightSleep());
        SleepData sleepData2 = sleepDataList.get(1);
        assertEquals(10,sleepData2.getAwake());
        assertEquals(0, sleepData2.getDeepSleep());
        assertEquals(20, sleepData2.getLightSleep());
        Log.w("Karl", "===== testStrangeData =====");
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

}
