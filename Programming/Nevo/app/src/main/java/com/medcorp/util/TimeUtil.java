package com.medcorp.util;

/**
 * Created by karl-john on 18/8/2016.
 */

public class TimeUtil {
    public static String formatTime(int walkDuration) {
        StringBuilder activityTime = new StringBuilder();
        if (walkDuration >= 60) {
            if (walkDuration % 60 > 0) {
                activityTime.append((walkDuration /60)).append("h");
                activityTime.append(walkDuration % 60).append("m");
            }
        } else {
            activityTime.append(walkDuration).append("m");
        }
        return activityTime.toString();
    }
}
