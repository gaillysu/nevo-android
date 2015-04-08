/*
 * COPYRIGHT (C) 2014 MED Enterprises LTD. All Rights Reserved.
 */
package com.nevowatch.nevo.ble.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.nevowatch.nevo.ble.ble.GattAttributes;


public class Utils {

	public static int getValueForOctet(String octet, int startIndex,
			int length, int powerValue) {
		int value = 0;

		if ((startIndex + length - 1) < octet.length()) {
			String bit = octet.substring(startIndex, startIndex + length);

			int iBit = 0;

			iBit = Integer.parseInt(bit, 16);

			value = (int) (iBit * Math.pow(16, powerValue));
		} else
			value = 0;
		return value;
	}

	public static long getNow() {
		return Calendar.getInstance().getTimeInMillis();
	}

	public static String formatTimeInterval(double timeInterval) {
		String formattedString = "";
		String sign = "";

		if (timeInterval >= 0)
			sign = "";
		else
			sign = "-";

		timeInterval = Math.abs(timeInterval);

		formattedString = String.format(
				"%s%s%02d: %02d",
				sign,
				((int) (timeInterval / 3600) > 0 ? String.format("%02d: ",
						(int) (timeInterval / 3600)) : "00: "),
				((int) timeInterval % 3600) / 60, (int) timeInterval % 60);

		return formattedString;
	}
	
	public static String formatTimeIntervalHHM(double timeInterval)
	{
		String formattedString = formatTimeInterval(timeInterval);
		
		if (formattedString.substring(0, 3).equals("00:"))
			formattedString = formattedString.substring(3, formattedString.length() );
		else
		{
			if (formattedString.substring(0, 1).equals("0"))
				formattedString = formattedString.substring(1, formattedString.length());
		}
		
		return formattedString;
	}




	public static String formatCaloriesUnit() {
		return "kcal";
	}

	public static String formatHeart(double heartRate, boolean isSymbol) {
		String formattedString;

		if (isSymbol)
			formattedString = String.format("%d %s", (int) heartRate,
					formatHeartUnit());
		else
			formattedString = String.format("%d", (int) heartRate);

		return formattedString;
	}

	public static String formatHeartUnit() {
		return "bpm";
	}

	public static String formatPower(double powerRate, boolean isSymbol) {
		String formattedString;

		if (isSymbol)
			formattedString = String.format("%d %s", (int) powerRate, "watt");
		else
			formattedString = String.format("%d", (int) powerRate);

		return formattedString;
	}

	public static String formatPowerUnit() {
		return "watt";
	}

	public static String formatCadence(double cadenceRate, boolean isSymbol) {
		String formattedString;

		if (isSymbol)
			formattedString = String.format("%d %s", (int) cadenceRate, "rpm");
		else
			formattedString = String.format("%d", (int) cadenceRate);

		return formattedString;
	}

	public static String formatCadenceUnit() {
		return "rpm";
	}


	public static String formatPaceForSpeech(double time) {
		String returnedString = "";
		
		int iHour;
		int iMinutes;
		int iSeconds;
		double dRest;
		
		iHour = (int) Math.floor(time / 60 / 60);
		dRest = time - (iHour * 60 * 60);
		iMinutes = (int) Math.floor(dRest / 60);
		dRest = dRest - (iMinutes * 60);
		iSeconds = (int) Math.floor(dRest);
		
		if (iHour > 0)
		{
			if (iHour != 1)
				returnedString = String.format("%d %s", iHour, "Hours");
			else
				returnedString = String.format("%d %s", iHour, "Hour");
		}
		
		if (iMinutes > 0)
		{
			if (iHour > 0)
				returnedString = String.format("%s ", returnedString);
			
			if (iMinutes != 1)
				returnedString = String.format("%s%d %s", returnedString, iMinutes, "Minutes");
			else
				returnedString = String.format("%s%d %s", returnedString, iMinutes, "Minute");
		}
		
		if (iSeconds > 0)
		{
			if (iMinutes > 0 || iHour > 0)
				returnedString = String.format("%s ", returnedString);
			
			if (iSeconds != 1)
				returnedString = String.format("%s%d %s", returnedString, iSeconds, "Seconds");
			else
				returnedString = String.format("%s%d %s", returnedString, iSeconds, "Second");
		}
		
		return returnedString;
	}

	
	public static String formatDate(long timeInterval)
	{
		SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy");
		
		return format.format(timeInterval);
	}
	
	public static String formatMonthDate(long timeInterval)
	{
		SimpleDateFormat format = new SimpleDateFormat("MMM yyyy");
		
		return format.format(timeInterval);
	}
	
	public static String formatDateD(long timeInterval)
	{
		SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy");
		
		return format.format(timeInterval);
	}
	public static String formatDateT(long timeInterval)
	{
		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
		
		return format.format(timeInterval);
	}
	
	public static long getStartTimeOfMonth(long timeInterval)
	{
		long time = 0;
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timeInterval);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		
		time = calendar.getTimeInMillis();
		
		return time;
	}
	
	public static long getEndTimeOfMonth(long timeInterval)
	{
		long time = 0;
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timeInterval);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		calendar.set(Calendar.HOUR, 23);
		calendar.set(Calendar.MINUTE, 59);
		
		time = calendar.getTimeInMillis();
		
		return time;
	}

	public static boolean isAvaialbleBLEService(String uuid) {
		if (uuid.equals(GattAttributes.HEART_RATE_CHARACTERISTIC)
				|| uuid.equals(GattAttributes.CYCLEPOWER_MEASUREMENT)
				|| uuid.equals(GattAttributes.BSC_RATE_UDID))
			return true;
		return false;
	}
	
	public static byte[] DecToBCDArray(int num) {
		
		if(num==0) return new byte[]{0x00};
		
		int digits = 0;
 
		long temp = num;
		while (temp != 0) {
			digits++;
			temp /= 10;
		}
 
		int byteLen = digits % 2 == 0 ? digits / 2 : (digits + 1) / 2;
		boolean isOdd = digits % 2 != 0;
 
		byte bcd[] = new byte[byteLen];
 
		for (int i = 0; i < digits; i++) {
			byte tmp = (byte) (num % 10);
 
			if (i == digits - 1 && isOdd)
				bcd[i / 2] = tmp;
			else if (i % 2 == 0)
				bcd[i / 2] = tmp;
			else {
				byte foo = (byte) (tmp << 4);
				bcd[i / 2] |= foo;
			}
 
			num /= 10;
		}
 
		for (int i = 0; i < byteLen / 2; i++) {
			byte tmp = bcd[i];
			bcd[i] = bcd[byteLen - i - 1];
			bcd[byteLen - i - 1] = tmp;
		}
 
		return bcd;
	}

    //Little endian bytes !
    public static int bytesToInt( byte[] bytes ) {
        int result = 0;
        for (int i=0; i<bytes.length; i++) {
            result =  (int) (result + ( (int)( bytes[i] & 0xFF) ) * Math.pow(2, 8*i));
        }
        return result;
    }
	
}
