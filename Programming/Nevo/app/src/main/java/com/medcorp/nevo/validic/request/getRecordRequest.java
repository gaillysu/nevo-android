package com.medcorp.nevo.validic.request;

import com.medcorp.nevo.validic.model.ValidicReadRecordModel;
import com.medcorp.nevo.validic.retrofit.Validic;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

/**
 * Created by gaillysu on 16/3/8.
 */
public class GetRecordRequest extends RetrofitSpiceRequest<ValidicReadRecordModel,Validic> {

    private String   organizationId;
    private String   organizationTokenKey;
    private String   validicUserId;
    private String   validicRecordId;

    public GetRecordRequest(String organizationId, String organizationTokenKey, String validicUserId, String validicRecordId) {
        super(ValidicReadRecordModel.class,Validic.class);

        this.organizationId = organizationId;
        this.organizationTokenKey = organizationTokenKey;
        this.validicUserId = validicUserId;
        this.validicRecordId = validicRecordId;
    }

    @Override
    public ValidicReadRecordModel loadDataFromNetwork() throws Exception {
        return getService().getRecordRequest(organizationId,validicUserId,validicRecordId,organizationTokenKey);
    }
}
