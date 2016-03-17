package com.medcorp.nevo.validic.request;

import android.util.Log;

import com.google.gson.Gson;
import com.medcorp.nevo.validic.model.StepsModel;
import com.medcorp.nevo.validic.model.UpdateRecordRequestObject;
import com.medcorp.nevo.validic.model.ValidicRecordModel;
import com.medcorp.nevo.validic.retrofit.Validic;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by gaillysu on 16/3/8.
 */
public class UpdateRecordRequest extends RetrofitSpiceRequest<ValidicRecordModel,Validic> implements BaseRetroRequest<UpdateRecordRequestObject> {

    private String   organizationId;
    private String   organizationTokenKey;
    private String   validicRecordId;
    private String   validicUserId;
    private int steps;

    public UpdateRecordRequest(String   organizationId,String   organizationTokenKey,String validicUserId,String  validicRecordId,int steps) {
        super(ValidicRecordModel.class,Validic.class);
        this.organizationId = organizationId;
        this.organizationTokenKey = organizationTokenKey;
        this.validicUserId = validicUserId;
        this.validicRecordId = validicRecordId;
        this.steps = steps;
    }


    @Override
    public UpdateRecordRequestObject buildRequestBody() {
        UpdateRecordRequestObject object = new UpdateRecordRequestObject();
        object.setAccess_token(organizationTokenKey);
        StepsModel stepsModel = new StepsModel();
        stepsModel.setSteps(steps);
        object.setRoutine(stepsModel);
        Log.i(this.getClass().getSimpleName(), "object: " + new Gson().toJson(object));
        return object;
    }

    @Override
    public ValidicRecordModel loadDataFromNetwork() throws Exception {
       return getService().updateRecordRequest(buildRequestBody(),organizationId,validicUserId,validicRecordId,"application/json");
    }
}
