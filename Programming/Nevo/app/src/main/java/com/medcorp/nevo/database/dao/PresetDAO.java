package com.medcorp.nevo.database.dao;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by Karl on 11/25/15.
 */
public class PresetDAO {

    public static final String iDString = "ID";
    @DatabaseField(generatedId = true)
    private int ID;

    public static final String labelString = "label";
    @DatabaseField
    private String Label;

    public static final String enabledString = "enabled";
    @DatabaseField
    private boolean Enabled;

    public static final String stepsString = "steps";
    @DatabaseField
    private int Steps;

    public static String getiDString() {
        return iDString;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public static String getLabelString() {
        return labelString;
    }

    public static String getEnabledString() {
        return enabledString;
    }

    public boolean isEnabled() {
        return Enabled;
    }

    public void setEnabled(boolean enabled) {
        Enabled = enabled;
    }

    public String getLabel() {
        return Label;
    }

    public void setLabel(String label) {
        Label = label;
    }

    public static String getStepsString() {
        return stepsString;
    }

    public int getSteps() {
        return Steps;
    }

    public void setSteps(int steps) {
        Steps = steps;
    }
}
