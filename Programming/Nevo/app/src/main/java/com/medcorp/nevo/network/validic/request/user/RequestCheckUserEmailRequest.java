package com.medcorp.nevo.network.validic.request.user;

import com.medcorp.nevo.network.base.BaseRetroRequest;
import com.medcorp.nevo.network.med.model.RequestCheckRequestBody;
import com.medcorp.nevo.network.med.model.RequestTokenBodyParameters;
import com.medcorp.nevo.network.med.retrofit.MedCorp;
import com.medcorp.nevo.network.validic.response.RequestCheckResponse;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

/**
 * Created by Administrator on 2016/7/13.
 */
public class RequestCheckUserEmailRequest extends
        RetrofitSpiceRequest<RequestCheckRequestBody,MedCorp> implements BaseRetroRequest<RequestCheckResponse> {


    private String token;
    private String email;

    public RequestCheckUserEmailRequest(String token,String email) {
        super(RequestCheckRequestBody.class, MedCorp.class);
        this.token = token;
        this.email = email;
    }


    @Override
    public RequestCheckResponse buildRequestBody() {
        return null;
    }

    @Override
    public RequestCheckRequestBody loadDataFromNetwork() throws Exception {
        RequestTokenBodyParameters parameters = new RequestTokenBodyParameters(email);
        return new RequestCheckRequestBody(token,parameters);
    }
}
