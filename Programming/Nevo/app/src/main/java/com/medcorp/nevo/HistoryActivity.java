package com.medcorp.nevo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarEntry;
import com.medcorp.nevo.History.database.DatabaseHelper;
import com.medcorp.nevo.History.database.IDailyHistory;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by gaillysu on 15/10/7.
 */
public class HistoryActivity extends Activity {

    private BarChart  mBarChart;
    private JSONObject sleepAnalysisResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_history_activity);

        ((ImageView)findViewById(R.id.backImage)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HistoryActivity.this.finish();
            }
        });

        mBarChart = (BarChart)findViewById(R.id.history_bar);
        //Init Chart by read local database
        List<IDailyHistory> history = new ArrayList<IDailyHistory>();
        try {
            history  = DatabaseHelper.getInstance(this).getDailyHistoryDao().queryBuilder().orderBy("created", false).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(history.isEmpty()) return;
        //fill the ChartBar's dataset with "history" List
        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        int i = 0;
        float val1 =0 ;
        float val2 =0;
        float val3 =0;

        for (IDailyHistory daily: history) {
            Date theDay = new Date(daily.getCreated()); // getCreated() return millsecond from 1970.1.1 00:00:00
         //   xVals.add(theDay);
            sleepAnalysisResult = DatabaseHelper.getInstance(this).getSleepZone(theDay);

            long startsleep =0;
            long endsleep=0;

            try {
                startsleep = sleepAnalysisResult.getLong("startDateTime");
                endsleep = sleepAnalysisResult.getLong("endDateTime");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //no sleep data for this day
            if(startsleep == 0 || endsleep ==0 || startsleep==endsleep)
            {
                yVals1.add(new BarEntry(new float[] { 0, 0, 0 }, i));
                i++;
            }
            else
            {
                try {
                    int [] values1 = DatabaseHelper.string2IntArray(sleepAnalysisResult.getString("mergeHourlyWakeTime"));
                    int [] values2 = DatabaseHelper.string2IntArray(sleepAnalysisResult.getString("mergeHourlyLightTime"));
                    int [] values3 = DatabaseHelper.string2IntArray(sleepAnalysisResult.getString("mergeHourlyDeepTime"));

                    val1 = 0;for(int k=0;k<values1.length;k++) val1 +=values1[k];
                    val2 = 0;for(int k=0;k<values2.length;k++) val2 +=values2[k];
                    val3 = 0;for(int k=0;k<values3.length;k++) val3 +=values3[k];

                    yVals1.add(new BarEntry(new float[] { val1, val2, val3 }, i));
                    i++;

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
