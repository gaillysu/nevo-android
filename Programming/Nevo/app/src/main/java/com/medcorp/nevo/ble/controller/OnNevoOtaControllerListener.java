package com.medcorp.nevo.ble.controller;

import com.medcorp.nevo.ble.model.packet.NevoPacket;
import com.medcorp.nevo.ble.util.Constants.DfuFirmwareTypes;
/**
 * Created by gaillysu on 15/4/1.
 */
public interface OnNevoOtaControllerListener {
    /**
     Called when a packet is received from the device
     */
    void packetReceived(NevoPacket packet);
    void connectionStateChanged(boolean isConnected);
    void onDFUStarted();
    void onDFUCancelled();
    void onTransferPercentage(int percent);
    void onSuccessfulFileTranferred();
    void onError(OtaController.ERRORCODE errorcode);
    /**
     Call when finished OTA, will reconnect nevo and read firmware, refresh the firmware  to screen view
     @parameter whichfirmware, firmware type
     @parameter version, return the version
     */
    void firmwareVersionReceived(DfuFirmwareTypes whichfirmware, String version);
}
