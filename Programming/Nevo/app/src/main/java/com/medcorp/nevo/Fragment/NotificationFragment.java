package com.medcorp.nevo.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.medcorp.nevo.R;
import com.medcorp.nevo.Activity.MainActivity;
import com.medcorp.nevo.ble.controller.SyncController;
import com.medcorp.nevo.ble.listener.OnSyncControllerListener;
import com.medcorp.nevo.ble.model.notification.CalendarNotification;
import com.medcorp.nevo.ble.model.notification.EmailNotification;
import com.medcorp.nevo.ble.model.notification.FacebookNotification;
import com.medcorp.nevo.ble.model.notification.SmsNotification;
import com.medcorp.nevo.ble.model.notification.TelephoneNotification;
import com.medcorp.nevo.ble.model.notification.WeChatNotification;
import com.medcorp.nevo.ble.model.notification.WhatsappNotification;
import com.medcorp.nevo.ble.model.notification.visitor.NotificationDefaultColorVisitor;
import com.medcorp.nevo.ble.model.packet.NevoPacket;
import com.medcorp.nevo.ble.notification.NevoNotificationListener;
import com.medcorp.nevo.ble.util.Constants;
import com.medcorp.nevo.View.NotificationItem;

import java.util.ArrayList;
import java.util.List;

/**
 * NotificationFragment
 */
public class NotificationFragment extends Fragment
        implements OnSyncControllerListener {

    public static final String NOTIFICATIONFRAGMENT = "NotificationFragment";
    public static final int NOTIPOSITION = 4;
    private ListView mListView;
    private List<NotificationItem> mList;
    private NotificationFragmentAdapter mAdatper;
    private NotificationDefaultColorVisitor defaultColorvisitor = new NotificationDefaultColorVisitor();
    private void initListView(){
        mList = new ArrayList<NotificationItem>();
        mList.add(new NotificationItem(new TelephoneNotification().accept(defaultColorvisitor), getResources().getString(R.string.call_string), R.drawable.setting));
        mList.add(new NotificationItem(new EmailNotification().accept(defaultColorvisitor), getResources().getString(R.string.email_string), R.drawable.setting));
        mList.add(new NotificationItem(new FacebookNotification().accept(defaultColorvisitor), getResources().getString(R.string.facebook_string), R.drawable.setting));
        mList.add(new NotificationItem(new SmsNotification().accept(defaultColorvisitor), getResources().getString(R.string.sms_string), R.drawable.setting));
        mList.add(new NotificationItem(new CalendarNotification().accept(defaultColorvisitor), getResources().getString(R.string.calendar_string), R.drawable.setting));
        mList.add(new NotificationItem(new WeChatNotification().accept(defaultColorvisitor), getResources().getString(R.string.wechat_string), R.drawable.setting));
        mList.add(new NotificationItem(new WhatsappNotification().accept(defaultColorvisitor), getResources().getString(R.string.whatsapp_string), R.drawable.setting));
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
}
