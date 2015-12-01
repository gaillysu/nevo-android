package com.medcorp.nevo.application;

/**
 * Created by Karl on 11/27/15.
 */

import android.util.Log;

import com.medcorp.nevo.database.entry.SleepDatabaseHelper;
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
    private Sleep testData4;
    private Sleep testData5;
    private Sleep testData6;
    private Sleep testData7;
    private Sleep testData8;
    private Sleep testData9;
    private Sleep testData10;
    private Sleep testData11;
    private Sleep testData12;


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

        testData4 = new Sleep(1,1,1448812800000l);;
        testData4.setDate(1448812800000l);
        testData4.setTotalSleepTime(459);
        testData4.setTotalWakeTime(70);
        testData4.setTotalLightTime(389);
        testData4.setTotalDeepTime(0);
        testData4.setHourlyWake("[10, 0, 10, 0, 10, 10, 10, 11, 9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]");
        testData4.setHourlyLight("[2, 60, 50, 60, 50, 50, 50, 49, 18, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]");
        testData4.setHourlyDeep("[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]") ;

        testData5 = new Sleep(1,1,1448726400000l);;
        testData5.setDate(1448726400000l);
        testData5.setTotalSleepTime(29);
        testData5.setTotalWakeTime(10);
        testData5.setTotalLightTime(19);
        testData5.setTotalDeepTime(0);
        testData5.setHourlyWake("[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 10]");
        testData5.setHourlyLight("[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 19]");
        testData5.setHourlyDeep("[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]") ;

        testData6 = new Sleep(1,1,1448640000000l);;
        testData6.setDate(1448640000000l);
        testData6.setTotalSleepTime(425);
        testData6.setTotalWakeTime(0);
        testData6.setTotalLightTime(225);
        testData6.setTotalDeepTime(200);
        testData6.setHourlyWake("[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]");
        testData6.setHourlyLight("[23, 50, 27, 23, 50, 27, 23, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]");
        testData6.setHourlyDeep("[37, 10, 33, 37, 10, 33, 37, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]") ;

        testData7 = new Sleep(1,1,1448553600000l);;
        testData7.setDate(1448553600000l);
        testData7.setTotalSleepTime(37);
        testData7.setTotalWakeTime(10);
        testData7.setTotalLightTime(27);
        testData7.setTotalDeepTime(0);
        testData7.setHourlyWake("[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 10]");
        testData7.setHourlyLight("[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 27]");
        testData7.setHourlyDeep("[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]") ;

        testData8 = new Sleep(1,1,1448467200000l);;
        testData8.setDate(1448467200000l);
        testData8.setTotalSleepTime(495);
        testData8.setTotalWakeTime(10);
        testData8.setTotalLightTime(287);
        testData8.setTotalDeepTime(198);
        testData8.setHourlyWake("[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 4]");
        testData8.setHourlyLight("[37, 43, 20, 37, 43, 20, 37, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 50]");
        testData8.setHourlyDeep("[23, 17, 40, 23, 17, 40, 23, 9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6]") ;

        testData9 = new Sleep(1,1,1448380800000l);;
        testData9.setDate(1448380800000l);
        testData9.setTotalSleepTime(113);
        testData9.setTotalWakeTime(10);
        testData9.setTotalLightTime(63);
        testData9.setTotalDeepTime(40);
        testData9.setHourlyWake("[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 10, 0]");
        testData9.setHourlyLight("[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 43, 20]");
        testData9.setHourlyDeep("[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 40]") ;

        testData10 = new Sleep(1,1,1448294400000l);;
        testData10.setDate(1448294400000l);
        testData10.setTotalSleepTime(429);
        testData10.setTotalWakeTime(27);
        testData10.setTotalLightTime(242);
        testData10.setTotalDeepTime(160);
        testData10.setHourlyWake("[12, 15, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]");
        testData10.setHourlyLight("[0, 45, 20, 35, 45, 20, 35, 42, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]");
        testData10.setHourlyDeep("[0, 0, 40, 25, 15, 40, 25, 15, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]") ;

        testData11 = new Sleep(1,1,1448208000000l);;
        testData11.setDate(1448208000000l);
        testData11.setTotalSleepTime(309);
        testData11.setTotalWakeTime(48);
        testData11.setTotalLightTime(261);
        testData11.setTotalDeepTime(0);
        testData11.setHourlyWake("[0, 0, 13, 5, 10, 10, 0, 10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]");
        testData11.setHourlyLight("[0, 0, 0, 55, 50, 50, 60, 46, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]");
        testData11.setHourlyDeep("[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]") ;

        testData12 = new Sleep(1,1,1448121600000l);;
        testData12.setDate(1448121600000l);
        testData12.setTotalSleepTime(468);
        testData12.setTotalWakeTime(52);
        testData12.setTotalLightTime(416);
        testData12.setTotalDeepTime(0);
        testData12.setHourlyWake("[0, 0, 10, 0, 0, 10, 10, 0, 11, 9, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]");
        testData12.setHourlyLight("[0, 0, 29, 60, 60, 50, 50, 60, 49, 51, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]");
        testData12.setHourlyDeep("[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]") ;
    }

    @Test
    public void testTestData(){
        List<Sleep> sleepList = new ArrayList<>();
        sleepList.add(testData10);
        sleepList.add(testData11);
        sleepList.add(testData12);
        sleepList.add(testData4);
        sleepList.add(testData5);
        sleepList.add(testData6);
        sleepList.add(testData7);
        sleepList.add(testData8);
        sleepList.add(testData9);
        SleepDataHandler handler = new SleepDataHandler(sleepList);
        List<SleepData> sleepDataList = handler.getSleepData();
        assertEquals(7, sleepDataList.size());

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

    public boolean testEqualHours(List<Sleep> sleepList, List<SleepData> sleepDataList){
        for(Sleep sleep: sleepList){
            for(SleepData sleepData: sleepDataList){
                if (sleep.getDate() == sleepData.getDate()){
                    if (sleep.getTotalSleepTime() != sleepData.getTotalSleep()){
                        return false;
                    }
                }

            }
        }

        return true;
    }
}
