package com.medcorp.nevo.util;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.medcorp.nevo.model.Steps;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by karl-john on 19/2/16.
 */
public class GoogleFitStepsDataHandler {

    private List<Steps> stepsList;
    private Context context;

    public GoogleFitStepsDataHandler(List<Steps> stepsList, Context context) {
        this.stepsList = stepsList;
        this.context = context;
    }

    public DataSet getSteps(){
        DataSource dataSource = new DataSource.Builder()
                .setAppPackageName(context)
                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .setName("nevo - step count")
                .setType(DataSource.TYPE_RAW)
                .build();
        DataSet dataSet = DataSet.create(dataSource);

        for (Steps steps : stepsList) {
            if (steps.getHourlySteps() == null){
                continue;
            }
            JSONArray hourlySteps;
            try {
                hourlySteps = new JSONArray(steps.getHourlySteps());
            } catch (JSONException e) {
                e.printStackTrace();
                hourlySteps = new JSONArray();
            }
            for (int i = 0; i< hourlySteps.length(); i++) {
                try {
                    int stepCount = hourlySteps.getInt(i);
                    if (stepCount > 0){
                        Log.w("Karl", "Added datapoint");
                        Calendar calendar = Calendar.getInstance();
                        Date date = new Date(steps.getDate());
                        calendar.setTime(date);
                        calendar.set(Calendar.HOUR_OF_DAY, i);
                        Log.w("Karl", "Start date = " + calendar.get(Calendar.DAY_OF_MONTH)+"/"+calendar.get(Calendar.MONTH) + " " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));
                        long startTime = calendar.getTimeInMillis();
                        calendar.add(Calendar.HOUR_OF_DAY,1);
                        Log.w("Karl", "End date = " + calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.MONTH) + " " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));
                        long endTime = calendar.getTimeInMillis();
                        DataPoint dataPoint = dataSet.createDataPoint();
                        dataPoint.getValue(Field.FIELD_STEPS).setInt(stepCount);
                        Log.w("Karl","Steps = " + stepCount);
                        dataPoint.setTimeInterval(startTime, endTime, TimeUnit.MICROSECONDS);
                        dataSet.add(dataPoint);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return dataSet;
    }

    public DataSet getStepsTillToday (Date from){
        return null;
    }
}
