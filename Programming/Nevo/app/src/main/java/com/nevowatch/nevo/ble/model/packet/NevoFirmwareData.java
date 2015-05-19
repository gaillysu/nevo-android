package com.nevowatch.nevo.ble.model.packet;

import android.bluetooth.BluetoothGattCharacteristic;
import android.util.Log;

import org.apache.commons.codec.binary.Hex;

import java.util.UUID;

/**
 * Created by Hugo on 17/5/15.
 */
public class NevoFirmwareData implements SensorData {

    String mAddress;

    byte[] mRawData;

    /** The TYPE of data, the getType function should return this value. */
    public final static String TYPE = "NevoFirmware";

    public NevoFirmwareData(BluetoothGattCharacteristic characteristic, String address) {

        mAddress = address;

        mRawData = characteristic.getValue();


        Log.i("Nevo Received", new String(Hex.encodeHex(mRawData)));
    }

    @Override
    public String getAddress() {
        return mAddress;
    }

    /*
     * (non-Javadoc)
     * @see fr.imaze.sdk.model.receive.SensorData#getType()
     */
    @Override
    public String getType() {
        return TYPE;
    }
}
