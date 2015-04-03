/*
 * COPYRIGHT (C) 2014 MED Enterprises LTD. All Rights Reserved.
 */
package com.nevowatch.nevo.ble.util;



public class Constants {

	public static String BLE_DEVICE_NAME_POWER = "PowerCal";
	public static String BLE_DEVICE_NAME_BSC = "BSC";
	public static String BLE_DEVICE_NAME_HRM = "HRMBLE";
	
	public static final String kFlickrKey = "dcae6273b01e1ec003fd7ed56901ea14";
	public static final String kAgentString = "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_5_6; en-us) AppleWebKit/525.27.1 (KHTML, like Gecko) Version/3.2.1 Safari/525.27.1";
	
	public static final int kDefaultAge = 35;
	public static final int kDefaultHeight = 175;
	public static final int kDefaultWeight = 75;
	
	public static final boolean kDefaultIsMan = true;
	public static final boolean kDefaultIsMetric = true;
	
	public static final String kPowerZoneLink = "http://www.imazecorp.com/power-zones/";
	public static final String kHeartZoneLink = "http://www.imazecorp.com/fitness-hr-strap-heart-rate-zones/";
	
	public static final int kMinPowerMaxPicker = 100;
	public static final int kMaxPowerMaxPicker = 3000;
	
	public static final int kMinHRMaxPicker = 80;
	public static final int kMaxHRManMaxPicker = 220;
	public static final int kMaxHRWomanMaxPicker = 227;
	
	
	public static final String powerZone1 = "Recovery : < 50%";
	public static final String powerZone2 = "Endurance : 50-75%";
	public static final String powerZone3 = "Tempo : 75-90%";
	public static final String powerZone4 = "Lactic Threshold : 90-105%";
	public static final String powerZone5 = "Anaerobic : > 105%";
	
	public static final String hrZone1 = "Health : 50-60%";
	public static final String hrZone2 = "Fitness : 60-70%";
	public static final String hrZone3 = "Aerobic : 70-80%";
	public static final String hrZone4 = "Anaerobic : 80-90%";
	public static final String hrZone5 = "Maximum : 90-100%";
	
	public static final int kTrType_free = 1;
	public static final int kTrType_time = 2;
	public static final int kTrType_distance = 3;
	public static final int kTrType_calories = 4;
	public static final int kTrType_ghost = 5;
	public static final int kTrType_challenge = 6;
	
	public static final int kAcType_running = 1;
	public static final int kAcType_biking = 2;
	public static final int kAcType_boxing = 3;
	public static final int kAcType_cardio = 4;
	public static final int kAcType_dancing = 5;
	public static final int kAcType_elipticalbike = 6;
	public static final int kAcType_indoorbike = 7;
	public static final int kAcType_machineworkout = 8;
	public static final int kAcType_pullup = 9;
	public static final int kAcType_rowing = 10;
	public static final int kAcType_threadmills = 11;
	public static final int kAcType_weightworkout = 12;
	public static final int kAcType_yoga = 13;
	
	public static final int kHRDefaultZone = 3;
	public static final int kPowerDefaultZone = 3;

	public static final int kMinWeightPicker = 30;
	public static final int kMaxWeightPicker = 150;
	
	public static final int kMinHeightPicker = 100;
	public static final int kMaxHeightPicker = 240;
	
	public static final int kMinAgePicker = 10;
	public static final int kMaxAgePicker = 100;
	
	public static final int kPowerAlarmMax = 4000;
	public static final int kHRAlarmMax = 4000;
	
	public static final int kDistancePanel = 1;
	public static final int kSpeedPanel = 2;
	public static final int kCaloriePanel = 3;
	public static final int kHeartPanel = 4;
	public static final int kPowerPanel = 5;
	public static final int kCadencePanel = 6;
	public static final int kElevationPanel = 7;
	
	public static final int kStartTraining = 1;
	public static final int kPauseTraining = 2;
	public static final int kStopTraining = 3;
	
	public static final int kGoalDistanceMax = 100;
	public static final int kGoalTimeMax = 600;
	public static final int kGoalCalorieMax = 4000;
	
	public static final long kChronoAccuracy = 1000;
	
	public static final int kMinTimeFitnessRefresh = 2;
	
	public static final float kMilesConvert = (621.371192f/1000.0f);
	public static final float kkFeetConvert = 3.2808399f;
	
	public static final int kMinSensorInactivity = 20 * 1000;
	public static final int kMinSensorData = 8 * 1000;
	
	public static final int kMinHAccuracy = 200;
	public static final int kMinVAccuracy = 40;
	
	public static final int kMinTimeLocation = 5 * 1000;
	public static final int kMinVoiceZoneAlarm = 60 * 1000;
	
	public static final int kMinSpeed = 2;
	public static final int kMaxSpeed = 300;
	
	public static final int kAscentMinVAccuracy = 20;
	
	public static final int kSampleTypeSpeed = 1;
	public static final int kSampleTypeDistance = 2;
	public static final int kSampleTypeCalories = 3;
	public static final int kSampleTypeTime = 4;
	public static final int kSampleTypePace = 5;
	public static final int kSampleTypeHeart = 6;
	public static final int kSampleTypePower = 7;
	public static final int kSampleTypeCadence = 8;
	
	public static final int kSampleModeTime = 1;
	public static final int kSampleModeDistance = 2;
	
	public static final int kIntervalCalories = 30;
	
	public static final float kPI = 3.1415926535897932384626f;
	public static final long kRadius = 6378140;
	
	public static final float kWomanCalories = 0.85f;
	
	//public static Training selectedTraining;
	
	public static final int kGraphDistance = 1001;
	public static final int kGraphSpeed = 1002;
	public static final int kGraphCalorie = 1003;
	public static final int kGraphHeart = 1004;
	public static final int kGraphPower = 1005;
	public static final int kGraphCadence = 1006;

}
	