package com.medcorp.nevo.database.dao;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by gaillysu on 15/11/17.
 */
public class UserDAO {
    /**
     * field name and initialize value, Primary field
     */
    public static final String fID = "ID";
    @DatabaseField(generatedId = true)
    private int ID = 1;

    /**
     * milliseconds since January 1, 1970, 00:00:00 GMT
     * for sample: Nov 17,2000,18:50:20
     */
    public static final String fBirthday = "Birthday";
    @DatabaseField
    private long Birthday;

    /**
     * default age
     */
    public static final String fAge = "Age";
    @DatabaseField
    private int Age = 35;

    /**
     * default weight, 75kg
     */
    public static final String fWeight = "Weight";
    @DatabaseField
    private int Weight = 75;

    /**
     * default height, 175cm
     */
    public static final String fHeight = "Height";
    @DatabaseField
    private int Height = 175;


    /**
     * created date
     */
    public static final String fCreatedDate = "CreatedDate";
    @DatabaseField
    private long CreatedDate;

    /**
     * remarks field, save extend user infomation, such as: blood type, email,step length...
     * it is a Json string
     */

    public static final String fRemarks = "Remarks";
    @DatabaseField
    private String Remarks;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public long getBirthday() {
        return Birthday;
    }

    public void setBirthday(long birthday) {
        Birthday = birthday;
    }

    public int getAge() {
        return Age;
    }

    public void setAge(int age) {
        Age = age;
    }

    public int getWeight() {
        return Weight;
    }

    public void setWeight(int weight) {
        Weight = weight;
    }

    public int getHeight() {
        return Height;
    }

    public void setHeight(int height) {
        Height = height;
    }

    public long getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(long createdDate) {
        CreatedDate = createdDate;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }
}
