package com.nevowatch.nevo.NevoGFT;

import com.google.android.gms.fitness.FitnessActivities;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.fitness.request.DataDeleteRequest;
import com.google.android.gms.fitness.request.SessionInsertRequest;
import com.google.android.gms.fitness.request.SessionReadRequest;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by evan on 2015/6/30 0030.
 */
public class GFSteps implements GFDataPoint{
    private Date mStartDate;
    private Date mEndDate;
    private int mSteps;

    private int mGoogleFitValue;

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
        //if present in Google and value got changed, update it!!!
        if(mGoogleFitValue !=mSteps && mSteps>0 && mGoogleFitValue>0) return true;
        /**
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        Calendar calBeginning = Calendar.getInstance();
        calBeginning.setTime(mStartDate);
        if(c.get(Calendar.YEAR) == calBeginning.get(Calendar.YEAR)
                && c.get(Calendar.MONTH) == calBeginning.get(Calendar.MONTH)
                && c.get(Calendar.DAY_OF_MONTH) == calBeginning.get(Calendar.DAY_OF_MONTH)
                && c.get(Calendar.HOUR_OF_DAY) == calBeginning.get(Calendar.HOUR_OF_DAY))
        {
            return true;
        }*/
        return false;
    }

    @Override
    public SessionReadRequest toSessionReadRequest() {
        long startTime = mStartDate.getTime();
        long endTime = mEndDate.getTime();
        SessionReadRequest readRequest = new SessionReadRequest.Builder()
                .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
                .read(DataType.TYPE_STEP_COUNT_DELTA)
//                .setSessionName(SAMPLE_SESSION_NAME)
                .build();
        return readRequest;
    }

    @Override
    public DataDeleteRequest toSessionDeleteRequest() {
        long startTime = mStartDate.getTime();
        long endTime = mEndDate.getTime();
        DataDeleteRequest request = new DataDeleteRequest.Builder()
                .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .deleteAllSessions() // Or specify a particular session here
                .build();
        return request;
    }

    @Override
    public void saveValue(int value) {
        mGoogleFitValue = value;
    }
}
