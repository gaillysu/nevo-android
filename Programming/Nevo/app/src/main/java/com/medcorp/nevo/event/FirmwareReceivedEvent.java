package com.medcorp.nevo.event;

import net.medcorp.library.ble.util.Constants;

/**
 * Created by karl-john on 4/3/16.
 */
public class FirmwareReceivedEvent {

    private final Constants.DfuFirmwareTypes type;
    private final String version;


    public FirmwareReceivedEvent(Constants.DfuFirmwareTypes type, String version) {
        this.type = type;
        this.version = version;
    }

    public Constants.DfuFirmwareTypes getType() {
        return type;
    }

    public String getVersion() {
        return version;
    }
}
