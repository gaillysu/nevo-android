package com.nevowatch.nevo.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.nevowatch.nevo.MainActivity;
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
public class NotificationFragment extends Fragment
        implements OnSyncControllerListener/*, AdapterView.OnItemClickListener*/{

    public static final String NOTIFICATIONFRAGMENT = "NotificationFragment";
    public static final int NOTIPOSITION = 3;
    private ListView mListView;
    private List<NotificationItem> mList;
    private NotificationFragmentAdapter mAdatper;

    private void initListView(){
        mList = new ArrayList<NotificationItem>();
        mList.add(new NotificationItem(getDefaultColor(R.drawable.orange_indicator), getResources().getString(R.string.call_string), R.drawable.setting));
        mList.add(new NotificationItem(getDefaultColor(R.drawable.yellow_indicator), getResources().getString(R.string.email_string), R.drawable.setting));
        mList.add(new NotificationItem(getDefaultColor(R.drawable.blue_indicator), getResources().getString(R.string.facebook_string), R.drawable.setting));
        mList.add(new NotificationItem(getDefaultColor(R.drawable.green_indicator), getResources().getString(R.string.sms_string), R.drawable.setting));
        mList.add(new NotificationItem(getDefaultColor(R.drawable.red_indicator), getResources().getString(R.string.calendar_string), R.drawable.setting));
        mList.add(new NotificationItem(getDefaultColor(R.drawable.grass_green_indicator), getResources().getString(R.string.wechat_string), R.drawable.setting));
        mList.add(new NotificationItem(getDefaultColor(R.drawable.grass_green_indicator), getResources().getString(R.string.whatsapp_string), R.drawable.setting));
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
      //  mListView.setOnItemClickListener(this);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(SyncController.Singleton.getInstance(getActivity())!=null && !SyncController.Singleton.getInstance(getActivity()).isConnected()){
            ((MainActivity)getActivity()).replaceFragment(10, ConnectAnimationFragment.CONNECTFRAGMENT);
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

    private int getDefaultColor(int defColor){
        switch (defColor){
            case R.drawable.orange_indicator:
                return R.drawable.orange_indicator;
            case R.drawable.yellow_indicator:
                return R.drawable.yellow_indicator;
            case R.drawable.grass_green_indicator:
                return R.drawable.grass_green_indicator;
            case R.drawable.green_indicator:
                return R.drawable.green_indicator;
            case R.drawable.red_indicator:
                return R.drawable.red_indicator;
            default:
                break;
        }
        return 0;
    }
/*
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), PaletteActivity.class);
        intent.putExtra("Position", position);
        getActivity().startActivity(intent);
    }*/
}
