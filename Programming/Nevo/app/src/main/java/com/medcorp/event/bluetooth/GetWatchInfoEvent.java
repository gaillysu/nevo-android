package com.medcorp.event.bluetooth;

import com.medcorp.model.WatchInfomation;

/**
 * Created by med on 16/11/8.
 */

public class GetWatchInfoEvent {
 final WatchInfomation watchInfomation;

    public GetWatchInfoEvent(WatchInfomation watchInfomation) {
        this.watchInfomation = watchInfomation;
    }

    public WatchInfomation getWatchInfomation() {
        return watchInfomation;
    }
}
