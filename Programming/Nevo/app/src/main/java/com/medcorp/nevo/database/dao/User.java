package com.medcorp.nevo.database.dao;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by gaillysu on 15/11/17.
 */
public class User {
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

}
