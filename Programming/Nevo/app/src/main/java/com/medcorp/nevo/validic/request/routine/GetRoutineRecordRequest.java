package com.medcorp.nevo.validic.request.routine;

import com.medcorp.nevo.validic.model.routine.ValidicReadRoutineRecordModel;
import com.medcorp.nevo.validic.retrofit.Validic;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

/**
 * Created by gaillysu on 16/3/8.
 */
public class GetRoutineRecordRequest extends RetrofitSpiceRequest<ValidicReadRoutineRecordModel,Validic> {

    private String   organizationId;
    private String   organizationTokenKey;
    private String   validicUserId;
    private String   validicRecordId;

    public GetRoutineRecordRequest(String organizationId, String organizationTokenKey, String validicUserId, String validicRecordId) {
        super(ValidicReadRoutineRecordModel.class,Validic.class);

        this.organizationId = organizationId;
        this.organizationTokenKey = organizationTokenKey;
        this.validicUserId = validicUserId;
        this.validicRecordId = validicRecordId;
    }

    @Override
    public ValidicReadRoutineRecordModel loadDataFromNetwork() throws Exception {
        return getService().getRoutineRecordRequest(organizationId, validicUserId, validicRecordId, organizationTokenKey);
    }
}