package com.medcorp.nevo.validic.request.routine;

import com.medcorp.nevo.validic.model.routine.ValidicReadMoreRoutineRecordsModel;
import com.medcorp.nevo.validic.retrofit.Validic;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

/**
 * Created by gaillysu on 16/3/8.
 */
public class GetMoreRoutineRecordsRequest extends RetrofitSpiceRequest<ValidicReadMoreRoutineRecordsModel,Validic> {

    private String   organizationId;
    private String   organizationTokenKey;
    private String   validicUserId;

    public GetMoreRoutineRecordsRequest(String organizationId, String organizationTokenKey, String validicUserId) {
        super(ValidicReadMoreRoutineRecordsModel.class,Validic.class);
        this.organizationId = organizationId;
        this.organizationTokenKey = organizationTokenKey;
        this.validicUserId = validicUserId;
    }

    @Override
    public ValidicReadMoreRoutineRecordsModel loadDataFromNetwork() throws Exception {
        return getService().getMoreRoutineRecordsRequest(organizationId, validicUserId, organizationTokenKey);
    }
}
