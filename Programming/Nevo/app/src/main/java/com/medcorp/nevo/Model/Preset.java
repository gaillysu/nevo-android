package com.medcorp.nevo.model;

/**
 * Created by Karl on 11/25/15.
 */
public class Preset {

    private final int ID;
    private String label;
    private boolean status;
    private int steps;

    public Preset(int ID, String label, boolean status, int steps) {
        this.ID = ID;
        this.label = label;
        this.status = status;
        this.steps = steps;
    }

    public int getID() {
        return ID;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }
}
