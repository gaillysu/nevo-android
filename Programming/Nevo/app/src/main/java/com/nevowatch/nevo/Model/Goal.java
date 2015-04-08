package com.nevowatch.nevo.Model;

public interface Goal {
	
	public enum GoalIntensity {
		LOW,MEDIUM,HIGH
	}
	
	String getType();
	GoalIntensity getGoalIntensity();
	int getValue();
}
