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
    private String   start_timestamp;
    private String   end_timestamp;

    public GetMoreSleepRecordsRequest(String organizationId, String organizationTokenKey, String validicUserId,String   start_timestamp,String   end_timestamp) {
        super(ValidicReadMoreSleepRecordsModel.class,Validic.class);
        this.organizationId = organizationId;
        this.organizationTokenKey = organizationTokenKey;
        this.validicUserId = validicUserId;
        this.start_timestamp = start_timestamp;
        this.end_timestamp = end_timestamp;
    }

    @Override
    public ValidicReadMoreSleepRecordsModel loadDataFromNetwork() throws Exception {
        return getService().getMoreSleepRecordsRequest(organizationId, validicUserId, organizationTokenKey,start_timestamp,end_timestamp,1);
    }
}
