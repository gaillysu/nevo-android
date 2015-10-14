package com.medcorp.nevo.model;

public interface Goal {
	
	public enum GoalIntensity {
		LOW,MEDIUM,HIGH
	}
	
	public String getType();
	public GoalIntensity getGoalIntensity();
	public int getValue();
}
