package com.medcorp.nevo.validic.request.routine;

import android.util.Log;

import com.google.gson.Gson;
import com.medcorp.nevo.validic.model.routine.RoutineModel;
import com.medcorp.nevo.validic.model.routine.UpdateRoutineRecordRequestObject;
import com.medcorp.nevo.validic.model.routine.ValidicRoutineRecordModel;
import com.medcorp.nevo.validic.request.BaseRetroRequest;
import com.medcorp.nevo.validic.retrofit.Validic;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

/**
 * Created by gaillysu on 16/3/8.
 */
public class UpdateRoutineRecordRequest extends RetrofitSpiceRequest<ValidicRoutineRecordModel,Validic> implements BaseRetroRequest<UpdateRoutineRecordRequestObject> {

    private String   organizationId;
    private String   organizationTokenKey;
    private String   validicRecordId;
    private String   validicUserId;
    private int steps;

    public UpdateRoutineRecordRequest(String organizationId, String organizationTokenKey, String validicUserId, String validicRecordId, int steps) {
        super(ValidicRoutineRecordModel.class,Validic.class);
        this.organizationId = organizationId;
        this.organizationTokenKey = organizationTokenKey;
        this.validicUserId = validicUserId;
        this.validicRecordId = validicRecordId;
        this.steps = steps;
    }


    @Override
    public UpdateRoutineRecordRequestObject buildRequestBody() {
        UpdateRoutineRecordRequestObject object = new UpdateRoutineRecordRequestObject();
        object.setAccess_token(organizationTokenKey);
        RoutineModel stepsModel = new RoutineModel();
        stepsModel.setSteps(steps);
        object.setRoutine(stepsModel);
        Log.i(this.getClass().getSimpleName(), "object: " + new Gson().toJson(object));
        return object;
    }

    @Override
    public ValidicRoutineRecordModel loadDataFromNetwork() throws Exception {
       return getService().updateRoutineRecordRequest(buildRequestBody(), organizationId, validicUserId, validicRecordId, "application/json");
    }
}
