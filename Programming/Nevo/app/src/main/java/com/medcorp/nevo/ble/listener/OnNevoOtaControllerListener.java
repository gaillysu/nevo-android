package com.medcorp.nevo.ble.listener;

import com.medcorp.nevo.ble.controller.OtaController;
import com.medcorp.nevo.ble.model.packet.NevoPacket;
import com.medcorp.nevo.ble.util.Constants.DfuFirmwareTypes;
/**
 * Created by gaillysu on 15/4/1.
 */
public interface OnNevoOtaControllerListener {
    /**
     Called when a packet is received from the device
     */
    public void onPrepareOTA(DfuFirmwareTypes which);
    public void packetReceived(NevoPacket packet);
    public void connectionStateChanged(boolean isConnected);
    public void onDFUStarted();
    public void onDFUCancelled();
    public void onTransferPercentage(int percent);
    public void onSuccessfulFileTranferred();
    public void onError(OtaController.ERRORCODE errorcode);
    /**
     Call when finished OTA, will reconnect nevo and read firmware, refresh the firmware  to screen view
     @parameter whichfirmware, firmware type
     @parameter version, return the version
     */
    public void firmwareVersionReceived(DfuFirmwareTypes whichfirmware, String version);
}
