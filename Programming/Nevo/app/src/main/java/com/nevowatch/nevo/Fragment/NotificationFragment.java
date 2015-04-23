package com.nevowatch.nevo.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.nevowatch.nevo.MainActivity;
import com.nevowatch.nevo.PaletteActivity;
import com.nevowatch.nevo.R;
import com.nevowatch.nevo.View.NotificationItem;
import com.nevowatch.nevo.ble.controller.OnSyncControllerListener;
import com.nevowatch.nevo.ble.controller.SyncController;
import com.nevowatch.nevo.ble.model.packet.NevoPacket;

import java.util.ArrayList;
import java.util.List;

/**
 * NotificationFragment
 */
public class NotificationFragment extends Fragment
        implements OnSyncControllerListener, AdapterView.OnItemClickListener{

    public static final String NotificationFragment = "Notification Fragment";
    private ListView mListView;
    private List<NotificationItem> mList;
    private NotificationFragmentAdapter mAdatper;

    private void initListView(){
        mList = new ArrayList<NotificationItem>();
        mList.add(new NotificationItem(getResources().getString(R.string.call_string), R.drawable.setting));
        mList.add(new NotificationItem(getResources().getString(R.string.email_string), R.drawable.setting));
        mList.add(new NotificationItem(getResources().getString(R.string.facebook_string), R.drawable.setting));
        mList.add(new NotificationItem(getResources().getString(R.string.sms_string), R.drawable.setting));
        mList.add(new NotificationItem(getResources().getString(R.string.calendar_string), R.drawable.setting));
        mList.add(new NotificationItem(getResources().getString(R.string.wechat_string), R.drawable.setting));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.notification_fragment, container, false);
        initListView();
        mAdatper = new NotificationFragmentAdapter(getActivity(), R.layout.notification_listview_item, mList);
        mListView = (ListView) rootView.findViewById(R.id.TypeListView);
        mListView.setAdapter(mAdatper);
        mListView.setOnItemClickListener(this);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(SyncController.Singleton.getInstance(getActivity())!=null && !SyncController.Singleton.getInstance(getActivity()).isConnected()){
            ((MainActivity)getActivity()).replaceFragment(10, ConnectAnimationFragment.CONNECTFRAGMENT);
        }
    }

    @Override
    public void packetReceived(NevoPacket packet) {

    }

    @Override
    public void connectionStateChanged(boolean isConnected) {
        ((MainActivity)getActivity()).replaceFragment(isConnected?2:10, isConnected?NotificationFragment:ConnectAnimationFragment.CONNECTFRAGMENT);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), PaletteActivity.class);
        intent.putExtra("Position", position);
        getActivity().startActivity(intent);
    }
}
