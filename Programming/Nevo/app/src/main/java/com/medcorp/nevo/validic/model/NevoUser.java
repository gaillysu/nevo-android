package com.medcorp.nevo.validic.model;

import java.io.Serializable;

/**
 * Created by gaillysu on 16/3/8.
 */
public class NevoUser implements Serializable {
    private String uid;
    private Profile profile;

    public NevoUser(String uid,Profile profile)
    {
        this.uid = uid;
        this.profile = profile;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }
}
