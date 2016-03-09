package com.medcorp.nevo.validic;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.medcorp.nevo.R;
import com.medcorp.nevo.validic.model.NevoUser;
import com.medcorp.nevo.validic.model.Profile;
import com.medcorp.nevo.validic.request.BaseRequest;
import com.medcorp.nevo.validic.request.CreateUserRequest;
import com.octo.android.robospice.JacksonSpringAndroidSpiceService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.request.listener.RequestListener;

/**
 * Created by gaillysu on 16/3/8.
 */
public class ValidicManager {
    private Context context;
    private SpiceManager spiceManager;

    public ValidicManager(Context context)
    {
        this.context = context;
        spiceManager = new SpiceManager(JacksonSpringAndroidSpiceService.class);
        startSpiceManager();
    }

    public String getOrganizationID(){
        return context.getString(R.string.key_validic_organization_id);
    }

    public String getOrganizationToken(){
        return context.getString(R.string.key_validic_organization_token);
    }

    public void startSpiceManager()
    {
        if(!spiceManager.isStarted()) {
            spiceManager.start(context);
        }
    }
    public void stopSpiceManager()
    {
        if(spiceManager!=null) {
            spiceManager.shouldStop();
        }
    }

    public <T> void performRequest(BaseRequest<T> request,RequestListener<T> response)
    {
        spiceManager.execute(request, request.getClass().getName(),
                10*DurationInMillis.ONE_SECOND, response);
    }

}
