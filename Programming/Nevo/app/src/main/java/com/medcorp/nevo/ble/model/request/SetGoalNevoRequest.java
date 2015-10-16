package com.medcorp.nevo.ble.model.request;


import android.content.Context;

import com.medcorp.nevo.model.Goal;

public class SetGoalNevoRequest extends NevoRequest {
	public  final static  byte HEADER = 0x22;
    private Goal mGoal = new NumberOfStepsGoal(NumberOfStepsGoal.LOW);
	public SetGoalNevoRequest(Context context, Goal goal )
	{
		super(context);
		mGoal = goal;
	}

	@Override
	public byte[] getRawData() {		
		return null;
	}

	@Override
	public byte[][] getRawDataEx() {
		
		 byte level = (byte)(mGoal.getGoalIntensity().ordinal());
		 byte display = 0;  //default is step goal showing
		 int goal_dist = 10000; //unit ??cm
				  
		 int goal_steps = mGoal.getType().equals(NumberOfStepsGoal.TYPE) ? mGoal.getValue() : 0;
				        
		 int goal_carlories = 2000; // unit ??
		 int goal_time = 100; //unit ??
		 int goal_stroke = 3000; // unit ???
				    
		return new byte[][] {
				   {0,HEADER,level,display,
					(byte) (goal_dist&0xFF),
					(byte) ((goal_dist>>8)&0xFF),
					(byte) ((goal_dist>>16)&0xFF),
					(byte) ((goal_dist>>24)&0xFF),
					(byte) (goal_steps&0xFF),
					(byte) ((goal_steps>>8)&0xFF),
					(byte) ((goal_steps>>16)&0xFF),
					(byte) ((goal_steps>>24)&0xFF),
					(byte) (goal_carlories&0xFF),
					(byte) ((goal_carlories>>8)&0xFF),
					(byte) ((goal_carlories>>16)&0xFF),
					(byte) ((goal_carlories>>24)&0xFF),
					(byte) (goal_time&0xFF),
					(byte) ((goal_time>>8)&0xFF),
					(byte) ((goal_time>>16)&0xFF),
					(byte) ((goal_time>>24)&0xFF)
					},
				
				{(byte) 0xFF,HEADER,
						(byte) (goal_stroke&0xFF),
						(byte) ((goal_stroke>>8)&0xFF),
						(byte) ((goal_stroke>>16)&0xFF),
						(byte) ((goal_stroke>>24)&0xFF),
						0,0,0,0,0,0,0,0,0,0,0,0,0,0
				}					
		      };
	}

	@Override
	public byte getHeader() {
		
		return HEADER;
	}

}
