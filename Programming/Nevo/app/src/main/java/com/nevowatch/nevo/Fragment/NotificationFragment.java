package com.nevowatch.nevo.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;

import com.nevowatch.nevo.MainActivity;
import com.nevowatch.nevo.R;
import com.nevowatch.nevo.View.NotificationItem;
import com.nevowatch.nevo.ble.controller.OnSyncControllerListener;
import com.nevowatch.nevo.ble.model.packet.NevoPacket;

import java.util.ArrayList;
import java.util.List;

/**
 * NotificationFragment
 */
public class NotificationFragment extends Fragment implements OnSyncControllerListener, Switch.OnCheckedChangeListener{

    public static final String NotificationFragment = "Notification Fragment";
    private ListView mListView;
    private List<NotificationItem> mList;
    private NotificationFragmentAdapter mAdatper;
    private Switch[] mSwitchArray = new Switch[]{};

    private void initListView(){
        mList = new ArrayList<NotificationItem>();
        mList.add(new NotificationItem(R.drawable.call_icon50, getResources().getString(R.string.call_string), R.drawable.moreitem));
        mList.add(new NotificationItem(R.drawable.email_icon50, getResources().getString(R.string.email_string), R.drawable.moreitem));
        mList.add(new NotificationItem(R.drawable.facebook_icon50, getResources().getString(R.string.facebook_string), R.drawable.moreitem));
        mList.add(new NotificationItem(R.drawable.sms_icon50, getResources().getString(R.string.sms_string), R.drawable.moreitem));
        mList.add(new NotificationItem(R.drawable.calendar_icon50, getResources().getString(R.string.calendar_string), R.drawable.moreitem));
        mList.add(new NotificationItem(R.drawable.wechat_icon50, getResources().getString(R.string.wechat_string), R.drawable.moreitem));
    }

    private void getSwitchButton(){
        for (int i=0; i<mList.size(); i++){
            LinearLayout linearLayout = (LinearLayout) mListView.getChildAt(i);
            mSwitchArray[i] = (Switch)linearLayout.findViewById(R.id.typeSwitch);
            mSwitchArray[i].setOnCheckedChangeListener(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.notification_fragment, container, false);
        initListView();
        mAdatper = new NotificationFragmentAdapter(getActivity(), R.layout.notification_listview_item, mList);
        mListView = (ListView) rootView.findViewById(R.id.TypeListView);
        mListView.setAdapter(mAdatper);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
       // getSwitchButton();
    }

    @Override
    public void packetReceived(NevoPacket packet) {

    }

    @Override
    public void connectionStateChanged(boolean isConnected) {
        ((MainActivity)getActivity()).replaceFragment(isConnected?2:10, isConnected?NotificationFragment:ConnectAnimationFragment.CONNECTFRAGMENT);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){

        }else {

        }
    }
}
