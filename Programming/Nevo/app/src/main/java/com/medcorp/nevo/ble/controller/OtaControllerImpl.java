package com.medcorp.nevo.ble.controller;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.medcorp.nevo.R;
import com.medcorp.nevo.activity.base.BaseActivity;
import com.medcorp.nevo.activity.old.OTAActivity;
import com.medcorp.nevo.application.ApplicationModel;
import com.medcorp.nevo.ble.exception.BLEUnstableException;
import com.medcorp.nevo.ble.exception.NevoException;
import com.medcorp.nevo.ble.listener.OnConnectListener;
import com.medcorp.nevo.ble.listener.OnDataReceivedListener;
import com.medcorp.nevo.ble.listener.OnExceptionListener;
import com.medcorp.nevo.ble.listener.OnFirmwareVersionListener;
import com.medcorp.nevo.ble.listener.OnNevoOtaControllerListener;
import com.medcorp.nevo.ble.model.packet.NevoFirmwareData;
import com.medcorp.nevo.ble.model.packet.NevoRawData;
import com.medcorp.nevo.ble.model.packet.SensorData;
import com.medcorp.nevo.ble.model.request.NevoMCU_OTAChecksumRequest;
import com.medcorp.nevo.ble.model.request.NevoMCU_OTAPacketRequest;
import com.medcorp.nevo.ble.model.request.NevoMCU_OTAPageRequest;
import com.medcorp.nevo.ble.model.request.NevoMCU_OTAStartRequest;
import com.medcorp.nevo.ble.model.request.NevoOTAControlRequest;
import com.medcorp.nevo.ble.model.request.NevoOTAPacketFileSizeRequest;
import com.medcorp.nevo.ble.model.request.NevoOTAPacketRequest;
import com.medcorp.nevo.ble.model.request.NevoOTAStartRequest;
import com.medcorp.nevo.ble.model.request.SensorRequest;
import com.medcorp.nevo.ble.util.Constants.DFUControllerState;
import com.medcorp.nevo.ble.util.Constants.DFUResponse;
import com.medcorp.nevo.ble.util.Constants.DfuFirmwareTypes;
import com.medcorp.nevo.ble.util.Constants.DfuOperationStatus;
import com.medcorp.nevo.ble.util.Constants.DfuOperations;
import com.medcorp.nevo.ble.util.Constants.enumPacketOption;
import com.medcorp.nevo.ble.util.IntelHex2BinConverter;
import com.medcorp.nevo.ble.util.Optional;
import com.medcorp.nevo.ble.util.QueuedMainThreadHandler;

import org.apache.commons.codec.binary.Hex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class OtaControllerImpl implements OtaController, OnExceptionListener, OnDataReceivedListener, OnConnectListener, OnFirmwareVersionListener {
    private final static String TAG = "OtaControllerImpl";

    private ApplicationModel mContext;

    private Optional<OnNevoOtaControllerListener> mOnOtaControllerListener = new Optional<OnNevoOtaControllerListener>();
    private ConnectionController connectionController;

    private DfuFirmwareTypes dfuFirmwareType = DfuFirmwareTypes.APPLICATION ;
    private List<NevoFirmwareData> mPacketsbuffer = new ArrayList<NevoFirmwareData>();
    private List<NevoRawData> mNevoPacketsbuffer = new ArrayList<NevoRawData>();

    private int uploadTimeInSeconds = 0;
    private String firmwareFile ;
    private DFUResponse dfuResponse = new DFUResponse((byte)0, (byte)0, (byte)0);

    private int hexFileSize  = 0;
    private byte[] hexFileData ;
    private int binFileSize  = 0;
    private byte[] binFileData = new byte[0];
    private int numberOfPackets = 0;
    private int bytesInLastPacket = 0;
    private int writingPacketNumber = 0;

    /** check the OTA is doing or stop */
    private Timer mTimeoutTimer = null;
    public static int MAX_TIME = 35000;
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
    private boolean manualmode = false;
    //end added

    public OtaControllerImpl(ApplicationModel context)
    {
        mContext = context;
        connectionController = ConnectionController.Singleton.getInstance(context);
        connectionController.connect();
    }

    public void setManualMode(boolean  manualmode)
    {
        this.manualmode = manualmode;
    }
    //below function is defined for BLE OTA,
    //start package function
    void openFile(String filename){

        InputStream is;
        String filetype;
        try {

            filetype = filename.substring(filename.length() - 3);
            Log.i(TAG,"selected file "+ filename+",extension is " + filetype);

            if (filetype.equals("hex"))
            {
                byte[] buffer = new byte[16];
                int len =-1;
                is = mContext.getAssets().open(filename);

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                while( -1 != (len = is.read(buffer)))
                {
                    bos.write(buffer,0,len);
                }
                hexFileData = bos.toByteArray();
                hexFileSize = hexFileData.length;
                if (hexFileSize > 0) {
                    convertHexFileToBin();
                }
                else {
                    Log.w(TAG,"Error: file is empty!");
                    String errorMessage = "Error on openning file\n Message: file is empty or not exist";
                    if(mOnOtaControllerListener.notEmpty()) mOnOtaControllerListener.get().onError(ERRORCODE.OPENFILEERROR);
                }
                bos.close();
                is.close();
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
        binFileData = IntelHex2BinConverter.convert(hexFileData);
        binFileSize = binFileData.length;
        Log.i(TAG,"HexFileSize: "+ hexFileSize  + " and BinFileSize: "+binFileSize);

        numberOfPackets = binFileSize / enumPacketOption.PACKET_SIZE.rawValue();

        bytesInLastPacket = binFileSize % enumPacketOption.PACKET_SIZE.rawValue();

        if (bytesInLastPacket == 0) {
            bytesInLastPacket = enumPacketOption.PACKET_SIZE.rawValue();
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
        int percentage = 0;
        for (int index = 0; index<enumPacketOption.PACKETS_NOTIFICATION_INTERVAL.rawValue(); index++)
        {
        if (writingPacketNumber > numberOfPackets-2) {
            Log.i(TAG,"writing last packet");

            byte[] nextPacketData = new byte[bytesInLastPacket];
            System.arraycopy(binFileData,writingPacketNumber*enumPacketOption.PACKET_SIZE.rawValue(),nextPacketData,0,bytesInLastPacket);

            Log.i(TAG,"writing packet number " + (writingPacketNumber+1) + " ...");
           //Log.i(TAG, new String(Hex.encodeHex(nextPacketData)));

            sendRequest(new NevoOTAPacketRequest(mContext,nextPacketData));
            progress = 100.0;
            percentage = (int)(progress);
            Log.i(TAG,"DFUOperations: onTransferPercentage " + percentage);
            if(mOnOtaControllerListener.notEmpty()) mOnOtaControllerListener.get().onTransferPercentage(percentage);
            writingPacketNumber++;
            if(mTimeoutTimer!=null) {mTimeoutTimer.cancel();mTimeoutTimer=null;}
            Log.i(TAG,"DFUOperations: onAllPacketsTransfered");
            break;

        }
        byte[] nextPacketData = new byte[enumPacketOption.PACKET_SIZE.rawValue()];
        System.arraycopy(binFileData,writingPacketNumber*enumPacketOption.PACKET_SIZE.rawValue(),nextPacketData,0,enumPacketOption.PACKET_SIZE.rawValue());

        Log.i(TAG,"writing packet number " + (writingPacketNumber+1) + " ...");
       // Log.i(TAG, new String(Hex.encodeHex(nextPacketData)));

        sendRequest(new NevoOTAPacketRequest(mContext,nextPacketData));
        progress = 100.0*writingPacketNumber * enumPacketOption.PACKET_SIZE.rawValue() / binFileSize;
        percentage = (int)progress;

        Log.i(TAG,"DFUOperations: onTransferPercentage "+ percentage);
            if(mOnOtaControllerListener.notEmpty()) mOnOtaControllerListener.get().onTransferPercentage(percentage);

        writingPacketNumber++;

    }

    }

    //OtaController and syncController Queue is used for these packets[00...FF]
    //they are the high level Queue

    //BLE OTA use the lower Queue: QueueType.NevoBT, pls see @NevoBTService.sendRequest
    //due to BLE OTA packets is not the regular packets which start with 00,FF
    private void sendRequest(final SensorRequest request)
    {
        if(dfuFirmwareType == DfuFirmwareTypes.SOFTDEVICE)
        QueuedMainThreadHandler.getInstance(QueuedMainThreadHandler.QueueType.OtaController).post(new Runnable(){
            @Override
            public void run() {
                connectionController.sendRequest(request);
            }
        });
        else
        connectionController.sendRequest(request);
    }

    void startSendingFile()
    {
        Log.i(TAG,"DFUOperationsdetails enablePacketNotification");

        sendRequest(new NevoOTAControlRequest(mContext,new byte[]{(byte)DfuOperations.PACKET_RECEIPT_NOTIFICATION_REQUEST.rawValue()
                                                                              ,(byte)enumPacketOption.PACKETS_NOTIFICATION_INTERVAL.rawValue()
                                                                              ,0}));
        sendRequest(new NevoOTAControlRequest(mContext,new byte[]{(byte)DfuOperations.RECEIVE_FIRMWARE_IMAGE_REQUEST.rawValue()}));

        //wait 20ms
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                writeNextPacket();
            }
        },20);


        if(mOnOtaControllerListener.notEmpty()) mOnOtaControllerListener.get().onDFUStarted();

    }
    void resetSystem()
    {
        Log.i(TAG,"DFUOperationsDetails resetSystem");
        sendRequest(new NevoOTAControlRequest(mContext,new byte[]{(byte)DfuOperations.RESET_SYSTEM.rawValue()}));
    }

    void validateFirmware()
    {
        Log.i(TAG,"DFUOperationsDetails validateFirmware");
        sendRequest(new NevoOTAControlRequest(mContext,new byte[]{(byte)DfuOperations.VALIDATE_FIRMWARE_REQUEST.rawValue()}));
    }
    void activateAndReset()
    {
        Log.i(TAG,"DFUOperationsDetails activateAndReset");
        sendRequest(new NevoOTAControlRequest(mContext,new byte[]{(byte)DfuOperations.ACTIVATE_AND_RESET_REQUEST.rawValue()}));
    }
    String responseErrorMessage(byte errorCode)
    {
        if (errorCode == DfuOperationStatus.OPERATION_FAILED_RESPONSE.rawValue())
                 return new String("Operation Failed");
        else if(errorCode == DfuOperationStatus.OPERATION_INVALID_RESPONSE.rawValue())
                return new String("Invalid Response");

        else if (errorCode == DfuOperationStatus.OPERATION_NOT_SUPPORTED_RESPONSE.rawValue())
                return new String("Operation Not Supported");

        else if (errorCode == DfuOperationStatus.DATA_SIZE_EXCEEDS_LIMIT_RESPONSE.rawValue())
                return new String("Data Size Exceeds");

        else if (errorCode == DfuOperationStatus.CRC_ERROR_RESPONSE.rawValue())
                return new String("CRC Error");

        return new String("unknown Error");

    }
    void processRequestedCode()
    {
        Log.i(TAG,"processsRequestedCode");

        if (dfuResponse.getrequestedCode() == DfuOperations.START_DFU_REQUEST.rawValue()){
            Log.i(TAG, "Requested code is StartDFU now processing response status");
            processStartDFUResponseStatus();
        }
        else if (dfuResponse.getrequestedCode() == DfuOperations.RECEIVE_FIRMWARE_IMAGE_REQUEST.rawValue()) {
            Log.i(TAG, "Requested code is Receive Firmware Image now processing response status");
            processReceiveFirmwareResponseStatus();
        }
        else if (dfuResponse.getrequestedCode() == DfuOperations.VALIDATE_FIRMWARE_REQUEST.rawValue()) {
            Log.i(TAG, "Requested code is Validate Firmware now processing response status");
            processValidateFirmwareResponseStatus();
        }
        else
            Log.i(TAG,"invalid Requested code in DFU Response " + dfuResponse.getrequestedCode());

    }
    void processStartDFUResponseStatus()
    {
        Log.i(TAG,"processStartDFUResponseStatus");
        String errorMessage = "Error on StartDFU\n Message: " + responseErrorMessage(dfuResponse.getresponseStatus());

        if (dfuResponse.getresponseStatus() == DfuOperationStatus.OPERATION_SUCCESSFUL_RESPONSE.rawValue()) {
            Log.i(TAG,"successfully received startDFU notification");
            startSendingFile();
        }
        else if (dfuResponse.getresponseStatus() == DfuOperationStatus.OPERATION_NOT_SUPPORTED_RESPONSE.rawValue()) {
            Log.i(TAG,"device has old DFU. switching to old DFU ...");
            performOldDFUOnFile();
        }

        else {
                Log.i(TAG,errorMessage);
                if(mOnOtaControllerListener.notEmpty()) mOnOtaControllerListener.get().onError(ERRORCODE.STARTDFUERROR);
                resetSystem();
        }

    }
    void processReceiveFirmwareResponseStatus()
    {
        Log.i(TAG,"processReceiveFirmwareResponseStatus");
        if (dfuResponse.getresponseStatus() == DfuOperationStatus.OPERATION_SUCCESSFUL_RESPONSE.rawValue()) {
            Log.i(TAG,"successfully received notification for whole File transfer");
            validateFirmware();
            //why call "activateAndReset" here, due to Ble validate Firmware done, will Close nevo BT
            //before nevo BT closed, we must send Reset cmd 0x05 to nevo,that let nevo service get change from DFU to normal
            //if no received before BT closed, nevo will keep DFU mode and the name also keep as "Nevo_DFU"
            activateAndReset();
        }
        else {
            Log.i(TAG,"Firmware Image failed, Error Status:" + responseErrorMessage(dfuResponse.getresponseStatus()));
            String errorMessage = "Error on Receive Firmware Image\n Message:" + responseErrorMessage(dfuResponse.getresponseStatus());
            if(mOnOtaControllerListener.notEmpty()) mOnOtaControllerListener.get().onError(ERRORCODE.INVALIDRESPONSE);
            resetSystem();
        }

    }
    void processValidateFirmwareResponseStatus()
    {
        Log.i(TAG,"processValidateFirmwareResponseStatus");
        if (dfuResponse.getresponseStatus() == DfuOperationStatus.OPERATION_SUCCESSFUL_RESPONSE.rawValue()) {
            Log.i(TAG,"succesfully received notification for ValidateFirmware");
            activateAndReset();
            if(mOnOtaControllerListener.notEmpty()) mOnOtaControllerListener.get().onSuccessfulFileTranferred();
        }
        else {
            Log.i(TAG,"Firmware validate failed, Error Status: "+ responseErrorMessage(dfuResponse.getresponseStatus()));
            String errorMessage = "Error on Validate Firmware Request\n Message: " + responseErrorMessage(dfuResponse.getresponseStatus());
            if(mOnOtaControllerListener.notEmpty()) mOnOtaControllerListener.get().onError(ERRORCODE.INVALIDRESPONSE);
            resetSystem();
        }

    }
    void processPacketNotification()
    {
        Log.i(TAG,"received Packet Received Notification");
        if (writingPacketNumber < numberOfPackets) {
            writeNextPacket();
        }
    }
    void setDFUResponseStruct(byte[] data)
    {
        Log.w(TAG, "received Packet Response:" + new String(Hex.encodeHex(data)));
        if(data.length<3)
        {
            Log.e(TAG, "received Packet Response invaild");
            return;
        }
        dfuResponse.setresponseCode(data[0]);
        dfuResponse.setrequestedCode(data[1]);
        dfuResponse.setresponseStatus(data[2]);
    }
    void processDFUResponse(NevoFirmwareData data)
    {
        Log.i(TAG,"processDFUResponse");
        setDFUResponseStruct(data.getRawData());

        if (dfuResponse.getresponseCode() == DfuOperations.RESPONSE_CODE.rawValue()) {
            processRequestedCode();
        }
        else if(dfuResponse.getresponseCode() == DfuOperations.PACKET_RECEIPT_NOTIFICATION_RESPONSE.rawValue()) {
            processPacketNotification();
        }

    }
    void performOldDFUOnFile()
    {
        if (dfuFirmwareType == DfuFirmwareTypes.APPLICATION)
        {
            openFile(firmwareFile);
            sendRequest(new NevoOTAControlRequest(mContext,new byte[]{(byte)DfuOperations.START_DFU_REQUEST.rawValue()}));
            sendRequest(new NevoOTAPacketFileSizeRequest(mContext,binFileSize,true));
        }
        else
        {
            String errorMessage = "Old DFU only supports Application upload";
            if(mOnOtaControllerListener.notEmpty()) mOnOtaControllerListener.get().onError(ERRORCODE.NOSUPPORTOLDDFU);
            resetSystem();
        }

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
        if(!isConnected()) {
            String errorMessage = mContext.getString(R.string.connect_error_no_nevo_do_ota);
            Log.e(TAG,errorMessage);
            state = DFUControllerState.INIT;
            Toast.makeText(mContext,errorMessage,Toast.LENGTH_LONG).show();
            if(mOnOtaControllerListener.notEmpty()) mOnOtaControllerListener.get().onError(ERRORCODE.NOCONNECTION);
            return;
        }
        mPacketsbuffer.clear();
        lastprogress = 0.0;
        progress = 0.0;
        mTimeoutTimer = new Timer();
        mTimeoutTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (lastprogress == progress) //when no change happened, timeout
                {
                    if (MAX_TIME < 60000) MAX_TIME = MAX_TIME + 5000;
                    Log.w(TAG, "* * * OTA timeout * * *" + "state = " + state + ",connected:" + isConnected());
                    String errorMessage = "Timeout,please try again";
                    if (state == DFUControllerState.SEND_START_COMMAND
                            && dfuFirmwareType == DfuFirmwareTypes.APPLICATION
                            && isConnected()) {
                        Log.w(TAG, "* * * call SamsungS4Patch function * * *");
                        SamsungS4Patch();
                    }
                    //when start Scan DFU service, perhaps get nothing with 20s, here need again scan it?
                    else if (state == DFUControllerState.DISCOVERING && dfuFirmwareType == DfuFirmwareTypes.APPLICATION) {
                        Log.w(TAG, "* * * call OTA timeout function for no found DFU service * * *");
                        if (mOnOtaControllerListener.notEmpty())
                            mOnOtaControllerListener.get().onError(ERRORCODE.NODFUSERVICE);
                    } else {
                        Log.w(TAG, "* * * call OTA timeout function * * *");
                        if (mOnOtaControllerListener.notEmpty())
                            mOnOtaControllerListener.get().onError(ERRORCODE.TIMEOUT);
                    }
                } else {
                    lastprogress = progress;
                }

            }
        }, MAX_TIME, MAX_TIME);

        dfuFirmwareType = firmwareType;
        firmwareFile = filename;
        //Hex to bin and read it to buffer
        openFile(filename);

        //help mode for doing OTA
        if(manualmode && dfuFirmwareType == DfuFirmwareTypes.APPLICATION)
        {
            state = DFUControllerState.SEND_FIRMWARE_DATA;
            sendRequest(new NevoOTAControlRequest(mContext, new byte[]{(byte) DfuOperations.START_DFU_REQUEST.rawValue(), (byte) DfuFirmwareTypes.APPLICATION.rawValue()}));
            sendRequest(new NevoOTAPacketFileSizeRequest(mContext, binFileSize,false));
        }
        //pair mode for doing OTA
        else
        {
            state = DFUControllerState.IDLE;
            connectionController.setOTAMode(false, true);
        }
        if(mOnOtaControllerListener.notEmpty()) mOnOtaControllerListener.get().onPrepareOTA(firmwareType);
    }

    public void setOtaMode(boolean otaMode,boolean disConnect)
    {
        connectionController.setOTAMode(otaMode, disConnect);
    }
    /**
     * patch for samsung S4 Ble OTA, send start ble OTA cmd 0x72, can't get disconnect after 7s
     * so here add this patch function do it
     * this patch will make a disconnect to nevo (normal OTA should be get disconnect from nevo )
     */
    private void SamsungS4Patch()
    {
        //app make a disconnect to nevo
        connectionController.setOTAMode(true, true);
    }
    /**
     * cancel OTA
     */
    public void cancelDFU()
    {
        Log.i(TAG, "cancelDFU");

        if (dfuFirmwareType.rawValue() == DfuFirmwareTypes.APPLICATION.rawValue())
        { resetSystem();}

        if(mOnOtaControllerListener.notEmpty()) mOnOtaControllerListener.get().onDFUCancelled();
    }
    /**
     * get in charge of ConnectionController
     */
    public void switch2OtaController()
    {
        connectionController.setOnExceptionListener(this);
        connectionController.setOnDataReceivedListener(this);
        connectionController.setOnConnectListener(this);
        connectionController.setOnFirmwareVersionListener(this);
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
        return connectionController.isConnected();
    }

    @Override
    public DFUControllerState getState()
    {
        return state;
    }
    @Override
    public void setState(DFUControllerState state)
    {
        this.state = state;
    }
    @Override
    public  void switch2SyncController()
    {
        connectionController.setOnExceptionListener((OnExceptionListener) mContext.getSyncController());
        connectionController.setOnDataReceivedListener((OnDataReceivedListener)mContext.getSyncController());
        connectionController.setOnConnectListener((OnConnectListener)mContext.getSyncController());
        connectionController.setOnFirmwareVersionListener((OnFirmwareVersionListener)mContext.getSyncController());
    }

    /**
     reset to normal mode "NevoProfile"
     parameter: switch2SyncController: true/false
     step1: restore Address
     step2: restore syncController
     step3: restore normal mode
     step4: reconnect
     //from OTA mode to normal mode, must make syncController to handle connectionController
     because MCU/BLE ota, user has done one of them, perhaps do another one,
     so no need make syncController handle connectionController
     */
    @Override
    public void reset(boolean switch2SyncController) {

        if(mTimeoutTimer!=null) {mTimeoutTimer.cancel();mTimeoutTimer=null;}
        //reset it to INIT status !!!IMPORTANT!!!
        state = DFUControllerState.INIT;

        if(dfuFirmwareType == DfuFirmwareTypes.APPLICATION )
        {
            connectionController.restoreSavedAddress();
        }
        if (switch2SyncController)
        {
            switch2SyncController();
        }
        if(manualmode)
        {
            manualmode = false;
            connectionController.forgetSavedAddress();
        }

        //disconnect and reconnect for reading new version
        connectionController.setOTAMode(false, true);

    }

    @Override
    public String getFirmwareVersion() {
        return connectionController.getFirmwareVersion();
    }

    @Override
    public String getSoftwareVersion() {
        return connectionController.getSoftwareVersion();
    }

    @Override
    public void forGetDevice()
    {
        //BLE OTA need repair NEVO, so here forget this nevo when OTA done.
        connectionController.unPairDevice();
    }
    //end public function

    //start ConnectionController.Delegate interface
    @Override
    public void onConnectionStateChanged(boolean connected, String address) {
        if(mOnOtaControllerListener.notEmpty()) mOnOtaControllerListener.get().connectionStateChanged(connected);
        //only BLE OTA run below code
        if(dfuFirmwareType == DfuFirmwareTypes.APPLICATION )
        {
            if (connected)
            {
                if (state == DFUControllerState.SEND_RECONNECT)
                {
                    state = DFUControllerState.SEND_START_COMMAND;

                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            connectionController.sendRequest(new NevoOTAStartRequest(mContext));
                        }
                    },1000);
                }
                else if (state == DFUControllerState.DISCOVERING)
                {
                    state = DFUControllerState.SEND_FIRMWARE_DATA;

                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            sendRequest(new NevoOTAControlRequest(mContext, new byte[]{(byte) DfuOperations.START_DFU_REQUEST.rawValue(), (byte) DfuFirmwareTypes.APPLICATION.rawValue()}));
                            sendRequest(new NevoOTAPacketFileSizeRequest(mContext, binFileSize,false));
                        }
                    },1000);
                }
            }
            else
            {
                if (state == DFUControllerState.IDLE)
                {
                    state = DFUControllerState.SEND_RECONNECT;

                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            connectionController.reconnect();
                        }
                    },1000);
                }

                //by BLE peer disconnect when normal mode to ota mode
                else if (state == DFUControllerState.SEND_START_COMMAND)
                {
                    state = DFUControllerState.DISCOVERING;
                    connectionController.setOTAMode(true, true);

                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.i(TAG,"***********set OTA mode,forget it firstly,and scan DFU service*******");
                            //when switch to DFU mode, the MAC address has changed to another one
                            connectionController.forgetSavedAddress();
                            connectionController.connect();
                        }
                    },1000);
                }
            }
        }
        //only MCU OTA run below code
        else if(dfuFirmwareType == DfuFirmwareTypes.SOFTDEVICE )
        {
            if (connected)
            {
                if (state == DFUControllerState.SEND_RECONNECT)
                {
                    state = DFUControllerState.SEND_START_COMMAND;

                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            connectionController.sendRequest(new NevoMCU_OTAStartRequest(mContext));
                        }
                    },1000);
                }
            }
            else
            {
                if (state == DFUControllerState.IDLE)
                {
                    state = DFUControllerState.SEND_RECONNECT;

                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            connectionController.reconnect();
                        }
                    },1000);
                }
            }
        }

    }

    @Override
    public void onSearching() {

    }

    @Override
    public void onSearchSuccess() {

    }

    @Override
    public void onSearchFailure() {

    }

    @Override
    public void onConnecting() {

    }

    @Override
    public void onDataReceived(SensorData data) {

        if (data.getType().equals(NevoFirmwareData.TYPE))
        {
            if(dfuFirmwareType == DfuFirmwareTypes.APPLICATION
                    && ((NevoFirmwareData)data).getUuid().equals(UUID.fromString(mContext.getString(R.string.NEVO_OTA_CALLBACK_CHARACTERISTIC))))
            {
                processDFUResponse((NevoFirmwareData) data);
            }
            else if(dfuFirmwareType == DfuFirmwareTypes.SOFTDEVICE
                    && ((NevoFirmwareData)data).getUuid().equals(UUID.fromString(mContext.getString(R.string.NEVO_OTA_CHARACTERISTIC))))
            {
                QueuedMainThreadHandler.getInstance(QueuedMainThreadHandler.QueueType.OtaController).next();
                MCU_processDFUResponse((NevoFirmwareData) data);
            }
        }
    }

    @Override
    public void onException(NevoException e) {
        //the exception got happened when do connection NEVO
        Log.e(TAG," ********* onException ********* " + e + ",state:" + getState());
        if (mTimeoutTimer != null) {
            mTimeoutTimer.cancel();
            mTimeoutTimer = null;
        }
        if(e instanceof BLEUnstableException && getState()!= DFUControllerState.DISCOVERING)
        {
            Log.e(TAG,"happen " + e + ",due to DFU mode to normal mode, perhaps BLE is not stable,again auto reconnect it after 10s");
            return;
        }
        if(mOnOtaControllerListener.notEmpty()) {
            mOnOtaControllerListener.get().onError(ERRORCODE.EXCEPTION);
        }

    }

    @Override
    public void firmwareVersionReceived(DfuFirmwareTypes whichfirmware, String version) {
        if(mOnOtaControllerListener.notEmpty()) {
            mOnOtaControllerListener.get().firmwareVersionReceived(whichfirmware,version);
        }
    }
    //end ConnectionController.Delegate interface

    //below function used for MCU OTA, package function
    void MCU_openfirmware(String filename)
    {
        InputStream is = null;
        try {
            is = mContext.getAssets().open(filename);
            byte[] buffer = new byte[16];
            int len =-1;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            is.skip(16*1024);
            while( -1 != (len = is.read(buffer)))
            {
                bos.write(buffer,0,len);
            }
            binFileData = bos.toByteArray();
            binFileSize = binFileData.length;
            firmwareDataBytesSent = 0;
            curpage = 0;
            totalpage = binFileSize/DFUCONTROLLER_PAGE_SIZE;
            checksum = 0;
            dfuFirmwareType = DfuFirmwareTypes.SOFTDEVICE;

            for(byte b : binFileData ){
                checksum = checksum + (int)(b);
            }

            Log.i(TAG,"Set firmware with size:"+ binFileData.length + "notificationPacketInterval:"+notificationPacketInterval + ", totalpage: "+totalpage + ",Checksum: "+ checksum);

            bos.close();
            is.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    void MCU_sendFirmwareChunk()
    {
        Log.i(TAG,"sendFirmwareData");
        NevoMCU_OTAPageRequest Onepage = new NevoMCU_OTAPageRequest(mContext);
        for (int i = 0; i < notificationPacketInterval && firmwareDataBytesSent < binFileSize; i++)
        {
            int length = DFUCONTROLLER_MAX_PACKET_SIZE;
            byte[] pagePacket;
            if( i == 0)
            {   //LSB format
                pagePacket = new byte[] {00,(byte)0x71,
                        (byte)(curpage & 0xFF),
                        (byte)((curpage>>8) & 0xFF),
                        (byte)(totalpage & 0xFF),
                        (byte)((totalpage>>8) & 0xFF),
                    00,00,00,00,00,00,00,00,00,00,00,00,00,00};
            }
            else
            {
                if( i != (notificationPacketInterval - 1))
                {
                    length = DFUCONTROLLER_MAX_PACKET_SIZE;
                }
                else
                {
                    length = DFUCONTROLLER_PAGE_SIZE%DFUCONTROLLER_MAX_PACKET_SIZE;
                }

                 byte[] currentData = new byte[length];
                 System.arraycopy(binFileData,firmwareDataBytesSent,currentData,0,length);

                //20 bytes, last packet of the page, remains 8 bytes,fill 0
                byte [] fulldata = new byte[] {0,(byte)0x71,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};

                if (i == notificationPacketInterval - 1)
                {
                    fulldata[0] = (byte) 0xFF;
                }
                else
                {
                    fulldata[0] = (byte) i;
                }
                System.arraycopy(currentData,0,fulldata,2,length);

                pagePacket = fulldata;

                firmwareDataBytesSent += length;
            }
            Onepage.addPacket(new NevoMCU_OTAPacketRequest(mContext,pagePacket));
        }
        if(curpage < totalpage)
        {
            sendRequest(Onepage);
            progress = 100.0*(firmwareDataBytesSent) / (binFileSize);
            if(mOnOtaControllerListener.notEmpty()) mOnOtaControllerListener.get().onTransferPercentage((int)(progress));
            Log.i(TAG,"didWriteDataPacket");

            if (state == DFUControllerState.SEND_FIRMWARE_DATA)
            {
                curpage++;
                state = DFUControllerState.WAIT_RECEIPT;
            }

        }
        else
        {
            state = DFUControllerState.FINISHED;
            progress = 100.0;
            if(mOnOtaControllerListener.notEmpty()) mOnOtaControllerListener.get().onTransferPercentage((int)(progress));
            sendRequest(new NevoMCU_OTAChecksumRequest(mContext,totalpage, checksum));
            Log.i(TAG,"sendEndPacket, totalpage = " + totalpage +", checksum = " + checksum + ", checksum-Lowbyte = " + (checksum&0xFF));
            return;
        }
        Log.i(TAG,"Sent " + (firmwareDataBytesSent) + " bytes, pageno: " + (curpage));

    }

    void MCU_processDFUResponse(NevoFirmwareData rawData)
    {
        Log.i(TAG,"didReceiveReceipt");
        mPacketsbuffer.add(rawData);
        byte []databyte = rawData.getRawData();
        if(databyte[0] == (byte)0xFF)
        {
            if( databyte[1] == (byte)0x70)
            {
                //first Packet  as header get successful response!
                progress = 1.0*firmwareDataBytesSent / binFileSize;
                state = DFUControllerState.SEND_FIRMWARE_DATA;
            }
            if( databyte[1] == (byte)0x71 && state == DFUControllerState.FINISHED)
            {
                byte []databyte1 = mPacketsbuffer.get(0).getRawData();

                if(databyte1[1] == (byte)0x71
                        && databyte1[2] == (byte)0xFF
                        && databyte1[3] == (byte)0xFF
                        )
                {
                    byte TotalPageLo = (byte) (totalpage & 0xFF);
                    byte TotalPageHi = (byte) ((totalpage>>8) & 0xFF);

                    if (databyte1[4] == TotalPageLo
                            && databyte1[5] == TotalPageHi)
                    {
                        //Check sum match ,OTA over.
                        Log.i(TAG,"Checksum match ,OTA get success!");
                        if(mTimeoutTimer!=null) {mTimeoutTimer.cancel();mTimeoutTimer=null;}
                        if(mOnOtaControllerListener.notEmpty()) mOnOtaControllerListener.get().onSuccessfulFileTranferred();
                    }
                    else
                    {
                        Log.i(TAG,"Checksum error ,OTA get failure!");
                        if(mTimeoutTimer!=null) {mTimeoutTimer.cancel();mTimeoutTimer=null;}
                        if(mOnOtaControllerListener.notEmpty()) mOnOtaControllerListener.get().onError(ERRORCODE.CHECKSUMERROR);
                    }
                }
            }

            mPacketsbuffer.clear();

            if (state == DFUControllerState.SEND_FIRMWARE_DATA)
            {
                MCU_sendFirmwareChunk();
            }
            else if(state == DFUControllerState.WAIT_RECEIPT)
            {
                state = DFUControllerState.SEND_FIRMWARE_DATA;
                MCU_sendFirmwareChunk();
            }
        }

    }
    //end MCU OTA
}
