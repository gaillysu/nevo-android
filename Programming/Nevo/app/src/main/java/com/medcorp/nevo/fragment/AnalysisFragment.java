package com.medcorp.nevo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.medcorp.nevo.R;
import com.medcorp.nevo.fragment.base.BaseObservableFragment;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/7/14.
 */
public class AnalysisFragment extends BaseObservableFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.analysis_fragment_layout,container,false);
        ButterKnife.bind(this,view);

        return view;
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.choose_goal_menu).setVisible(false);
        menu.findItem(R.id.add_menu).setVisible(false);
    }
}



