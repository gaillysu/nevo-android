package com.medcorp.nevo.validic.request;

import android.test.AndroidTestCase;
import android.util.Log;

import com.google.gson.Gson;
import com.medcorp.nevo.validic.ValidicManager;
import com.medcorp.nevo.validic.model.NevoUser;
import com.medcorp.nevo.validic.model.Profile;
import com.medcorp.nevo.validic.model.ValidicUser;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

/**
 * Created by gaillysu on 16/3/8.
 */
public class CreateUserRequestTest extends AndroidTestCase {
    private static final String TAG = CreateUserRequestTest.class.getSimpleName();
    ValidicManager validicManager;
    CreateUserRequest createUserRequest;
    NevoUser nevoUser;
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        validicManager = new ValidicManager(getContext());
        validicManager.startSpiceManager();
        nevoUser = new NevoUser("123456",null/*new Profile("M","Shenzhen","2016",180f,75f)*/);
        createUserRequest = new CreateUserRequest(nevoUser,validicManager.getOrganizationID(),validicManager.getOrganizationToken());
        String body  = createUserRequest.buildRequestBody();
        String url = createUserRequest.buildRequestURL();
        Log.i(TAG,"url = " + url);
        Log.i(TAG,"body = " + body);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        validicManager.stopSpiceManager();
    }
    public void testCreateUser()
    {
        validicManager.performRequest(createUserRequest, new RequestListener<ValidicUser>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                Log.e(TAG, spiceException.getMessage());
            }

            @Override
            public void onRequestSuccess(ValidicUser validicUser) {
                String result = new Gson().toJson(validicUser);
                Log.i(TAG,result);
            }
        });
    }
}
