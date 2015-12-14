package com.medcorp.nevo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.medcorp.nevo.R;

import butterknife.ButterKnife;

/**
 * Created by karl-john on 14/12/15.
 */
public class SettingsFragment extends Fragment{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sleep, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
}
