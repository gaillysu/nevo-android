package com.medcorp.nevo.fragment.base;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.medcorp.nevo.activity.base.BaseActivity;
import com.medcorp.nevo.fragment.observer.FragmentObservable;

/**
 * Created by karl-john on 16/12/15.
 */
public abstract class BaseObservableFragment extends  BaseFragment implements FragmentObservable {

    private Toolbar toolbar;

    public static BaseObservableFragment instantiate(Context context, String tag)
    {
        return (BaseObservableFragment) instantiate(context,tag,null);
    }

    public BaseActivity getAppCompatActivity(){
        return (BaseActivity) getActivity();
    }


}
