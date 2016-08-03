package com.medcorp.util;

/**
 * Created by Karl on 11/27/15.
 */

import com.medcorp.model.Sleep;
import com.medcorp.model.SleepData;
import com.medcorp.util.SleepDataHandler;

import junit.framework.TestCase;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Karl on 11/27/15.
 */
public class SleepDataHandlerTest extends TestCase {

    private Sleep sleepBeforeAndAfter;
    private Sleep sleepBeforeWakeAfter;
    private Sleep sleepBeforeWakeAfter2;
    private Sleep sleepAfterWakeAfter;
    private Sleep strangeData;
    private Sleep strangeData2;
    private Sleep strangeData3;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        sleepBeforeAndAfter = new Sleep(1448812800000l);;
        sleepBeforeAndAfter.setDate(1448812800000l);
        sleepBeforeAndAfter.setHourlyWake("[0, 0, 10, 0, 10, 10, 10, 11, 9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 10, 0, 0, 0, 0]");
        sleepBeforeAndAfter.setHourlyLight("[0, 60, 50, 60, 50, 50, 50, 49, 18, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 50, 20, 30, 50, 5]");
        sleepBeforeAndAfter.setHourlyDeep("[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 40, 30, 10, 10]") ;

        sleepBeforeWakeAfter = new Sleep(1448726400000l);
        sleepBeforeWakeAfter.setDate(1448726400000l);
        sleepBeforeWakeAfter.setHourlyWake("[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 10]");
        sleepBeforeWakeAfter.setHourlyLight("[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 19]");
        sleepBeforeWakeAfter.setHourlyDeep("[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]") ;

        sleepBeforeWakeAfter2 = new Sleep(1448640000000l);;
        sleepBeforeWakeAfter2.setDate(1448640000000l);
        sleepBeforeWakeAfter2.setHourlyWake("[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]");
        sleepBeforeWakeAfter2.setHourlyLight("[23, 50, 27, 23, 50, 27, 23, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]");
        sleepBeforeWakeAfter2.setHourlyDeep("[37, 10, 33, 37, 10, 33, 37, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]") ;

        sleepAfterWakeAfter = new Sleep(1448553600000l);;
        sleepAfterWakeAfter.setDate(1448553600000l);
        sleepAfterWakeAfter.setHourlyWake("[0, 10, 0, 0, 0, 20, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]");
        sleepAfterWakeAfter.setHourlyLight("[0, 40, 0, 0, 0, 40, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]");
        sleepAfterWakeAfter.setHourlyDeep("[0, 10, 60, 60, 60, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]") ;

        strangeData = new Sleep(1448467200000l);;
        strangeData.setDate(1448467200000l);
        strangeData.setHourlyWake("[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 4]");
        strangeData.setHourlyLight("[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 50]");
        strangeData.setHourlyDeep("[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6]") ;

        strangeData2 = new Sleep(1448380800000l);;
        strangeData2.setDate(1448380800000l);
        strangeData2.setHourlyWake("[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 10, 0]");
        strangeData2.setHourlyLight("[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 43, 20]");
        strangeData2.setHourlyDeep("[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 40]") ;

        strangeData3 = new Sleep(1448294400000l);;
        strangeData3.setDate(1448294400000l);
        strangeData3.setHourlyWake("[0, 15, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]");
        strangeData3.setHourlyLight("[0, 45, 20, 35, 45, 20, 35, 42, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]");
        strangeData3.setHourlyDeep("[0, 0, 40, 25, 15, 40, 25, 15, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]");

    }


    /*
        This test case tests Sleep after 12, wake after 12.
        This test case also tests Sleep before 12, wake before 12.
     */

    @Test
    public void testBoth(){
        List<Sleep> sleepList = new ArrayList<>();
        sleepList.add(sleepBeforeAndAfter);
        testList(sleepList,2);
    }

    @Test
    public void testSleepBeforeAfter(){
        List<Sleep> sleepList = new ArrayList<>();
        sleepList.add(sleepBeforeWakeAfter);
        sleepList.add(sleepBeforeWakeAfter2);
        testList(sleepList,1);
    }

    @Test
    public void testSleepAfterAfter(){
        List<Sleep> sleepList = new ArrayList<>();
        sleepList.add(sleepAfterWakeAfter);
        testList(sleepList,1);
    }

    @Test
    public void testStrangeDataa(){
        List<Sleep> sleepList = new ArrayList<>();
        sleepList.add(strangeData);
        sleepList.add(strangeData2);
        sleepList.add(strangeData3);
        testList(sleepList,3);
    }

    @Test
    public void testAll(){
        List<Sleep> sleepList = new ArrayList<>();
        sleepList.add(sleepBeforeAndAfter);
        sleepList.add(sleepBeforeWakeAfter);
        sleepList.add(sleepBeforeWakeAfter2);
        sleepList.add(sleepAfterWakeAfter);
        sleepList.add(strangeData);
        sleepList.add(strangeData2);
        sleepList.add(strangeData3);
        testList(sleepList,7);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }


    public void testList(List<Sleep> list,int sleepDataListSize){
        SleepDataHandler handler = new SleepDataHandler(list,true);
        List<SleepData> sleepDataList = handler.getSleepData();
        assertEquals(sleepDataListSize, sleepDataList.size());
    }
}
