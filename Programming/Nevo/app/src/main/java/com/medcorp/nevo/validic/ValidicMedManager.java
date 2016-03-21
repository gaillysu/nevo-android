package com.medcorp.nevo.validic;

import android.content.Context;

import com.medcorp.nevo.R;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.request.SpiceRequest;
import com.octo.android.robospice.request.listener.RequestListener;

/**
 * Created by gaillysu on 16/3/8.
 */
public class ValidicMedManager {
    private Context context;
    private SpiceManager spiceManager;

    public ValidicMedManager(Context context)
    {
        this.context = context;
        spiceManager = new SpiceManager(BaseMedRetroService.class);
        startSpiceManager();
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

    public void execute(SpiceRequest request, RequestListener listener){
        spiceManager.execute(request, listener);
    }

}
