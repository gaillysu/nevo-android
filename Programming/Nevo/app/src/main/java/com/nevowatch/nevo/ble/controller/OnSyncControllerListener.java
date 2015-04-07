package com.nevowatch.nevo.ble.controller;

import com.nevowatch.nevo.ble.model.packet.NevoPacket;

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
}
