package com.nevowatch.nevo.ble.controller;

import android.content.Context;

import java.io.InputStream;

import com.nevowatch.nevo.ble.util.Constants.*;
/**
 * this class define some functions for firmware upgrade.
 * @author Gaillysu
 *
 */
public interface OtaController {

    /**
     * define OtaControllerImpl Singleton, for supporting background run mode
     */
    public  class Singleton {
        private static  OtaControllerImpl sInstance = null;
        public static OtaController getInstance(Context context) {
            if(null == sInstance )
            {
                sInstance = new OtaControllerImpl(context);
            } else {
                sInstance.setContext(context);
            }
            return sInstance;
        }
    }

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


    public enum ERRORCODE {
        NOCONNECTION,
        TIMEOUT,
        STARTDFUERROR,
        OPENFILEERROR,
        INVALIDRESPONSE,
        NOSUPPORTOLDDFU,
        EXCEPTION,
        CHECKSUMERROR
    }
}
