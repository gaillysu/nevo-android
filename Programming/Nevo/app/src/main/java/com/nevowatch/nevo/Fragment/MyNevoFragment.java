package com.nevowatch.nevo.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nevowatch.nevo.FontManager;
import com.nevowatch.nevo.MainActivity;
import com.nevowatch.nevo.OTAActivity;
import com.nevowatch.nevo.R;
import com.nevowatch.nevo.View.StepPickerView;
import com.nevowatch.nevo.ble.controller.OnSyncControllerListener;
import com.nevowatch.nevo.ble.controller.SyncController;
import com.nevowatch.nevo.ble.model.packet.NevoPacket;
import com.nevowatch.nevo.ble.model.request.NumberOfStepsGoal;

/**
 * GoalFragment aims to set goals including Moderate, Intensive, Sportive and Custom
 */
public class MyNevoFragment extends Fragment implements View.OnClickListener,OnSyncControllerListener {


    private static final String TAG="MyNevoFragment";
    public static final String MYNEVOFRAGMENT = "MyNevoFragment";
    public static final int MYNEVOPOSITION = 4;
    private Context mCtx;
    private Button mynevo_pushOTAButton;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.mynevo_fragment, container, false);
        mCtx = getActivity();
        mynevo_pushOTAButton = (Button) rootView.findViewById(R.id.mynevo_push_ota);
        mynevo_pushOTAButton.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.mynevo_push_ota:
                Intent intent = new Intent(mCtx, OTAActivity.class);
                mCtx.startActivity(intent);
                break;
            default:
                break;
        }

    }


    @Override
    public void packetReceived(NevoPacket packet) {

    }

    @Override
    public void connectionStateChanged(boolean isConnected) {
        ((MainActivity)getActivity()).replaceFragment(isConnected?GoalFragment.GOALPOSITION:ConnectAnimationFragment.CONNECTPOSITION, isConnected?GoalFragment.GOALFRAGMENT:ConnectAnimationFragment.CONNECTFRAGMENT);
    }
}