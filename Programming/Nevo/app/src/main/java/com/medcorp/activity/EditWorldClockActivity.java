package com.medcorp.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.medcorp.R;
import com.medcorp.adapter.ChooseCityAdapter;
import com.medcorp.adapter.SearchWorldAdapter;
import com.medcorp.base.BaseActivity;
import com.medcorp.model.ChooseCityViewModel;
import com.medcorp.util.Preferences;
import com.medcorp.view.PinyinComparator;
import com.medcorp.view.SideBar;

import net.medcorp.library.worldclock.City;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Jason on 2016/10/26.
 *
 */

public class EditWorldClockActivity extends BaseActivity {

    @Bind(R.id.main_toolbar)
    Toolbar toolbar;
    @Bind(R.id.toolbar_line_view)
    View view;
    @Bind(R.id.show_all_city_list)
    ListView showAllCityList;
    @Bind(R.id.choose_activity_list_index_sidebar)
    SideBar sortCityBar;
    @Bind(R.id.search_world_city_edit_city_name_ed)
    EditText searchCityAutoCompleteTv;
    @Bind(R.id.search_city_result_list)
    ListView searchResultListView;

    private Realm realm = Realm.getDefaultInstance();
    private RealmResults<City> cities;
    private List<ChooseCityViewModel> chooseCityViewModelsList;
    private PinyinComparator pinyinComparator;
    private ChooseCityAdapter allCityAdapter;
    private SearchWorldAdapter autoAdapter;
    private List<ChooseCityViewModel> resultList;

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
        initData();
    }

    private void initData() {
        resultList = new ArrayList<>();
        autoAdapter = new SearchWorldAdapter(resultList, this);
        searchResultListView.setAdapter(autoAdapter);

        cities = realm.where(City.class).findAll();
        chooseCityViewModelsList = new ArrayList<>();
        pinyinComparator = new PinyinComparator();

        for (int i = 0; i < cities.size(); i++) {
            chooseCityViewModelsList.add(new ChooseCityViewModel(cities.get(i)));
        }

        Collections.sort(chooseCityViewModelsList, pinyinComparator);
        allCityAdapter = new ChooseCityAdapter(this, chooseCityViewModelsList);
        showAllCityList.setAdapter(allCityAdapter);
        sortCityBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                int position = allCityAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    showAllCityList.setSelection(position);
                }
            }
        });

        showAllCityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectCity(chooseCityViewModelsList.get(position));
            }
        });
        searchResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectCity(resultList.get(position));
            }
        });
        searchCityAutoCompleteTv.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode()
                        == KeyEvent.KEYCODE_ENTER)) {
                    resultList.clear();
                    return true;
                }
                return false;
            }
        });

        searchCityAutoCompleteTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence content, int start, int before, int count) {
                Log.i("jason", content.toString());
                resultList.clear();
                searchCity(content.toString());
                autoAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @OnClick(R.id.edit_world_clock_cancel_search_button)
    public void cancelClick() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void searchCity(String content) {
        if (!TextUtils.isEmpty(content)) {
            searchResultListView.setVisibility(View.VISIBLE);
            for (ChooseCityViewModel chooseCityModel : chooseCityViewModelsList) {
                if (chooseCityModel.getDisplayName().toLowerCase().contains(content.toLowerCase())) {
                    resultList.add(chooseCityModel);
                }
            }
            if (resultList.size() > 0) {
                Collections.sort(resultList, pinyinComparator);
                autoAdapter.notifyDataSetChanged();
            }
        }
    }

    public void selectCity(ChooseCityViewModel chooseCityModel) {
        String name = chooseCityModel.getDisplayName();
        Preferences.saveUserSelectCity(EditWorldClockActivity.this, chooseCityModel.getDisplayName());
        searchCityAutoCompleteTv.setText(name);
        allCityAdapter.notifyDataSetChanged();
        finish();
    }
}