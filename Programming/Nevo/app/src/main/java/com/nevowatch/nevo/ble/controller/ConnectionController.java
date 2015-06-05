package com.nevowatch.nevo.ble.controller;

import android.content.Context;

import com.nevowatch.nevo.ble.controller.ConnectionControllerImpl;
import com.nevowatch.nevo.ble.kernel.NevoBT;
import com.nevowatch.nevo.ble.kernel.OnConnectListener;
import com.nevowatch.nevo.ble.kernel.OnDataReceivedListener;
import com.nevowatch.nevo.ble.kernel.OnExceptionListener;
import com.nevowatch.nevo.ble.kernel.OnFirmwareVersionListener;
import com.nevowatch.nevo.ble.model.packet.SensorData;
import com.nevowatch.nevo.ble.model.request.NevoRequest;
import com.nevowatch.nevo.ble.model.request.SensorRequest;

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
    }

    /**
     set one  delegate,  this delegate comes from syncController
     Layer struct: L1(NevoBT) -->L2 (ConnectionController,Single instance) -->L3 (syncController, single instance)
     -->L4(UI viewController), L1 is the base Layer, L4 is the top layer
     */
    public void setDelegate(ConnectionController.Delegate delegate);

    /**
     Tries to connect to a Nevo
     Myabe it will scan for nearby nevo, maybe it will simply connect to a known nevo
     */
    public void connect();

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


    interface Delegate extends OnExceptionListener, OnDataReceivedListener, OnConnectListener ,OnFirmwareVersionListener{

    }

}
