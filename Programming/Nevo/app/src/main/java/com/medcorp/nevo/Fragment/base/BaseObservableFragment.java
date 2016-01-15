package com.medcorp.nevo.fragment.base;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.transition.Fade;
import android.util.Log;
import android.view.Window;

import com.medcorp.nevo.R;
import com.medcorp.nevo.activity.SettingAboutActivity;
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

    public void startActivity(Class <?> cls) {
        Intent intent = new Intent(getActivity(), cls);
        getActivity().getWindow().setExitTransition(new Explode());
        getActivity().startActivity(intent);
    }
}
