package com.medcorp.nevo;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.medcorp.nevo.History.database.DatabaseHelper;
import com.medcorp.nevo.History.database.IDailyHistory;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by gaillysu on 15/10/7.
 */
public class HistoryActivity extends Activity implements OnChartValueSelectedListener {

    private BarChart  mBarChart;

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
        mBarChart.setOnChartValueSelectedListener(this);
        mBarChart.setDescription("");
        mBarChart.setNoDataTextDescription("");
        mBarChart.setPinchZoom(false);
        mBarChart.setDrawGridBackground(false);
        mBarChart.setScaleEnabled(false);
        mBarChart.setDrawValueAboveBar(false);
        mBarChart.setDoubleTapToZoomEnabled(false);
//        mBarChart.setViewPortOffsets(0.0f, 0.0f, 0.0f, 0.0f);

        YAxis yAxis = mBarChart.getAxisLeft();
        yAxis.setDrawAxisLine(false);
        yAxis.setDrawGridLines(false);
        yAxis.setEnabled(false);
        yAxis.setSpaceTop(0.6f);

        XAxis xAxis = mBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);

        //Init Chart by read local database
        List<IDailyHistory> history = new ArrayList<IDailyHistory>();
        try {
            history  = DatabaseHelper.getInstance(this).getDailyHistoryDao().queryBuilder().orderBy("created", false).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        mBarChart.setScaleX(history.size()/7);
        mBarChart.setScaleY(1);

        if(history.isEmpty()){
            return;
        }
        //fill the ChartBar's dataset with "history" List
        List<String> xVals = new ArrayList<String>();
        List<BarEntry> yVals1 = new ArrayList<BarEntry>();

        int i = 0;
        float val1 =0 ;
        float val2 =0;
        float val3 =0;

        for (IDailyHistory daily: history) {
            Date theDay = new Date(daily.getCreated()); // getCreated() return millsecond from 1970.1.1 00:00:00
            SimpleDateFormat sdf = new SimpleDateFormat("d'/'M");

            xVals.add(sdf.format(theDay));
            Log.w("Karl", sdf.format(theDay));
            JSONObject sleepAnalysisResult = DatabaseHelper.getInstance(this).getSleepZone(theDay);
            Log.w("Karl", sleepAnalysisResult.toString());
            long startsleep =0;
            long endsleep=0;

            try {
                startsleep = sleepAnalysisResult.getLong("startDateTime");
                endsleep = sleepAnalysisResult.getLong("endDateTime");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //no sleep data for this day
            if(startsleep == 0 || endsleep ==0 || startsleep==endsleep) {
                continue;
            }
            else {
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
        Log.w("Karl", " yvals length = " + yVals1.size());
        BarDataSet set1 = new BarDataSet(yVals1, "");
        Resources rs = getResources();
        set1.setColors(new int[]{rs.getColor(R.color.deep_sleep), rs.getColor(R.color.light_sleep), rs.getColor(R.color.wake_sleep)});
        set1.setStackLabels(new String[]{"Deep", "Light", "Wake"});
//        set1.setBarSpacePercent(35f);

        List<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);
//        data.setValueTextSize(10f);
        mBarChart.setData(data);
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
}
