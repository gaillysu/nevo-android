package com.medcorp.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.medcorp.R;
import com.medcorp.base.BaseActivity;
import com.medcorp.view.SideBar;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Jason on 2016/10/26.
 */

public class EditWorldClockActivity extends BaseActivity {

    @Bind(R.id.main_toolbar)
    Toolbar toolbar;
    @Bind(R.id.toolbar_line_view)
    View view;
    @Bind(R.id.show_all_city_list)
    ListView showAllCityList;
    @Bind(R.id.show_search_city_result_list)
    ListView showSearchResultCityList;
    @Bind(R.id.choose_activity_list_index_sidebar)
    SideBar sortCityBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.world_clock_activity_layout);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView title = (TextView) toolbar.findViewById(R.id.lunar_tool_bar_title);
        title.setText(R.string.choose_activity_title_choose_city_tv);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        view.setVisibility(View.GONE);

    }
}
