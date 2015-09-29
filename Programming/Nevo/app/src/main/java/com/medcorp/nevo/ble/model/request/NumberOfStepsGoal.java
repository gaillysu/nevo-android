package com.medcorp.nevo.ble.model.request;

import com.medcorp.nevo.Model.Goal;

public class NumberOfStepsGoal implements Goal {
	
	public static final int LOW = 7000;
	public static final int MEDIUM = 10000;
	public static final int HIGH = 20000;
	public static final String TYPE = "NumberOfStepsGoal";
	private int mSteps = LOW;
	
	public NumberOfStepsGoal(int steps)
	{
		mSteps = steps;
	}
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public GoalIntensity getGoalIntensity() {
	   if(mSteps<=LOW) return GoalIntensity.LOW;
	   if(mSteps<=MEDIUM) return GoalIntensity.MEDIUM;
		return GoalIntensity.HIGH;
	}

	@Override
	public int getValue() {
		return mSteps;
	}

}