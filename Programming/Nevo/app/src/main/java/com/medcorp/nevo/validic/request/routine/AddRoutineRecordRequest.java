package com.medcorp.nevo.validic.request.routine;

import android.util.Log;

import com.google.gson.Gson;
import com.medcorp.nevo.validic.model.routine.AddRoutineRecordRequestObject;
import com.medcorp.nevo.validic.model.routine.ValidicRoutineRecord;
import com.medcorp.nevo.validic.model.routine.ValidicRoutineRecordModel;
import com.medcorp.nevo.validic.request.BaseRetroRequest;
import com.medcorp.nevo.validic.retrofit.Validic;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

/**
 * Created by gaillysu on 16/3/8.
 */
public class AddRoutineRecordRequest extends RetrofitSpiceRequest<ValidicRoutineRecordModel,Validic> implements BaseRetroRequest<AddRoutineRecordRequestObject> {

    private ValidicRoutineRecord record;
    private String   organizationId;
    private String   organizationTokenKey;
    private String   validicUserId;

    public AddRoutineRecordRequest(ValidicRoutineRecord record, String organizationId, String organizationTokenKey, String validicUserId) {
        super(ValidicRoutineRecordModel.class,Validic.class);
        this.record = record;
        this.organizationId = organizationId;
        this.organizationTokenKey = organizationTokenKey;
        this.validicUserId = validicUserId;
    }
    @Override
    public AddRoutineRecordRequestObject buildRequestBody() {
        AddRoutineRecordRequestObject object = new AddRoutineRecordRequestObject();
        object.setAccess_token(organizationTokenKey);
        object.setRoutine(record);
        Log.i(this.getClass().getSimpleName(),"object: "+new Gson().toJson(object));
        return object;
    }

    @Override
    public ValidicRoutineRecordModel loadDataFromNetwork() throws Exception {
        return getService().addRoutineRecordRequest(buildRequestBody(), organizationId, validicUserId, "application/json");
    }
}
