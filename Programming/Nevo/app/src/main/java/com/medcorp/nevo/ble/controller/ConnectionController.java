package com.medcorp.nevo.ble.controller;

import android.content.Context;

import com.medcorp.nevo.ble.listener.OnConnectListener;
import com.medcorp.nevo.ble.listener.OnDataReceivedListener;
import com.medcorp.nevo.ble.listener.OnExceptionListener;
import com.medcorp.nevo.ble.listener.OnFirmwareVersionListener;
import com.medcorp.nevo.ble.model.request.SensorRequest;

/**
 * /!\/!\/!\Backbone Class : Modify with care/!\/!\/!\
 */
public interface ConnectionController {

    public  class Singleton {
        private static ConnectionControllerImpl sInstance = null;
        public static ConnectionController getInstance(Context context) {
            if(null == sInstance )
            {
                sInstance = new ConnectionControllerImpl(context);
            } else {
                sInstance.setContext(context);
            }
            return sInstance;
        }
        public static void destroy() {
            if(null != sInstance )
            {
                sInstance.destroy();
                sInstance = null;
            }
        }
    }

        /**
     Tries to connect to a Nevo
     Myabe it will scan for nearby nevo, maybe it will simply connect to a known nevo
     */
    public void connect();

    /**
     * used for OTA reconnect
     * @param
     */
    public void reconnect();


    /**
     Checks if there's a device currently connected
     */
    public boolean isConnected();

    /**
     Forgets the currently saved address.
     Next time connect is called, we will have to scan for nearby devices
     */
    public void forgetSavedAddress();

    /**
     Tries to send a request, you can't be sure that it will effectively be sent
     */
    public void sendRequest(SensorRequest request);

    /**
     get Nevo 's ble firmware version
     */
    public String getFirmwareVersion();

    /**
     get Nevo 's MCU software version
     */
    public String getSoftwareVersion();

    /**
     go to the OTA mode. In this mode, it searchs the Nevo that can enable OTA (DFU service opened)
     It won't connect to other Nevo and will stop sending regular nevo querries
     add second parameter, when BLE ota, auto disconnect by BLE peer, so no need disconnect it again
     */
    public void setOTAMode(boolean otaMode,boolean disConnect);

    /**
     Checks whether the connection controller is in OTA mode
     While in OTA mode, the ConnectionController will stop responding to normal commands
     */
    public boolean getOTAMode();


    /**
     restore the saved address. BLE OTA use it
     Usage:forgetSavedAddress()/restoreSavedAddress(), if not call forgetSavedAddress()
     before call it, do nothing
     */
    public void restoreSavedAddress();

    /**
     Checks if there is a preffered device.
     If the answer is yes, then we will systematically connect to this device.
     If it is no, then we will scan for a new device
     */
    public boolean hasSavedAddress();

    /**
     * when Nevo got disconnect, sometimes need right now connect
     * such as:
     * 1:screenOn (user press power/home key)
     * 2:nevo got paired by system setting (user press nevo A key)
     * 3:received Notification ---now has called connect(), means connect it once
     * newScan() will invoked when above case happen
     */
    public void newScan();

    /**
     * when nevo is unpaired with phone, send command can't get response packets
     * such as: user firstly run app, or finished OTA, the two case need pair nevo before connect it
     * otherwise, can't get response packets, such as get step count...
     */
    public void pairDevice();
    public void unPairDevice();

    public void setOnExceptionListener(OnExceptionListener listener);
    public void setOnDataReceivedListener(OnDataReceivedListener listener);
    public void setOnConnectListener(OnConnectListener listener);
    public void setOnFirmwareVersionListener(OnFirmwareVersionListener listener);

}
