package com.nevowatch.nevo;

import android.util.Log;

import com.nevowatch.nevo.Model.DailyHistory;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.sql.DataSource;

/**
 * Created by Hugo on 21/4/15.
 */
public class GoogleFitManager {

    private static GoogleFitManager sInstance = null;
    public static GoogleFitManager getInstance() {
        if(null == sInstance )
        {
            sInstance = new GoogleFitManager();
        }
        return sInstance;
    }

    public void saveDailyHistory(DailyHistory dailyHistory){
        Date historyDate = dailyHistory.getDate();

        //TODO by Hugo, I should check that there's no doublons between daily and hourly data
        //saveDailyDataPoint( historyDate , dailyHistory.getTotalSteps() );

        for(int i=0; i<dailyHistory.getHourlySteps().size(); i++) {

            saveHourlyDataPoint(historyDate, i, dailyHistory.getHourlySteps().get(i) );
        }

    }

    private void saveDailyDataPoint(Date date,int steps) {
        Calendar calMorning = new GregorianCalendar();
        calMorning.setTime(new Date());
        calMorning.set(Calendar.HOUR_OF_DAY, 0);
        calMorning.set(Calendar.MINUTE, 0);
        calMorning.set(Calendar.SECOND, 0);
        calMorning.set(Calendar.MILLISECOND, 0);
        Date morning = calMorning.getTime();

        Calendar calNight = new GregorianCalendar();
        calNight.setTime(new Date());
        calNight.set(Calendar.HOUR_OF_DAY, 23);
        calNight.set(Calendar.MINUTE, 59);
        calNight.set(Calendar.SECOND, 59);
        calNight.set(Calendar.MILLISECOND, 0);
        Date night = calNight.getTime();

        saveDataPoint(morning, night, steps);
    }

    private void saveHourlyDataPoint(Date date, int hour,int steps ) {
        Calendar calBeginning = new GregorianCalendar();
        calBeginning.setTime(new Date());
        calBeginning.set(Calendar.HOUR_OF_DAY, hour);
        calBeginning.set(Calendar.MINUTE, 0);
        calBeginning.set(Calendar.SECOND, 0);
        calBeginning.set(Calendar.MILLISECOND, 0);
        Date morning = calBeginning.getTime();

        Calendar calEnd = new GregorianCalendar();
        calEnd.setTime(new Date());
        calEnd.set(Calendar.HOUR_OF_DAY, hour);
        calEnd.set(Calendar.MINUTE, 59);
        calEnd.set(Calendar.SECOND, 59);
        calEnd.set(Calendar.MILLISECOND, 0);
        Date night = calEnd.getTime();

        saveDataPoint(morning, night, steps);
    }

    private void saveDataPoint(Date startDate, Date endDate, int value) {/*
        // Create a data source
        DataSource dataSource = new DataSource.Builder()
                .setAppPackageName(this)
                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .setName(TAG + " - step count")
                .setType(DataSource.TYPE_RAW)
                .build();

        // Create a data set
        int stepCountDelta = 1000;
        DataSet dataSet = DataSet.create(dataSource);
        // For each data point, specify a start time, end time, and the data value -- in this case,
        // the number of new steps.
        DataPoint dataPoint = dataSet.createDataPoint()
                .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS);
        dataPoint.getValue(Field.FIELD_STEPS).setInt(stepCountDelta);
        dataSet.add(dataPoint);

        // Then, invoke the History API to insert the data and await the result, which is
        // possible here because of the {@link AsyncTask}. Always include a timeout when calling
        // await() to prevent hanging that can occur from the service being shutdown because
        // of low memory or other conditions.
        Log.i(GoogleFitManager.class.getName(), "Inserting the dataset in the History API");
        com.google.android.gms.common.api.Status insertStatus =
                Fitness.HistoryApi.insertData(mClient, dataSet)
                        .await(1, TimeUnit.MINUTES);

        // Before querying the data, check to see if the insertion succeeded.
        if (!insertStatus.isSuccess()) {
            Log.i(GoogleFitManager.class.getName(), "There was a problem inserting the dataset.");
        } else {
            // At this point, the data has been inserted and can be read.
            Log.i(GoogleFitManager.class.getName(), "Data insert was successful!");
        }
        */


    }


}
