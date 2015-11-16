package com.medcorp.nevo.ble.controller;

import com.medcorp.nevo.ble.listener.OnConnectListener;
import com.medcorp.nevo.ble.listener.OnDataReceivedListener;
import com.medcorp.nevo.ble.listener.OnExceptionListener;
import com.medcorp.nevo.ble.listener.OnFirmwareVersionListener;
import com.medcorp.nevo.ble.listener.OnNevoOtaControllerListener;
import com.medcorp.nevo.ble.util.Constants.DFUControllerState;
import com.medcorp.nevo.ble.util.Constants.DfuFirmwareTypes;
/**
 * this class define some functions for firmware upgrade.
 * @author Gaillysu
 *
 */
public interface OtaController {

    /**
     * start OTA
     * @param filename
     * @param firmwareType
     */
    void performDFUOnFile(String filename , DfuFirmwareTypes firmwareType);

    /**
     * cancel OTA
     */
    void cancelDFU();

    /**
     * get in charge of ConnectionController
     */
    void setConnectControllerDelegate2Self();

    /**
     * set hight level listener, it should be a activity (OTA controller view:Activity or one fragment)
     */
    void setOnNevoOtaControllerListener(OnNevoOtaControllerListener listener);

    /**
     * read ConnectionController status
     * @return true or false
     */
    Boolean isConnected();

    /**
     * get/set state
     */
    DFUControllerState getState();

    void setState(DFUControllerState state);

    void switch2SyncController();

    /**
     * when OTA done successful or failure, reset it to normal mode
     * switch2SyncController: true / false ,which controller will be in charge of connectionController：
     * syncController or otaController
     *
     */
    void reset(boolean switch2SyncController);

    /**
     *
     * @return BLW FW version
     */
    String getFirmwareVersion();
    /**
     *
     * @return MCU FW version
     */
    String getSoftwareVersion();

    /**
     * patch for samsung S4 Ble OTA, send start ble OTA cmd 0x72, can't get disconnect after 7s
     * so here add this patch function do it
     * this patch will make a disconnect to nevo (normal OTA should be get disconnect from nevo )
     */
    void SamsungS4Patch();

    /**
     * when BLE OTA done, need unpair Nevo (forget it)
     */
    void forGetDevice();

    public enum ERRORCODE {
        NOCONNECTION,
        TIMEOUT,
        STARTDFUERROR,
        OPENFILEERROR,
        INVALIDRESPONSE,
        NOSUPPORTOLDDFU,
        EXCEPTION,
        CHECKSUMERROR,
        NODFUSERVICE,
        NOFINISHREADVERSION
    }

    public static String PREF_NAME = "nevoPrefs";
    public static String SYNCDATE = "nevoSyncdate";

    public void setOnExceptionListener(OnExceptionListener listener);
    public void setOnDataReceivedListener(OnDataReceivedListener listener);
    public void setOnConnectListener(OnConnectListener listener);
    public void setOnFirmwareVersionListener(OnFirmwareVersionListener listener);

}
