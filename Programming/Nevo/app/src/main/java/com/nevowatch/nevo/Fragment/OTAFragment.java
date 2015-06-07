package com.nevowatch.nevo.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nevowatch.nevo.FontManager;
import com.nevowatch.nevo.MainActivity;
import com.nevowatch.nevo.Model.Notification.NotificationType;
import com.nevowatch.nevo.R;
import com.nevowatch.nevo.View.NotificationItem;
import com.nevowatch.nevo.View.RoundProgressBar;
import com.nevowatch.nevo.ble.controller.OnSyncControllerListener;
import com.nevowatch.nevo.ble.controller.SyncController;
import com.nevowatch.nevo.ble.model.packet.NevoPacket;
import com.nevowatch.nevo.ble.model.request.NumberOfStepsGoal;
import com.nevowatch.nevo.ble.notification.NevoNotificationListener;

import java.util.ArrayList;
import java.util.List;

/**
 * NotificationFragment
 */
public class OTAFragment extends Fragment
        implements View.OnClickListener,OnSyncControllerListener{

    public static final String OTAFRAGMENT = "OTAFragment";
    public static final int OTAPOSITION = 4;
    private Handler mUiHandler = new Handler(Looper.getMainLooper());
    private RoundProgressBar mOTAProgressBar;
    private TextView mMCUVersionTextView;
    private TextView mBleVersionTextView;
    private TextView mOTAProgressValueTextView;
    private Button mReButton;
    private ImageView mWarningButton;

    private void initListView(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.ota_fragment, container, false);
        initListView();
        mOTAProgressBar = (RoundProgressBar) rootView.findViewById(R.id.otaProgressBar);

        mMCUVersionTextView = (TextView) rootView.findViewById(R.id.mcuVersionLabel);

        mBleVersionTextView = (TextView) rootView.findViewById(R.id.bleVersionLabel);

        mOTAProgressValueTextView = (TextView) rootView.findViewById(R.id.progressValue);

        mReButton = (Button) rootView.findViewById(R.id.reUpgradebutton);
        mReButton.setOnClickListener(this);

        mWarningButton = (ImageView) rootView.findViewById(R.id.warningButton);
        mReButton.setOnClickListener(this);

        View [] viewArray = new View []{
                rootView.findViewById(R.id.mcuVersionLabel),
                rootView.findViewById(R.id.bleVersionLabel),
                rootView.findViewById(R.id.progressValue),
                rootView.findViewById(R.id.reUpgradebutton)
        };
        FontManager.changeFonts(viewArray, getActivity());

        return rootView;
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.reUpgradebutton:

                break;
            case R.id.warningButton:

                break;
            default:
                break;
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        if(SyncController.Singleton.getInstance(getActivity())!=null && !SyncController.Singleton.getInstance(getActivity()).isConnected()){
            ((MainActivity)getActivity()).replaceFragment(ConnectAnimationFragment.CONNECTPOSITION, ConnectAnimationFragment.CONNECTFRAGMENT);
        }
    }

    @Override
    public void packetReceived(NevoPacket packet) {

    }

    @Override
    public void connectionStateChanged(boolean isConnected) {
        ((MainActivity)getActivity()).replaceFragment(isConnected?NotificationFragment.NOTIPOSITION:ConnectAnimationFragment.CONNECTPOSITION, isConnected?NotificationFragment.NOTIFICATIONFRAGMENT:ConnectAnimationFragment.CONNECTFRAGMENT);
    }

    /**
    *Set the display Ble and MCU version number
    */
    private void setFirmwaresVersionText(final String mcuStr,final String bleStr){
        mMCUVersionTextView.setText(mcuStr);
        mBleVersionTextView.setText(bleStr);
    }

    /**
    *Set the OTA upgrade progress
    */
    public void setOTAProgressBar(final int progress){
        mOTAProgressBar.setProgress(progress);
        mOTAProgressValueTextView.setText(progress+"%");
    }
}
