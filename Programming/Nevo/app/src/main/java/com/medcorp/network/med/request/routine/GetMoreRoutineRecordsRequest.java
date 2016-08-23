package com.medcorp.network.med.request.routine;

import com.medcorp.network.base.BaseRequest;
import com.medcorp.network.med.model.MedReadMoreRoutineRecordsModel;
import com.medcorp.network.med.retrofit.MedCorp;
import com.medcorp.network.validic.model.ValidicReadMoreRoutineRecordsModel;
import com.medcorp.network.validic.retrofit.Validic;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

/**
 * Created by gaillysu on 16/3/8.
 */
public class GetMoreRoutineRecordsRequest extends BaseRequest<MedReadMoreRoutineRecordsModel,MedCorp> {

    private String   organizationTokenKey;
    private String   userID;
    private String   start_timestamp;
    private String   end_timestamp;

    public GetMoreRoutineRecordsRequest(String organizationTokenKey, String userID,String start_timestamp,String end_timestamp) {
        super(MedReadMoreRoutineRecordsModel.class,MedCorp.class);
        this.organizationTokenKey = organizationTokenKey;
        this.userID = userID;
        this.start_timestamp = start_timestamp;
        this.end_timestamp = end_timestamp;
    }

    @Override
    public MedReadMoreRoutineRecordsModel loadDataFromNetwork() throws Exception {
        return getService().getMoreRoutineRecordsRequest(buildAuthorization(),userID,organizationTokenKey,start_timestamp,end_timestamp);
    }
}
