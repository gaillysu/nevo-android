package com.medcorp.nevo.event.validic;

import java.util.Date;

/**
 * Created by karl-john on 17/5/16.
 */
public class ValidicDeleteSleepRecordModelEvent {

    private int userId;
    private Date date;

    public ValidicDeleteSleepRecordModelEvent(int userId, Date date) {
        this.userId = userId;
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
