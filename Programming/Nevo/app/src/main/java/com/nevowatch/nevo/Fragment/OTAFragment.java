package com.nevowatch.nevo.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.nevowatch.nevo.MainActivity;
import com.nevowatch.nevo.Model.Notification.NotificationType;
import com.nevowatch.nevo.R;
import com.nevowatch.nevo.View.NotificationItem;
import com.nevowatch.nevo.ble.controller.OnSyncControllerListener;
import com.nevowatch.nevo.ble.controller.SyncController;
import com.nevowatch.nevo.ble.model.packet.NevoPacket;
import com.nevowatch.nevo.ble.notification.NevoNotificationListener;

import java.util.ArrayList;
import java.util.List;

/**
 * NotificationFragment
 */
public class OTAFragment extends Fragment
        implements OnSyncControllerListener{

    public static final String OTAFRAGMENT = "OTAFragment";
    public static final int OTAPOSITION = 4;

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


        return rootView;
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
}
