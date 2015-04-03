package com.nevowatch.nevo.ble.model.request;

public interface Goal {
	
	public enum GoalIntensity {
		LOW,MEDIUM,HIGH
	}
	
	String getType();
	GoalIntensity getGoalIntensity();
	int getValue();
}
