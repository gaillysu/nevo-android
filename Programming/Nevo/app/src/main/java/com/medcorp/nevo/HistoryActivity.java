package com.medcorp.nevo;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
    private List<BarEntry> mSleepData;
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
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
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
                    val1 = 0;
                    val2 = getDailyTotalSleepTime(SleepType.DEEPSLEEP, sleepAnalysisResult);
                    val3 = getDailyTotalSleepTime(SleepType.WAKESLEEP, sleepAnalysisResult) + getDailyTotalSleepTime(SleepType.LIGHTSLEEP, sleepAnalysisResult);

                    yVals1.add(new BarEntry(new float[] { val2, val3 }, i));
                    i++;
            }
        }
        Log.w("Karl", " yvals length = " + yVals1.size());
        BarDataSet set1 = new BarDataSet(yVals1, "");
        mSleepData = yVals1;
        Resources rs = getResources();
        set1.setColors(new int[]{rs.getColor(R.color.deep_sleep), rs.getColor(R.color.light_sleep)});
        set1.setStackLabels(new String[]{rs.getString(R.string.deep_sleep), rs.getString(R.string.light_sleep)});
//        set1.setBarSpacePercent(35f);

        List<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);
//        data.setValueTextSize(10f);
        mBarChart.setData(data);
    }

    public int getDailyTotalSleepTime(SleepType type, JSONObject sleepAnalysisResult){
        String key = "";
        int time = 0;
        switch (type){
            case LIGHTSLEEP:
                key = "mergeHourlyLightTime";
                break;
            case WAKESLEEP:
                key = "mergeHourlyWakeTime";
                break;
            case DEEPSLEEP:
                key = "mergeHourlyDeepTime";
                break;
        }
        try{
            int [] values =  DatabaseHelper.string2IntArray(sleepAnalysisResult.getString(key));
            for(int k=0;k<values.length;k++) time +=values[k];
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return time;
    }

    public enum SleepType {
        LIGHTSLEEP,
        DEEPSLEEP,
        WAKESLEEP
    }
    private void refreshSleepDetail(BarEntry be){
        TextView lightValue = (TextView)findViewById(R.id.light_value);
        TextView totalValue = (TextView)findViewById(R.id.total_value);
        TextView deepValue = (TextView)findViewById(R.id.deep_value);
        float[] vals = be.getVals();be.getVals();
        if(vals.length>=2){
            String minutes = getResources().getString(R.string.minutes);
            deepValue.setText(String.format("%.0f %s", vals[0], minutes));
            lightValue.setText(String.format("%.0f %s", vals[1], minutes));
            totalValue.setText(String.format("%.0f %s", be.getVal(), minutes));
        }
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        BarEntry be = mSleepData.get(e.getXIndex());
        refreshSleepDetail(be);
    }

    @Override
    public void onNothingSelected() {

    }
}
