package com.nevowatch.nevo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessActivities;
import com.google.android.gms.fitness.data.*;
import com.google.android.gms.fitness.request.DataDeleteRequest;
import com.google.android.gms.fitness.request.SessionInsertRequest;
import com.google.android.gms.fitness.request.SessionReadRequest;
import com.google.android.gms.fitness.result.SessionReadResult;
import com.nevowatch.nevo.Model.DailyHistory;
import com.nevowatch.nevo.NevoGFT.GFDataPoint;
import com.nevowatch.nevo.NevoGFT.GFSteps;
import com.nevowatch.nevo.NevoGFT.GoogleFit;
import com.nevowatch.nevo.ble.util.QueuedMainThreadHandler;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * Created by Hugo on 21/4/15.
 */

/**
 * example
 * make sure you already instance in mainActivity before
 *
 Log.i("BasicSessions", "click insertData");
 GoogleFitManager gftManager = GoogleFitManager.getInstance(null,null);
 if(gftManager!=null) {
 gftManager.saveHourlyDataPoint(new Date(), 8, 1000);
 }
 */
public class GoogleFitManager implements GoogleFit{

    public static final String TAG = "GoogleFitManager";
    public static final String SAMPLE_SESSION_NAME = "Afternoon run";
    private static final int REQUEST_OAUTH = 1;
    private static final String DATE_FORMAT = "yyyy.MM.dd HH:mm:ss";

    /**
     * Track whether an authorization activity is stacking over the current activity, i.e. when
     *  a known auth error is being resolved, such as showing the account chooser or presenting a
     *  consent dialog. This avoids common duplications as might happen on screen rotations, etc.
     */
    private static final String AUTH_PENDING = "auth_state_pending";
    private boolean authInProgress = false;

    private GoogleApiClient mClient = null;
    private Context mContext = null;
    private Activity mActivity = null;

    private static GoogleFitManager sInstance = null;
    public static GoogleFitManager getInstance(Context context, Activity activity) {
        if(null == sInstance && context!=null && activity!=null)
        {
            sInstance = new GoogleFitManager();
            sInstance.setmContext(context);
            sInstance.setmActivity(activity);
            sInstance.buildFitnessClient();
        }
        return sInstance;
    }

    public GoogleApiClient getmClient() {
        return mClient;
    }

    public void setmContext(Context context){
        mContext = context;
    }

    public void setmActivity(Activity activity){
        mActivity = activity;
    }

    public void dealActivityResult(int requestCode, int resultCode, Intent data){
        Log.i(TAG, "dealActivityResult requestCode:" + requestCode + " resultCode: " + resultCode);
        if (requestCode == REQUEST_OAUTH) {
            authInProgress = false;
            if (resultCode == Activity.RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mClient.isConnecting() && !mClient.isConnected()) {
                    mClient.connect();
                }
            }
        }
    }

    public void dealSaveInstanceState(Bundle outState){
        Log.i(TAG, "dealSaveInstanceState" + authInProgress);
        outState.putBoolean(AUTH_PENDING, authInProgress);
    }

    public void saveDailyHistory(final DailyHistory dailyHistory){
        final Date historyDate = dailyHistory.getDate();

        //TODO by Hugo, I should check that there's no doublons between daily and hourly data
        //saveDailyDataPoint( historyDate , dailyHistory.getTotalSteps() );

        for(int i=0; i<dailyHistory.getHourlySteps().size(); i++) {
            if(dailyHistory.getHourlySteps().get(i)>0) {
                    final int index = i;
                    QueuedMainThreadHandler.getInstance(QueuedMainThreadHandler.QueueType.GoogleFit).post(new Runnable() {
                        @Override
                        public void run() {
                            Log.i(TAG, historyDate.toString() + ",hour:"+index + ",steps:"+ dailyHistory.getHourlySteps().get(index));
                            saveHourlyDataPoint(historyDate, index, dailyHistory.getHourlySteps().get(index));
                        }
                    });
            }
        }

    }

    private void saveDailyDataPoint(Date date,int steps) {
        Calendar calMorning = new GregorianCalendar();
        calMorning.setTime(date);
        calMorning.set(Calendar.HOUR_OF_DAY, 0);
        calMorning.set(Calendar.MINUTE, 0);
        calMorning.set(Calendar.SECOND, 0);
        calMorning.set(Calendar.MILLISECOND, 0);
        Date morning = calMorning.getTime();

        Calendar calNight = new GregorianCalendar();
        calNight.setTime(date);
        calNight.set(Calendar.HOUR_OF_DAY, 23);
        calNight.set(Calendar.MINUTE, 59);
        calNight.set(Calendar.SECOND, 59);
        calNight.set(Calendar.MILLISECOND, 0);
        Date night = calNight.getTime();

        GFSteps gfSteps = new GFSteps(morning, night, steps);
        writeDataPoint(gfSteps);
    }

    public void saveHourlyDataPoint(Date date, int hour,int steps ) {
        Calendar calBeginning = new GregorianCalendar();
        calBeginning.setTime(date);
        calBeginning.set(Calendar.HOUR_OF_DAY, hour);
        calBeginning.set(Calendar.MINUTE, 0);
        calBeginning.set(Calendar.SECOND, 0);
        calBeginning.set(Calendar.MILLISECOND, 0);
        Date morning = calBeginning.getTime();

        Calendar calEnd = new GregorianCalendar();
        calEnd.setTime(date);
        calEnd.set(Calendar.HOUR_OF_DAY, hour);
        calEnd.set(Calendar.MINUTE, 59);
        calEnd.set(Calendar.SECOND, 59);
        calEnd.set(Calendar.MILLISECOND, 0);
        Date night = calEnd.getTime();

        GFSteps gfSteps = new GFSteps(morning, night, steps);
        writeDataPoint(gfSteps);
    }


    @Override
    public void requestPermission() {

    }

    @Override
    public void writeDataPoint(GFDataPoint dataPoint) {
        if(mClient.isConnected()){
            new InsertAndVerifySessionTask().execute(dataPoint);
        }
    }

    @Override
    public boolean isPresent(GFDataPoint dataPoint) {
        return false;
    }

    /**
     *  Build a {@link GoogleApiClient} that will authenticate the user and allow the application
     *  to connect to Fitness APIs. The scopes included should match the scopes your app needs
     *  (see documentation for details). Authentication will occasionally fail intentionally,
     *  and in those cases, there will be a known resolution, which the OnConnectionFailedListener()
     *  can address. Examples of this include the user never having signed in before, or having
     *  multiple accounts on the device and needing to specify which account to use, etc.
     */
    private void buildFitnessClient() {
        // Create the Google API Client
        mClient = new GoogleApiClient.Builder(mContext)
                .addApi(Fitness.HISTORY_API)
                .addApi(Fitness.SESSIONS_API)
                .addApi(Fitness.RECORDING_API)
                .addScope(new Scope(Scopes.FITNESS_LOCATION_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addConnectionCallbacks(
                        new GoogleApiClient.ConnectionCallbacks() {
                            @Override
                            public void onConnected(Bundle bundle) {
                                Log.i(TAG, "Connected!!!");
                                // Now you can make calls to the Fitness APIs.  What to do?
                                // Play with some sessions!!

                            }

                            @Override
                            public void onConnectionSuspended(int i) {
                                // If your connection to the sensor gets lost at some point,
                                // you'll be able to determine the reason and react to it here.
                                if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                    Log.i(TAG, "Connection lost.  Cause: Network Lost.");
                                } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                    Log.i(TAG, "Connection lost.  Reason: Service Disconnected");
                                }
                            }
                        }
                )
                .addOnConnectionFailedListener(
                        new GoogleApiClient.OnConnectionFailedListener() {
                            // Called whenever the API client fails to connect.
                            @Override
                            public void onConnectionFailed(ConnectionResult result) {
                                Log.i(TAG, "Connection failed. Cause: " + result.toString());
                                if (!result.hasResolution()) {
                                    // Show the localized error dialog
                                    GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(),
                                            mActivity, 0).show();
                                    return;
                                }
                                // The failure has a resolution. Resolve it.
                                // Called typically when the app is not yet authorized, and an
                                // authorization dialog is displayed to the user.
                                if (!authInProgress) {
                                    try {
                                        Log.i(TAG, "Attempting to resolve failed connection");
                                        authInProgress = true;
                                        result.startResolutionForResult(mActivity,
                                                REQUEST_OAUTH);
                                    } catch (IntentSender.SendIntentException e) {
                                        Log.e(TAG,
                                                "Exception while starting resolution activity", e);
                                    }
                                }
                            }
                        }
                )
                .build();
    }

    /**
     *  Create and execute a {@link SessionInsertRequest} to insert a session into the History API,
     *  and then create and execute a {@link SessionReadRequest} to verify the insertion succeeded.
     *  By using an AsyncTask to make our calls, we can schedule synchronous calls, so that we can
     *  query for sessions after confirming that our insert was successful. Using asynchronous calls
     *  and callbacks would not guarantee that the insertion had concluded before the read request
     *  was made. An example of an asynchronous call using a callback can be found in the example
     *  on deleting sessions below.
     */
    private class InsertAndVerifySessionTask extends AsyncTask<GFDataPoint, Void, Void> {
        protected Void doInBackground(GFDataPoint... params) {
            //First, create a new session and an insertion request.
            SessionInsertRequest insertRequest = params[0].toSessionInsertRequest();

            // [START insert_session]
            // Then, invoke the Sessions API to insert the session and await the result,
            // which is possible here because of the AsyncTask. Always include a timeout when
            // calling await() to avoid hanging that can occur from the service being shutdown
            // because of low memory or other conditions.
            Log.i(TAG, "Inserting the session in the History API");
            com.google.android.gms.common.api.Status insertStatus =
                    Fitness.SessionsApi.insertSession(mClient, insertRequest)
                            .await(1, TimeUnit.MINUTES);

            // Before querying the session, check to see if the insertion succeeded.
            if (!insertStatus.isSuccess()) {
                Log.i(TAG, "There was a problem inserting the session: " +
                        insertStatus.getStatusMessage());
                return null;
            }

            // At this point, the session has been inserted and can be read.
            Log.i(TAG, "Session insert was successful!");
            // [END insert_session]

            /*
            // Begin by creating the query.
            SessionReadRequest readRequest = readFitnessSession();

            // [START read_session]
            // Invoke the Sessions API to fetch the session with the query and wait for the result
            // of the read request.
            SessionReadResult sessionReadResult =
                    Fitness.SessionsApi.readSession(mClient, readRequest)
                            .await(1, TimeUnit.MINUTES);

            // Get a list of the sessions that match the criteria to check the result.
            Log.i(TAG, "Session read was successful. Number of returned sessions is: "
                    + sessionReadResult.getSessions().size());
            for (Session session : sessionReadResult.getSessions()) {
                // Process the session
                dumpSession(session);

                // Process the data sets for this session
                List<DataSet> dataSets = sessionReadResult.getDataSet(session);
                for (DataSet dataSet : dataSets) {
                    dumpDataSet(dataSet);
                }
            }
            // [END read_session]
            */
            QueuedMainThreadHandler.getInstance(QueuedMainThreadHandler.QueueType.GoogleFit).next();
            return null;
        }
    }

    public long clearMillis2second(long millis){
        millis = millis/1000*1000;
        return millis;
    }

    /**
     *  Create a {@link SessionInsertRequest} for a run that consists of 10 minutes running,
     *  10 minutes walking, and 10 minutes of running. The request contains two {@link DataSet}s:
     *  speed data and activity segments data.
     *
     *  {@link Session}s are time intervals that are associated with all Fit data that falls into
     *  that time interval. This data can be inserted when inserting a session or independently,
     *  without affecting the association between that data and the session. Future queries for
     *  that session will return all data relevant to the time interval created by the session.
     *
     *  Sessions may contain {@link DataSet}s, which are comprised of {@link DataPoint}s and a
     *  {@link DataSource}.
     *  A {@link DataPoint} is associated with a Fit {@link DataType}, which may be
     *  derived from the {@link DataSource}, as well as a time interval, and a value. A given
     *  {@link DataSet} may only contain data for a single data type, but a {@link Session} can
     *  contain multiple {@link DataSet}s.
     */
    private SessionInsertRequest insertFitnessSession(GFSteps steps) {
        Log.i(TAG, "Creating a new session for steps");
        // Setting start and end times for our run.
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
//        cal.set(2015,5,30,7,0,0);
        // Set a range of the run, using a start time of 30 minutes before this moment,
        // with a 10-minute walk in the middle.
//        long endTime = clearMillis2second(cal.getTimeInMillis());
//        cal.add(Calendar.MINUTE, -10);
//        long startTime = clearMillis2second(cal.getTimeInMillis());
//        long startTime = startDate.getTime();
//        long endTime = endDate.getTime();
//
//        // Create a data source
//        DataSource dataSource = new DataSource.Builder()
//                .setAppPackageName(mContext.getPackageName())
//                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
//                .setName(SAMPLE_SESSION_NAME + "- steps")
//                .setType(DataSource.TYPE_RAW)
//                .build();
//
//        // Create a data set of the run speeds to include in the session.
//        DataSet dataSet = DataSet.create(dataSource);
//
//        DataPoint firstRunSpeed = dataSet.createDataPoint();
//        firstRunSpeed.setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS);
//        firstRunSpeed.getValue(Field.FIELD_STEPS).setInt(value);
//        dataSet.add(firstRunSpeed);

        // [START build_insert_session_request]
        // Create a session with metadata about the activity.
//        Session session = new Session.Builder()
//                .setName(SAMPLE_SESSION_NAME)
////                .setDescription("Long run around Shoreline Park")
////                .setIdentifier("UniqueIdentifierHere")
//                .setActivity(FitnessActivities.WALKING)
//                .setStartTime(startTime, TimeUnit.MILLISECONDS)
//                .setEndTime(endTime, TimeUnit.MILLISECONDS)
//                .build();
//
//        // Build a session insert request
//        SessionInsertRequest insertRequest = new SessionInsertRequest.Builder()
//                .setSession(session)
//                .addDataSet(dataSet)
//                .build();

        return null;//insertRequest;
    }

    /**
     * Return a {@link SessionReadRequest} for all speed data in the past week.
     */
    private SessionReadRequest readFitnessSession() {
        Log.i(TAG, "Reading History API results for session: " + SAMPLE_SESSION_NAME);
        // [START build_read_session_request]
        // Set a start and end time for our query, using a start time of 1 week before this moment.
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.WEEK_OF_MONTH, -1);
        long startTime = cal.getTimeInMillis();

        // Build a session read request
        SessionReadRequest readRequest = new SessionReadRequest.Builder()
                .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
                .read(DataType.TYPE_STEP_COUNT_DELTA)
//                .setSessionName(SAMPLE_SESSION_NAME)
                .build();
        // [END build_read_session_request]

        return readRequest;
    }

    private void dumpDataSet(DataSet dataSet) {
        Log.i(TAG, "Data returned for Data type: " + dataSet.getDataType().getName());
        for (DataPoint dp : dataSet.getDataPoints()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
            Log.i(TAG, "Data point:");
            Log.i(TAG, "\tType: " + dp.getDataType().getName());
            Log.i(TAG, "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            Log.i(TAG, "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
            for(Field field : dp.getDataType().getFields()) {
                Log.i(TAG, "\tField: " + field.getName() +
                        " Value: " + dp.getValue(field));
            }
        }
    }

    private void dumpSession(Session session) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        Log.i(TAG, "Data returned for Session: " + session.getName()
                + "\n\tDescription: " + session.getDescription()
                + "\n\tid: " + session.getIdentifier()
                + "\n\tStart: " + dateFormat.format(session.getStartTime(TimeUnit.MILLISECONDS))
                + "\n\tEnd: " + dateFormat.format(session.getEndTime(TimeUnit.MILLISECONDS)));
    }

    /**
     * Delete the {@link DataSet} we inserted with our {@link Session} from the History API.
     * In this example, we delete all step count data for the past 24 hours. Note that this
     * deletion uses the History API, and not the Sessions API, since sessions are truly just time
     * intervals over a set of data, and the data is what we are interested in removing.
     */
    private void deleteSession() {
        Log.i(TAG, "Deleting today's session data for speed");

        // Set a start and end time for our data, using a start time of 1 day before this moment.
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.WEEK_OF_MONTH, -1);
        long startTime = cal.getTimeInMillis();

        // Create a delete request object, providing a data type and a time interval
        DataDeleteRequest request = new DataDeleteRequest.Builder()
                .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .deleteAllSessions() // Or specify a particular session here
                .build();

        // Invoke the History API with the Google API client object and the delete request and
        // specify a callback that will check the result.
        Fitness.HistoryApi.deleteData(mClient, request)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            Log.i(TAG, "Successfully deleted today's sessions");
                        } else {
                            // The deletion will fail if the requesting app tries to delete data
                            // that it did not insert.
                            Log.i(TAG, "Failed to delete today's sessions");
                        }
                    }
                });
    }


}
