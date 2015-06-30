package com.nevowatch.nevo.NevoGFT;

import com.google.android.gms.fitness.FitnessActivities;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.fitness.request.SessionInsertRequest;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by evan on 2015/6/30 0030.
 */
public class GFSteps implements GFDataPoint{
    private Date mStartDate;
    private Date mEndDate;
    private int mSteps;

    public GFSteps(Date startDate, Date endDate, int steps){
        mStartDate = startDate;
        mEndDate = endDate;
        mSteps = steps;
    }

    @Override
    public DataSet toDataSet() {
        long startTime = mStartDate.getTime();
        long endTime = mEndDate.getTime();

        // Create a data source
        DataSource dataSource = new DataSource.Builder()
//                .setAppPackageName(mContext.getPackageName())
                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
//                .setName(SAMPLE_SESSION_NAME + "- steps")
                .setType(DataSource.TYPE_RAW)
                .build();

        // Create a data set of the run speeds to include in the session.
        DataSet dataSet = DataSet.create(dataSource);

        DataPoint firstRunSpeed = dataSet.createDataPoint();
        firstRunSpeed.setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS);
        firstRunSpeed.getValue(Field.FIELD_STEPS).setInt(mSteps);
        dataSet.add(firstRunSpeed);

        return dataSet;
    }

    @Override
    public SessionInsertRequest toSessionInsertRequest() {
        long startTime = mStartDate.getTime();
        long endTime = mEndDate.getTime();

        DataSet dataSet = toDataSet();
        // [START build_insert_session_request]
        // Create a session with metadata about the activity.
        Session session = new Session.Builder()
//                .setName(SAMPLE_SESSION_NAME)
//                .setDescription("Long run around Shoreline Park")
//                .setIdentifier("UniqueIdentifierHere")
                .setActivity(FitnessActivities.WALKING)
                .setStartTime(startTime, TimeUnit.MILLISECONDS)
                .setEndTime(endTime, TimeUnit.MILLISECONDS)
                .build();

        // Build a session insert request
        SessionInsertRequest insertRequest = new SessionInsertRequest.Builder()
                .setSession(session)
                .addDataSet(dataSet)
                .build();
        return insertRequest;
    }

    @Override
    public boolean isUpdate() {
        return false;
    }
}
