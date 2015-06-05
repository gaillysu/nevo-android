package com.nevowatch.nevo.ble.controller;

import android.content.Context;
import android.util.Log;
import java.io.File;
import com.nevowatch.nevo.ble.model.packet.NevoRawData;
import com.nevowatch.nevo.ble.model.packet.SensorData;
import com.nevowatch.nevo.ble.util.Constants;
import com.nevowatch.nevo.ble.util.Constants.enumPacketOption;
import com.nevowatch.nevo.ble.util.Constants.DFUControllerState;
import com.nevowatch.nevo.ble.util.Constants.DfuFirmwareTypes;
import com.nevowatch.nevo.ble.util.Constants.DFUResponse;
import com.nevowatch.nevo.ble.util.Optional;
import com.nevowatch.nevo.ble.util.IntelHex2BinConverter;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Timer;

import static com.nevowatch.nevo.ble.util.Constants.enumPacketOption.*;

public class OtaControllerImpl implements OtaController,ConnectionController.Delegate {
    private final static String TAG = "OtaControllerImpl";

    Context mContext;

    private Optional<OnNevoOtaControllerListener> mOnOtaControllerListener = new Optional<OnNevoOtaControllerListener>();
    private ConnectionController mConnectionController;

    private DfuFirmwareTypes dfuFirmwareType = DfuFirmwareTypes.APPLICATION ;
    private ArrayList<NevoRawData> mPacketsbuffer = new ArrayList<NevoRawData>();

    private int uploadTimeInSeconds = 0;
    private String firmwareFile ;
    private DFUResponse dfuResponse = new DFUResponse((byte)0, (byte)0, (byte)0);

    private int hexFileSize  = 0;
    private StringBuffer hexFileData = new StringBuffer();
    private int binFileSize  = 0;
    private byte[] binFileData = new byte[0];
    private int numberOfPackets = 0;
    private int bytesInLastPacket = 0;
    private int writingPacketNumber = 0;

    /** check the OTA is doing or stop */
    private Timer mTimeoutTimer;
    private static final int MAX_TIME = 20000;
    private double lastprogress = 0.0;
    //added for MCU OTA

    /**
     MCU page struct: total 5 packets, as below:

     app --> BLE
     0071................... header
     0171................... 18 bytes from firmware
     0271................... 18 bytes from firmware
     0371................... 18 bytes from firmware
     FF71...........00000000 10 bytes from firmware

     BLE --> app
     0071
     FF71
     */
    private static final int DFUCONTROLLER_MAX_PACKET_SIZE = 18;
    private static final int DFUCONTROLLER_PAGE_SIZE = 64;
    //one page has 5 packets
    private static final int notificationPacketInterval = 5;
    private DFUControllerState state = DFUControllerState.INIT;
    private int firmwareDataBytesSent = 0;
    private double progress = 0.0;
    private int curpage = 0;
    private int totalpage = 0;
    private int checksum = 0;
    //end added


    /*package*/OtaControllerImpl(Context context)
    {
        mContext = context;

        mConnectionController = ConnectionController.Singleton.getInstance(context);

        mConnectionController.setDelegate(this);

        mConnectionController.connect();

    }
    /*package*/void setContext(Context context) {
        if(context!=null)
            mContext = context;
    }

    //below function is defined for BLE OTA,
    //start package function
    void openFile(String filename){

        InputStream is;
        String filetype;
        try {
            is = mContext.getAssets().open(filename);
            filetype = filename.substring(filename.length() - 3);

            Log.i(TAG,"selected file "+ filename+",extension is " + filetype);

            if (filetype.equals("hex"))
            {
                byte[] buffer = new byte[16];
                hexFileData = new StringBuffer();
                while( -1 != is.read(buffer))
                {
                    hexFileData.append(buffer);
                }
                hexFileSize = hexFileData.length();
                if (hexFileSize > 0) {
                    convertHexFileToBin();
                }
                else {
                    Log.w(TAG,"Error: file is empty!");
                    String errorMessage = "Error on openning file\n Message: file is empty or not exist";
                    if(mOnOtaControllerListener.notEmpty()) mOnOtaControllerListener.get().onError(errorMessage);
                }
            }
            else
            {
                MCU_openfirmware(filename);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    void convertHexFileToBin()
    {
        binFileData = IntelHex2BinConverter.convert(hexFileData.toString().getBytes());
        binFileSize = binFileData.length;
        Log.i(TAG,"HexFileSize: "+ hexFileSize  + " and BinFileSize: "+binFileSize);

        numberOfPackets = binFileSize / PACKET_SIZE.rawValue();

        bytesInLastPacket = binFileSize % PACKET_SIZE.rawValue();

        if (bytesInLastPacket == 0) {
            bytesInLastPacket = PACKET_SIZE.rawValue();
        }
        else
        {
            numberOfPackets = numberOfPackets + 1;
        }
        Log.i(TAG,"Number of Packets "+ numberOfPackets + " Bytes in last Packet " + bytesInLastPacket);
        writingPacketNumber = 0;
        dfuFirmwareType = DfuFirmwareTypes.APPLICATION;

    }
    void writeNextPacket()
    {

    }
    void startSendingFile()
    {

    }
    void resetSystem()
    {

    }
    void validateFirmware()
    {

    }
    void activateAndReset()
    {

    }
    String responseErrorMessage(Constants.DfuOperationStatus errorCode)
    {
        return "";
    }
    void processRequestedCode()
    {

    }
    void processStartDFUResponseStatus()
    {

    }
    void processReceiveFirmwareResponseStatus()
    {

    }
    void processValidateFirmwareResponseStatus()
    {

    }
    void processPacketNotification()
    {

    }
    void setDFUResponseStruct(byte[] data)
    {

    }
    void processDFUResponse(byte[] data)
    {

    }
    void performOldDFUOnFile()
    {

    }
    //end package function

    //start public function
    /**
     * start OTA
     * @param filename
     * @param firmwareType
     */
    public void performDFUOnFile(String filename , DfuFirmwareTypes firmwareType)
    {

    }

    /**
     * cancel OTA
     */
    public void cancelDFU()
    {

    }
    /**
     * get in charge of ConnectionController
     */
    public void setConnectControllerDelegate2Self()
    {
        mConnectionController.setDelegate(this);
    }
    /**
     * set hight level listener, it should be a activity (OTA controller view:Activity or one fragment)
     */
    public void setOnNevoOtaControllerListener(OnNevoOtaControllerListener listener)
    {
        mOnOtaControllerListener.set(listener);
    }

    @Override
    public Boolean isConnected() {
        return mConnectionController.isConnected();
    }

    @Override
    public void reset(boolean switch2SyncController) {

    }

    @Override
    public String getFirmwareVersion() {
        return mConnectionController.getFirmwareVersion();
    }

    @Override
    public String getSoftwareVersion() {
        return mConnectionController.getSoftwareVersion();
    }
    //end public function

    //start ConnectionController.Delegate interface
    @Override
    public void onConnectionStateChanged(boolean connected, String address) {

    }

    @Override
    public void onDataReceived(SensorData data) {

    }

    @Override
    public void onException(Exception e) {

    }

    @Override
    public void firmwareVersionReceived(DfuFirmwareTypes whichfirmware, String version) {

    }
    //end ConnectionController.Delegate interface

    //below function used for MCU OTA, package function
    void MCU_openfirmware(String filename)
    {

    }
    void MCU_sendFirmwareChunk()
    {

    }
    void MCU_processDFUResponse(NevoRawData rawData)
    {

    }

    //end MCU OTA
}
