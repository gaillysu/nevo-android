package com.medcorp.nevo.fragment.base;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.medcorp.nevo.application.ApplicationModel;
import com.medcorp.nevo.fragment.observer.FragmentObservable;

/**
 * Created by Karl on 10/16/15.
 */
public abstract class BaseFragment extends Fragment{

    private ApplicationModel applicationModel;

    public ApplicationModel getModel() {
        if (applicationModel == null) {
            applicationModel = (ApplicationModel) getActivity().getApplication();
        }
        return applicationModel;
    }

    public static BaseFragment instantiate(Context context, String tag)
    {
           return (BaseFragment) instantiate(context,tag,null);
    }


}