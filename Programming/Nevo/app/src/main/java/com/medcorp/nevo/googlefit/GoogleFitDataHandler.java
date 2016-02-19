package com.medcorp.nevo.googlefit;

import android.content.Context;

import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.medcorp.nevo.ble.util.Optional;
import com.medcorp.nevo.model.Steps;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Karl on 2/18/16.
 */
public class GoogleFitDataHandler {


    private Context context;
    private List<Optional<Steps>> stepsList;

    public GoogleFitDataHandler(Context context, List<Optional<Steps>> stepsList) {
        this.context = context;
        this.stepsList = stepsList;
    }

    private DataSet getData(){
        Calendar calendar = Calendar.getInstance();
        Date now = new Date();
        calendar.setTime(now);
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.HOUR_OF_DAY, -1);
        long startTime = calendar.getTimeInMillis();

        DataSource dataSource = new DataSource.Builder()
                .setAppPackageName(context)
                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .setName("nevo - step count")
                .setType(DataSource.TYPE_RAW)
                .build();

        int stepCountDelta = 101;
        DataSet dataSet = DataSet.create(dataSource);
        DataPoint dataPoint = dataSet.createDataPoint();
        dataPoint.getValue(Field.FIELD_STEPS).setInt(stepCountDelta);
        dataPoint.setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS);
        dataSet.add(dataPoint);
        return dataSet;
    }

}
