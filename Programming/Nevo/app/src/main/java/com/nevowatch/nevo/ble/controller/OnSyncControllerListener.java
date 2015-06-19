package com.nevowatch.nevo.ble.controller;

import com.nevowatch.nevo.ble.model.packet.NevoPacket;
import com.nevowatch.nevo.ble.util.Constants;

/**
 * Created by gaillysu on 15/4/1.
 */
public interface OnSyncControllerListener {

    /**
    Called when a packet is received from the device
    */
    void packetReceived(NevoPacket packet);
    /**
    Called when a peripheral connects or disconnects
    */
    void connectionStateChanged(boolean isConnected);

    /*
    called when get version info
     */
    void firmwareVersionReceived(Constants.DfuFirmwareTypes whichfirmware, String version);
}
