package com.medcorp.nevo.model;

/**
 * Created by Karl on 11/25/15.
 */
public class Preset {

    // TODO change the name of the class to Goal bu since goal now is occupied we can't send it. Gotta configure it later.
    private int id;
    private String label;
    private boolean status;
    private int steps;

    public Preset(String label, boolean status, int steps) {
        this.label = label;
        this.status = status;
        this.steps = steps;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
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
