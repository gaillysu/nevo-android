package com.medcorp.nevo.network.validic.model;

import com.medcorp.nevo.network.base.BaseRequest;
import com.medcorp.nevo.network.med.model.RequestTokenBodyParameters;
import com.medcorp.nevo.network.med.retrofit.MedCorp;

/**
 * Created by Administrator on 2016/7/20.
 */
public class CheckUserInputEmailIsTrueRequest extends BaseRequest<RequestTokenResponse, MedCorp>
        implements BaseRequest.BaseRetroRequestBody<RequestTokenBody> {

    private String token;
    private String email;

    public CheckUserInputEmailIsTrueRequest(String token, String email) {
        super(RequestTokenResponse.class, MedCorp.class);
        this.token = token;
        this.email = email;
    }

    @Override
    public RequestTokenResponse loadDataFromNetwork() throws Exception {
        return getService().requestToken(buildRequestBody(), buildAuthorization(), CONTENT_TYPE);
    }

    @Override
    public RequestTokenBody buildRequestBody() {
        RequestTokenBodyParameters parameters = new RequestTokenBodyParameters(email);
        return new RequestTokenBody(token, parameters);
    }
}
