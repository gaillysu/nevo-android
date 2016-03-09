package com.medcorp.nevo.validic.model;

import java.io.Serializable;

/**
 * Created by gaillysu on 16/3/8.
 */
public class Profile{
    private String gender;
    private String location;
    private String birth_year;
    private float  height;
    private float  weight;

    public Profile(String gender,String location,String birth_year,float height,float weight)
    {
        this.gender = gender;
        this.location = location;
        this.birth_year = birth_year;
        this.height = height;
        this.weight = weight;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getBirth_year() {
        return birth_year;
    }

    public void setBirth_year(String birth_year) {
        this.birth_year = birth_year;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }
}
