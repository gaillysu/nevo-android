package com.medcorp.nevo.validic.request;

import android.test.AndroidTestCase;
import android.util.Log;

import com.medcorp.nevo.validic.ValidicManager;
import com.medcorp.nevo.validic.model.VerifyCredentialModel;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import org.json.JSONObject;

/**
 * Created by gaillysu on 16/3/8.
 */
public class VerifyCredentialRequestTest extends AndroidTestCase {
    private static final String TAG = VerifyCredentialRequestTest.class.getSimpleName();
    ValidicManager validicManager;
    VerifyCredentialRequest verifyCredentialRequest;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        validicManager = new ValidicManager(getContext());
        validicManager.startSpiceManager();
        verifyCredentialRequest = new VerifyCredentialRequest(validicManager.getOrganizationID(),validicManager.getOrganizationToken());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        validicManager.stopSpiceManager();
    }

    public void testVerify()
    {
        validicManager.performRequest(verifyCredentialRequest, new RequestListener<VerifyCredentialModel>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                Log.e(TAG, spiceException.toString());
            }

            @Override
            public void onRequestSuccess(VerifyCredentialModel jsonObject) {
                Log.i(TAG,jsonObject.toString());
            }
        });
    }
}