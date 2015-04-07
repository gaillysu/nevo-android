package com.nevowatch.nevo.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.nevowatch.nevo.Function.SaveData;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 15-4-4.
 */
public class MyService extends Service{

    private MyBinder mBinder = new MyBinder();
    private int mCurHour, mCurMin, mTempMin = -1;
    private static final String MYSERVICE = "com.nevowatch.nevo.MyService";

    public class MyBinder extends Binder implements GetDataService {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        getSysTime();
        connectBLE();
    }

    private void getSysTime(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    final Calendar mCalendar = Calendar.getInstance();
                    mCurHour = mCalendar.get(Calendar.HOUR);
                    mCurMin = mCalendar.get(Calendar.MINUTE);
                    if(mCurMin != mTempMin){
                        Intent intent = new Intent();
                        intent.setAction(MYSERVICE);
                        intent.putExtra("MinDegree", (float) (mCurMin * 6));
                        intent.putExtra("HourDegree", (float) ((mCurHour + mCurMin/60.0) * 30));
                        sendBroadcast(intent);
                        mTempMin = mCurMin;
                    }
                }
            }
        }).start();
    }

    public void connectBLE(){
        final TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                SaveData.saveBleConnectToPreference(getApplicationContext(), true);
                Log.d("MyService", "BLE Connected");
            }
        };
        Timer timer = new Timer(true);
        timer.schedule(timerTask, 10000);
    }
}
