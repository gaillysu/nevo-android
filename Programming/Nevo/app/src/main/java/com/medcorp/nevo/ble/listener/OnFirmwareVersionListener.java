/*
 * COPYRIGHT (C) 2014 MED Enterprises LTD. All Rights Reserved.
 */
package com.medcorp.nevo.ble.listener;

import com.medcorp.nevo.ble.util.Constants.DfuFirmwareTypes;
/**
 * This Listener will be Called when finish reading Firmware
 * @author Gaillysu
 *
 */
public interface OnFirmwareVersionListener {

    /**
     Call when finish reading Firmware
     @parameter whichfirmware, firmware type
     @parameter version, return the version
     */
    public void  firmwareVersionReceived(DfuFirmwareTypes whichfirmware, String version);
}
