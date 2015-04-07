package com.nevowatch.nevo.ble.controller;

import com.nevowatch.nevo.ble.model.packet.NevoPacket;

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
}
