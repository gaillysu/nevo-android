package com.nevowatch.nevo.ble.controller;

import com.nevowatch.nevo.ble.util.Constants.DfuFirmwareTypes;
/**
 * Created by gaillysu on 15/4/1.
 */
public interface OnNevoOtaControllerListener {
    void connectionStateChanged(boolean isConnected);
    void onDFUStarted();
    void onDFUCancelled();
    void onTransferPercentage(int percent);
    void onSuccessfulFileTranferred();
    void onError(String error);
    /**
     Call when finished OTA, will reconnect nevo and read firmware, refresh the firmware  to screen view
     @parameter whichfirmware, firmware type
     @parameter version, return the version
     */
    void firmwareVersionReceived(DfuFirmwareTypes whichfirmware, String version);
}
