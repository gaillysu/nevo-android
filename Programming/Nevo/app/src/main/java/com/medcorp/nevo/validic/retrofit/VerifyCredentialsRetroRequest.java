package com.medcorp.nevo.validic.retrofit;

import com.medcorp.nevo.validic.model.VerifyCredentialModel;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

/**
 * Created by Karl on 3/16/16.
 */
public class VerifyCredentialsRetroRequest extends RetrofitSpiceRequest<VerifyCredentialModel, Validic> {

    private String organization;
    private String token;

    public VerifyCredentialsRetroRequest(String organization, String token) {
        super(VerifyCredentialModel.class, Validic.class);
        this.organization = organization;
        this.token = token;
    }

    @Override
    public VerifyCredentialModel loadDataFromNetwork() throws Exception {
        return getService().verifyCredential(organization,token);
    }
}
