package com.medcorp.nevo.validic.request.sleep;

import com.medcorp.nevo.validic.model.sleep.ValidicReadMoreSleepRecordsModel;
import com.medcorp.nevo.validic.retrofit.Validic;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

/**
 * Created by gaillysu on 16/3/8.
 */
public class GetMoreSleepRecordsRequest extends RetrofitSpiceRequest<ValidicReadMoreSleepRecordsModel,Validic> {

    private String   organizationId;
    private String   organizationTokenKey;
    private String   validicUserId;

    public GetMoreSleepRecordsRequest(String organizationId, String organizationTokenKey, String validicUserId) {
        super(ValidicReadMoreSleepRecordsModel.class,Validic.class);
        this.organizationId = organizationId;
        this.organizationTokenKey = organizationTokenKey;
        this.validicUserId = validicUserId;
    }

    @Override
    public ValidicReadMoreSleepRecordsModel loadDataFromNetwork() throws Exception {
        return getService().getMoreSleepRecordsRequest(organizationId, validicUserId, organizationTokenKey);
    }
}
