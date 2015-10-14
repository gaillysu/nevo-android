package com.medcorp.nevo.fragment;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.medcorp.nevo.R;
import com.medcorp.nevo.ble.controller.SyncController;
import com.medcorp.nevo.ble.listener.OnSyncControllerListener;
import com.medcorp.nevo.ble.model.packet.DailyTrackerNevoPacket;
import com.medcorp.nevo.ble.model.packet.NevoPacket;
import com.medcorp.nevo.ble.model.request.ReadDailyTrackerNevoRequest;
import com.medcorp.nevo.ble.util.Constants;
import com.medcorp.nevo.database.DatabaseHelper;
import com.medcorp.nevo.database.IDailyHistory;
import com.medcorp.nevo.model.SleepBehavior;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by Karl on 10/2/15.
 */
public class NewHistoryFragment extends Fragment implements OnSyncControllerListener {

    protected String[] mMonths = new String[] {
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec"
    };

    private BarChart barChart;
    public static final String NEW_HISTORYFRAGMENT= "NewHistoryFragment";
    private int TotalHistory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.new_history_fragment, container, false);
        barChart = (BarChart) rootView.findViewById(R.id.new_history_fragment_chart);
        barChart.setDescription("Sleep History & Quality");
        barChart.setNoDataText("LOLOL. Get Rekt.");
        barChart.setDescriptionTypeface(Typeface.createFromAsset(getActivity().getAssets(),
                "font/Raleway-Light.ttf"));
        setData(12, 10);

        return rootView;
    }

    private void setData(int count, float range) {


        List<String> xVals = new ArrayList<String>();
        for (int i = 0; i < count + 1; i++) {
            xVals.add(mMonths[i % mMonths.length]);
        }

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        for (int i = 0; i < range+ 1; i++) {
            float mult = (range + 1);
            float val1 = (float) (Math.random() * mult) + mult / 3;
            float val2 = (float) (Math.random() * mult) + mult / 3;
            float val3 = (float) (Math.random() * mult) + mult / 3;

            yVals1.add(new BarEntry(new float[] { val1, val2, val3 }, i));
        }

        BarDataSet set1 = new BarDataSet(yVals1, "Statistics Vienna 2014");
        set1.setColors(getColors());
        set1.setStackLabels(new String[]{"Births", "Divorces", "Marriages"});

        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);
        data.setValueFormatter(new MyValueFormatter());

        barChart.setData(data);
        barChart.invalidate();
        List<IDailyHistory> dailyHistoryList = getDailyHistory();
        Log.w("Karl","Size of daily history = " + dailyHistoryList.size());
        for (IDailyHistory history: dailyHistoryList) {
            try {
                JSONObject json = new JSONObject(history.getRemarks());
                Log.w("Karl",json.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private int[] getColors() {

        int stacksize = 3;

        // have as many colors as stack-values per entry
        int[] colors = new int[stacksize];

        for (int i = 0; i < stacksize; i++) {
            colors[i] = ColorTemplate.VORDIPLOM_COLORS[i];
        }
        return colors;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (SyncController.Singleton.getInstance(getActivity()).isConnected()) {
            //if sync not done,force sync all days(max 7 days) once
            if(getDailyHistory().isEmpty())
            {
                SyncController.Singleton.getInstance(getActivity()).getDailyTrackerInfo(true);
            }
            else
            {
                SyncController.Singleton.getInstance(getActivity()).getDailyTrackerInfo(false);
            }
            SyncController.Singleton.getInstance(getActivity()).setSyncControllerListenser(this);
        }
    }

    @Override
    public void packetReceived(NevoPacket packet) {
        if((byte) ReadDailyTrackerNevoRequest.HEADER == packet.getHeader()) {

            DailyTrackerNevoPacket thispacket = packet.newDailyTrackerNevoPacket();
            Log.w("Karl", "Yo?2 " + thispacket.getDate());
            Log.w("Karl", "=======================");
            Log.w("Karl", "Hourly deep");
            Log.w("Karl", "Total " + thispacket.getHourlDeepTime().size());
            Log.w("Karl", "Light deep");
            Log.w("Karl", "Total " + thispacket.getHourlyLightTime().size());
            Log.w("Karl", "sleep deep");
            Log.w("Karl", "Total " + thispacket.getHourlySleepTime().size());

            if ((thispacket.getTotalLightTime() > 0) && (thispacket.getTotalDeepTime() > 0)){
                SleepBehavior sleepbehavior = new SleepBehavior(thispacket.getHourlDeepTime(), thispacket.getHourlyLightTime());
                sleepbehavior.printBehavior();
            }
        }
    }

    @Override
    public void connectionStateChanged(boolean isConnected) {

    }

    @Override
    public void firmwareVersionReceived(Constants.DfuFirmwareTypes whichfirmware, String version) {

    }

    private class MyValueFormatter implements ValueFormatter {

        private DecimalFormat mFormat;

        public MyValueFormatter() {
            mFormat = new DecimalFormat("###,###,###,##0.0");
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return mFormat.format(value) + " $";
        }
    }

    List<IDailyHistory> getDailyHistory()
    {
        Date from = null;
        String dtStart = "08/01/2014";
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        try {
            from = format.parse(dtStart);
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<Long> days = new ArrayList<Long>();
        //set theDay from 00:00:00
        Calendar calBeginning = new GregorianCalendar();
        calBeginning.setTime(new Date());
        calBeginning.set(Calendar.HOUR_OF_DAY, 0);
        calBeginning.set(Calendar.MINUTE, 0);
        calBeginning.set(Calendar.SECOND, 0);
        calBeginning.set(Calendar.MILLISECOND, 0);
        Date theday = calBeginning.getTime();
        days.add(theday.getTime());
        try {
            return DatabaseHelper.getInstance(getActivity()).getDailyHistoryDao().queryBuilder().orderBy("created", false).where().in("created",days).query();
        } catch (SQLException e) {
            Log.w("Karl","Exception. No shit.");
            e.printStackTrace();
        }
        return new ArrayList<IDailyHistory>();
    }
}