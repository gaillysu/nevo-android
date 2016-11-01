package com.medcorp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.medcorp.R;
import com.medcorp.model.ChooseCityViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Jason on 2016/10/31.
 */

public class SearchWorldAdapter extends BaseAdapter implements Filterable {

    private List<ChooseCityViewModel> cityList;
    private Context mContext;
    private ArrayFilter mFilter;
    private ArrayList<ChooseCityViewModel> mUnfilteredData;

    public SearchWorldAdapter(List<ChooseCityViewModel> cityList, Context context) {
        this.cityList = cityList;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return cityList.size() != 0 ? cityList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return cityList.get(position) != null ? cityList.size() : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.search_world_clock_pull_menu_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.resultTextView.setText(cityList.get(position).getDisplayName());
        return convertView;
    }

        @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }

    private class ArrayFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (mUnfilteredData == null) {
                mUnfilteredData = (ArrayList<ChooseCityViewModel>) cityList;
            }

            if (prefix == null || prefix.length() == 0) {
                ArrayList<ChooseCityViewModel> list = mUnfilteredData;
                results.values = list;
                results.count = list.size();
            } else {
                String prefixString = prefix.toString().toLowerCase();

                ArrayList<ChooseCityViewModel> unfilteredValues = mUnfilteredData;
                int count = unfilteredValues.size();

                ArrayList<ChooseCityViewModel> newValues = new ArrayList<>(count);

                for (int i = 0; i < count; i++) {
                    ChooseCityViewModel pc = unfilteredValues.get(i);
                    if (pc != null) {

                        if (pc.getDisplayName() != null && pc.getDisplayName().contains(prefixString)) {

                            newValues.add(pc);
                        }
                    }
                }

                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            cityList = (List<ChooseCityViewModel>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }

    }


    static class ViewHolder {
        @Bind(R.id.search_city_result_tv)
        TextView resultTextView;
        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
