package com.medcorp.model;

public interface GoalBase {
	
	public enum GoalIntensity {
		LOW,MEDIUM,HIGH
	}
	
	public String getType();
	public GoalIntensity getGoalIntensity();
	public int getValue();
}
