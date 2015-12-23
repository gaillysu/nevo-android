package com.medcorp.nevo.fragment.base;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.medcorp.nevo.fragment.observer.FragmentObservable;

/**
 * Created by karl-john on 16/12/15.
 */
public abstract class BaseObservableFragment extends  BaseFragment implements FragmentObservable {

    public static BaseObservableFragment instantiate(Context context, String tag)
    {
        return (BaseObservableFragment) instantiate(context,tag,null);
    }

    public AppCompatActivity getAppCompatActivity(){
        return (AppCompatActivity) getActivity();
    }
}
