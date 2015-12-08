package com.medcorp.nevo.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by gaillysu on 15/12/8.
 */
public class Common {

    static public Date getDateFromDate(Date date)
    {
        Calendar calBeginning = new GregorianCalendar();
        calBeginning.setTime(date);
        calBeginning.set(Calendar.HOUR_OF_DAY, 0);
        calBeginning.set(Calendar.MINUTE, 0);
        calBeginning.set(Calendar.SECOND, 0);
        calBeginning.set(Calendar.MILLISECOND, 0);
        Date today = calBeginning.getTime();
        return today;
    }
}
