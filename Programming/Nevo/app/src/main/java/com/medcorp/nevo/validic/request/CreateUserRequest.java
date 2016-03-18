package com.medcorp.nevo.validic.request;

import android.util.Log;

import com.medcorp.nevo.validic.model.ValidicUser;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by gaillysu on 16/3/8.
 */
public class CreateUserRequest extends BaseSpringRequest<ValidicUser> {

    private String nevouser_id;
    private String   access_token;
    private String   organization_id;
    private String   pincode;

    public CreateUserRequest(String nevouser_id,String organization_id,String access_token,String pincode) {
        super(ValidicUser.class);
        this.nevouser_id = nevouser_id;
        this.organization_id = organization_id;
        this.access_token = access_token;
        this.pincode = pincode;
    }

    @Override
    public ValidicUser loadDataFromNetwork() throws Exception {
        ValidicUser response = getRestTemplate().postForObject(buildRequestURL(), buildRequestBody(), ValidicUser.class);
        return response;
    }

    @Override
    public String buildRequestURL() {
        String url = String.format("https://api.validic.com/v1/organizations/%s/authorization/new_user",organization_id);
        Log.i(this.getClass().getSimpleName(),"buildRequestURL return:"+url);
        return url;
    }

    @Override
    public String buildRequestBody() {
        JSONObject json = new JSONObject();
        try {

            json.put("user",new JSONObject().put("uid",nevouser_id));
            json.put("access_token",access_token);
            json.put("pin",pincode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i(this.getClass().getSimpleName(),"buildRequestBody return:"+json.toString());
        return json.toString();
    }
}
