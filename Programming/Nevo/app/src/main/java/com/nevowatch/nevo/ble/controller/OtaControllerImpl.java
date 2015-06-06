package com.nevowatch.nevo.ble.controller;

import android.content.Context;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

import com.nevowatch.nevo.MainActivity;
import com.nevowatch.nevo.ble.model.packet.NevoRawData;
import com.nevowatch.nevo.ble.model.packet.SensorData;
import com.nevowatch.nevo.ble.model.request.NevoOTAPacketFileSizeRequest;
import com.nevowatch.nevo.ble.util.Constants.DfuOperationStatus;
import com.nevowatch.nevo.ble.util.Constants.enumPacketOption;
import com.nevowatch.nevo.ble.util.Constants.DFUControllerState;
import com.nevowatch.nevo.ble.util.Constants.DfuFirmwareTypes;
import com.nevowatch.nevo.ble.util.Constants.DFUResponse;
import com.nevowatch.nevo.ble.util.Constants.DfuOperations;
import com.nevowatch.nevo.ble.util.Optional;
import com.nevowatch.nevo.ble.util.IntelHex2BinConverter;
import com.nevowatch.nevo.ble.model.request.NevoOTAPacketRequest;
import com.nevowatch.nevo.ble.model.request.NevoOTAControlRequest;
import org.apache.commons.codec.binary.Hex;

import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


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
    private byte[] hexFileData ;
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

    private ConnectionController.Delegate mOldDelegate = null;
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

        mOldDelegate = mConnectionController.setDelegate(this);

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
                int len =-1;
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
            Log.i(TAG, new String(Hex.encodeHex(nextPacketData)));

            mConnectionController.sendRequest(new NevoOTAPacketRequest(nextPacketData));
            progress = 100.0;
            percentage = (int)(progress);
            Log.i(TAG,"DFUOperations: onTransferPercentage " + percentage);
            if(mOnOtaControllerListener.notEmpty()) mOnOtaControllerListener.get().onTransferPercentage(percentage);
            writingPacketNumber++;
            mTimeoutTimer.cancel();
            Log.i(TAG,"DFUOperations: onAllPacketsTransfered");
            break;

        }
        byte[] nextPacketData = new byte[enumPacketOption.PACKET_SIZE.rawValue()];
        System.arraycopy(binFileData,writingPacketNumber*enumPacketOption.PACKET_SIZE.rawValue(),nextPacketData,0,enumPacketOption.PACKET_SIZE.rawValue());

        Log.i(TAG,"writing packet number " + (writingPacketNumber+1) + " ...");
        Log.i(TAG, new String(Hex.encodeHex(nextPacketData)));

        mConnectionController.sendRequest(new NevoOTAPacketRequest(nextPacketData));
        progress = 100.0*writingPacketNumber * enumPacketOption.PACKET_SIZE.rawValue() / binFileSize;
        percentage = (int)progress;

        Log.i(TAG,"DFUOperations: onTransferPercentage "+ percentage);
            if(mOnOtaControllerListener.notEmpty()) mOnOtaControllerListener.get().onTransferPercentage(percentage);

        writingPacketNumber++;

    }

    }
    void startSendingFile()
    {
        Log.i(TAG,"DFUOperationsdetails enablePacketNotification");
        mConnectionController.sendRequest(new NevoOTAControlRequest(new byte[]{(byte)DfuOperations.PACKET_RECEIPT_NOTIFICATION_REQUEST.rawValue()
                                                                              ,(byte)enumPacketOption.PACKETS_NOTIFICATION_INTERVAL.rawValue()
                                                                              ,0}));
        Log.i(TAG, "DFUOperationsdetails receiveFirmwareImage");
        mConnectionController.sendRequest(new NevoOTAControlRequest(new byte[]{(byte)DfuOperations.RECEIVE_FIRMWARE_IMAGE_REQUEST.rawValue()}));
        writeNextPacket();
        if(mOnOtaControllerListener.notEmpty()) mOnOtaControllerListener.get().onDFUStarted();

    }
    void resetSystem()
    {
        Log.i(TAG,"DFUOperationsDetails resetSystem");
        mConnectionController.sendRequest(new NevoOTAControlRequest(new byte[]{(byte)DfuOperations.RESET_SYSTEM.rawValue()}));
    }

    void validateFirmware()
    {
        Log.i(TAG,"DFUOperationsDetails validateFirmware");
        mConnectionController.sendRequest(new NevoOTAControlRequest(new byte[]{(byte)DfuOperations.VALIDATE_FIRMWARE_REQUEST.rawValue()}));
    }
    void activateAndReset()
    {
        Log.i(TAG,"DFUOperationsDetails activateAndReset");
        mConnectionController.sendRequest(new NevoOTAControlRequest(new byte[]{(byte)DfuOperations.ACTIVATE_AND_RESET_REQUEST.rawValue()}));
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

        if (dfuResponse.getresponseCode() == DfuOperations.START_DFU_REQUEST.rawValue()){
            Log.i(TAG, "Requested code is StartDFU now processing response status");
            processStartDFUResponseStatus();
        }
        if (dfuResponse.getresponseCode() == DfuOperations.RECEIVE_FIRMWARE_IMAGE_REQUEST.rawValue()) {
            Log.i(TAG, "Requested code is Receive Firmware Image now processing response status");
            processReceiveFirmwareResponseStatus();
        }
        if (dfuResponse.getresponseCode() == DfuOperations.VALIDATE_FIRMWARE_REQUEST.rawValue()) {
            Log.i(TAG, "Requested code is Validate Firmware now processing response status");
            processValidateFirmwareResponseStatus();
        }
        else
            Log.i(TAG,"invalid Requested code in DFU Response " + dfuResponse.getresponseCode());

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
                if(mOnOtaControllerListener.notEmpty()) mOnOtaControllerListener.get().onError(errorMessage);
                resetSystem();
        }

    }
    void processReceiveFirmwareResponseStatus()
    {
        Log.i(TAG,"processReceiveFirmwareResponseStatus");
        if (dfuResponse.getresponseStatus() == DfuOperationStatus.OPERATION_SUCCESSFUL_RESPONSE.rawValue()) {
            Log.i(TAG,"successfully received notification for whole File transfer");
            validateFirmware();
        }
        else {
            Log.i(TAG,"Firmware Image failed, Error Status:" + responseErrorMessage(dfuResponse.getresponseStatus()));
            String errorMessage = "Error on Receive Firmware Image\n Message:" + responseErrorMessage(dfuResponse.getresponseStatus());
            if(mOnOtaControllerListener.notEmpty()) mOnOtaControllerListener.get().onError(errorMessage);
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
            if(mOnOtaControllerListener.notEmpty()) mOnOtaControllerListener.get().onError(errorMessage);
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
        Log.e(TAG,"received Packet Response:" + new String(Hex.encodeHex(data)));
        if(data.length<3)
        {
            Log.e(TAG, "received Packet Response invaild");
            return;
        }
        dfuResponse.setresponseCode(data[0]);
        dfuResponse.setrequestedCode(data[1]);
        dfuResponse.setresponseStatus(data[2]);
    }
    void processDFUResponse(byte[] data)
    {
        Log.i(TAG,"processDFUResponse");
        setDFUResponseStruct(data);

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
            mConnectionController.sendRequest(new NevoOTAControlRequest(new byte[]{(byte)DfuOperations.START_DFU_REQUEST.rawValue(), (byte) DfuFirmwareTypes.APPLICATION.rawValue()}));
            mConnectionController.sendRequest(new NevoOTAPacketFileSizeRequest(binFileSize));
        }
        else
        {
            String errorMessage = "Old DFU only supports Application upload";
            if(mOnOtaControllerListener.notEmpty()) mOnOtaControllerListener.get().onError(errorMessage);
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
        lastprogress = 0.0;
        progress = 0.0;
        mTimeoutTimer = new Timer();
        mTimeoutTimer.schedule(new TimerTask(){
            @Override
            public void run() {
                if (lastprogress == progress  && progress != 100.0)
                {
                    Log.w(TAG,"* * * OTA timeout * * *");
                    String errorMessage = "Timeout,please try again";
                    if(mOnOtaControllerListener.notEmpty()) mOnOtaControllerListener.get().onError(errorMessage);

                }
                else
                {
                    lastprogress = progress;
                }

            }
        },MAX_TIME,MAX_TIME);

        mConnectionController.setDelegate(this);
        state = DFUControllerState.IDLE;
        dfuFirmwareType = firmwareType;
        firmwareFile = filename;
        //Hex to bin and read it to buffer
        openFile(filename);
        //enable it done after doing discover service
        //[dfuRequests enableNotification];

        mConnectionController.setOTAMode(true,true);

    }

    /**
     * cancel OTA
     */
    public void cancelDFU()
    {
        Log.i(TAG,"cancelDFU");

        if (dfuFirmwareType.rawValue() == DfuFirmwareTypes.APPLICATION.rawValue())
        { resetSystem();}

        if(mOnOtaControllerListener.notEmpty()) mOnOtaControllerListener.get().onDFUCancelled();
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
        mTimeoutTimer.cancel();
        //reset it to INIT status !!!IMPORTANT!!!
        state = DFUControllerState.INIT;

        if(dfuFirmwareType == DfuFirmwareTypes.APPLICATION )
        {
            mConnectionController.restoreSavedAddress();
        }
        if (switch2SyncController)
        {
            mConnectionController.setDelegate(mOldDelegate);
        }
        mConnectionController.setOTAMode(false, true);
        mConnectionController.connect();
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
        //TODO OTA
    }

    @Override
    public void onDataReceived(SensorData data) {
        //TODO OTA
    }

    @Override
    public void onException(Exception e) {
        //TODO OTA
    }

    @Override
    public void firmwareVersionReceived(DfuFirmwareTypes whichfirmware, String version) {
        //TODO refresh OTA screen when OTA finished!
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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    void MCU_sendFirmwareChunk()
    {
        /*
        NSLog("sendFirmwareData");

        for var i:Int = 0; i < notificationPacketInterval && firmwareDataBytesSent < binFileSize; i++
        {
            var length = DFUCONTROLLER_MAX_PACKET_SIZE;
            var pagePacket : NSData;
            if( i == 0)
            {
                //LSB format
                var pagehead :[UInt8] = [
                00,0x71,
                        UInt8(curpage & 0xFF),
                        UInt8((curpage>>8) & 0xFF),
                        UInt8(totalpage & 0xFF),
                        UInt8((totalpage>>8) & 0xFF),
                        00,00,00,00,00,00,00,00,00,00,00,00,00,00]

                pagePacket = NSData(bytes: pagehead, length: pagehead.count)
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

                var currentRange:NSRange = NSMakeRange(self.firmwareDataBytesSent, length)

                var currentData:NSData =  binFileData!.subdataWithRange(currentRange)

                var fulldata:NSMutableData = NSMutableData()

                if i == self.notificationPacketInterval - 1
                {
                    fulldata.appendBytes([0xFF,0x71] as [UInt8], length: 2)
                }
                else
                {
                    fulldata.appendBytes([UInt8(i),0x71] as [UInt8], length: 2)
                }

                fulldata.appendData(currentData)

                //last packet of the page, remains 8 bytes,fill 0
                if(i == (notificationPacketInterval - 1))
                {
                    fulldata.appendBytes([0,0,0,0,0,0,0,0] as [UInt8], length: 8)
                }
                pagePacket = fulldata

                firmwareDataBytesSent += length;
            }

            mConnectionController?.sendRequest(Mcu_OnePacketRequest(packetdata: pagePacket ))

        }
        if(curpage < totalpage)
        {
            progress = 100.0*Double(firmwareDataBytesSent) / Double(binFileSize);
            mDelegate?.onTransferPercentage(Int(progress))
            NSLog("didWriteDataPacket");

            if (state == DFUControllerState.SEND_FIRMWARE_DATA)
            {
                curpage++
                state = DFUControllerState.WAIT_RECEIPT
            }

        }
        else
        {
            state = DFUControllerState.FINISHED;
            progress = 100.0
            mDelegate?.onTransferPercentage(Int(progress))
            mConnectionController?.sendRequest(Mcu_CheckSumPacketRequest(totalpage: totalpage, checksum: checksum))
            NSLog("sendEndPacket, totalpage =\(totalpage), checksum = \(checksum), checksum-Lowbyte = \(checksum&0xFF)")
            mTimeoutTimer?.invalidate()
            return
        }
        NSLog("Sent \(self.firmwareDataBytesSent) bytes, pageno: \(curpage).")
        */
    }
    void MCU_processDFUResponse(NevoRawData rawData)
    {
        /*
        NSLog("didReceiveReceipt")
        mPacketsbuffer.append(packet.getRawData())
        var databyte:[UInt8] = NSData2Bytes(packet.getRawData())

        if(databyte[0] == 0xFF)
        {
            if( databyte[1] == 0x70)
            {
                //first Packet  as header get successful response!
                progress = Double(firmwareDataBytesSent) / Double(binFileSize)
                self.state = DFUControllerState.SEND_FIRMWARE_DATA

            }
            if( databyte[1] == 0x71 && self.state == DFUControllerState.FINISHED)
            {
                var databyte1:[UInt8] = NSData2Bytes(mPacketsbuffer[0])

                if(databyte1[1] == 0x71
                        && databyte1[2] == 0xFF
                        && databyte1[3] == 0xFF
                        )
                {
                    var TotalPageLo:UInt8 = UInt8(totalpage & 0xFF)
                    var TotalPageHi:UInt8 = UInt8((totalpage>>8) & 0xFF)

                    if (databyte1[4] == TotalPageLo
                            && databyte1[5] == TotalPageHi)
                    {
                        //Check sum match ,OTA over.
                        NSLog("Checksum match ,OTA get success!");
                        mDelegate?.onSuccessfulFileTranferred()
                    }
                    else
                    {
                        NSLog("Checksum error ,OTA get failure!");
                        mDelegate?.onError(NSString(string:"Checksum error ,OTA get failure!"))
                    }
                    //reset to idle
                    self.state = DFUControllerState.IDLE
                }
            }

            mPacketsbuffer = []

            if (self.state == DFUControllerState.SEND_FIRMWARE_DATA)
            {
                MCU_sendFirmwareChunk()
            }
            else if(self.state == DFUControllerState.WAIT_RECEIPT)
            {
                self.state = DFUControllerState.SEND_FIRMWARE_DATA;
                MCU_sendFirmwareChunk()
            }
        }
        */
    }

    //end MCU OTA
}
