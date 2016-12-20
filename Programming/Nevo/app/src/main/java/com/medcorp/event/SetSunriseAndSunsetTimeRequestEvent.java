package com.medcorp.event;

/**
 * Created by med on 16/12/20.
 */

public class SetSunriseAndSunsetTimeRequestEvent {

    public enum SET_EVENT{
        START,
        SUCCESS
    }
    public final SET_EVENT status;

    public SetSunriseAndSunsetTimeRequestEvent(SET_EVENT status) {
        this.status = status;
    }

    public SET_EVENT getStatus() {
        return status;
    }
}
