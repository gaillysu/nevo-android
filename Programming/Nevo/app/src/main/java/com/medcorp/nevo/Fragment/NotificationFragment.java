package com.medcorp.nevo.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.medcorp.nevo.Activity.MainActivity;
import com.medcorp.nevo.Model.Notification.NotificationType;
import com.medcorp.nevo.R;
import com.medcorp.nevo.View.NotificationItem;
import com.medcorp.nevo.ble.controller.OnSyncControllerListener;
import com.medcorp.nevo.ble.controller.SyncController;
import com.medcorp.nevo.ble.model.packet.NevoPacket;
import com.medcorp.nevo.ble.notification.NevoNotificationListener;
import com.medcorp.nevo.ble.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * NotificationFragment
 */
public class NotificationFragment extends Fragment
        implements OnSyncControllerListener{

    public static final String NOTIFICATIONFRAGMENT = "NotificationFragment";
    public static final int NOTIPOSITION = 3;
    private ListView mListView;
    private List<NotificationItem> mList;
    private NotificationFragmentAdapter mAdatper;

    private void initListView(){
        mList = new ArrayList<NotificationItem>();
        mList.add(new NotificationItem(getDefaultColor(NotificationType.Call), getResources().getString(R.string.call_string), R.drawable.setting));
        mList.add(new NotificationItem(getDefaultColor(NotificationType.Email), getResources().getString(R.string.email_string), R.drawable.setting));
        mList.add(new NotificationItem(getDefaultColor(NotificationType.Facebook), getResources().getString(R.string.facebook_string), R.drawable.setting));
        mList.add(new NotificationItem(getDefaultColor(NotificationType.SMS), getResources().getString(R.string.sms_string), R.drawable.setting));
        mList.add(new NotificationItem(getDefaultColor(NotificationType.Calendar), getResources().getString(R.string.calendar_string), R.drawable.setting));
        mList.add(new NotificationItem(getDefaultColor(NotificationType.Wechat), getResources().getString(R.string.wechat_string), R.drawable.setting));
        mList.add(new NotificationItem(getDefaultColor(NotificationType.Whatsapp), getResources().getString(R.string.whatsapp_string), R.drawable.setting));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NevoNotificationListener.getNotificationAccessPermission(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.notification_fragment, container, false);
        initListView();
        mListView = (ListView) rootView.findViewById(R.id.TypeListView);
        mAdatper = new NotificationFragmentAdapter(getActivity(), R.layout.notification_listview_item, mList, mListView);
        mListView.setAdapter(mAdatper);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(SyncController.Singleton.getInstance(getActivity())!=null && !SyncController.Singleton.getInstance(getActivity()).isConnected()){
            ((MainActivity)getActivity()).replaceFragment(ConnectAnimationFragment.CONNECTPOSITION, ConnectAnimationFragment.CONNECTFRAGMENT);
        }
        mAdatper.notifyDataSetChanged();
    }

    @Override
    public void packetReceived(NevoPacket packet) {

    }

    @Override
    public void connectionStateChanged(boolean isConnected) {
        ((MainActivity)getActivity()).replaceFragment(isConnected?NotificationFragment.NOTIPOSITION:ConnectAnimationFragment.CONNECTPOSITION, isConnected?NotificationFragment.NOTIFICATIONFRAGMENT:ConnectAnimationFragment.CONNECTFRAGMENT);
    }
    @Override
    public void firmwareVersionReceived(Constants.DfuFirmwareTypes whichfirmware, String version) {

    }

    private int getDefaultColor(NotificationType type){
        if(type == NotificationType.Call)
            return R.drawable.orange_indicator;
        if(type == NotificationType.Email)
            return R.drawable.yellow_indicator;
        if(type == NotificationType.Facebook)
            return R.drawable.blue_indicator;
        if(type == NotificationType.SMS)
            return R.drawable.green_indicator;
        if(type == NotificationType.Calendar)
            return R.drawable.red_indicator;
        if(type == NotificationType.Wechat)
            return R.drawable.grass_green_indicator;
        if(type == NotificationType.Whatsapp)
            return R.drawable.grass_green_indicator;
        return 0;
    }
}
