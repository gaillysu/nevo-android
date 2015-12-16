package com.medcorp.nevo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.medcorp.nevo.R;
import com.medcorp.nevo.fragment.base.BaseFragment;

/**
 * Created by Karl on 12/10/15.
 */
public class SleepHistoryFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sleep_history, container, false);
        // TODO Gailly, implement Sleep history fragment. See design.
        return view;
    }
}