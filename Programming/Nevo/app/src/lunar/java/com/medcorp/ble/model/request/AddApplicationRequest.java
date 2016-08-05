package com.medcorp.ble.model.request;

import android.content.Context;

import com.medcorp.ble.datasource.GattAttributesDataSourceImpl;
import com.medcorp.model.ApplicationInfomation;
import com.medcorp.util.SplitPacketConverter;

import net.medcorp.library.ble.datasource.GattAttributesDataSource;
import net.medcorp.library.ble.model.request.BLERequestData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by med on 16/8/5.
 */
public class AddApplicationRequest extends BLERequestData {
    public  final static  byte HEADER = 0x32;
    private ApplicationInfomation applicationInfomation;

    public AddApplicationRequest(Context context, ApplicationInfomation applicationInfomation) {
        super(new GattAttributesDataSourceImpl(context));
        this.applicationInfomation = applicationInfomation;
    }

    @Override
    public byte[] getRawData() {
        return null;
    }

    @Override
    public byte[][] getRawDataEx() {
        List<Byte> data = new ArrayList<>();
        data.add((byte)0x80);
        data.add((byte)applicationInfomation.getData().length());
        data.add((byte)(applicationInfomation.getLedPattern()&0xFF));
        data.add((byte)((applicationInfomation.getLedPattern()>>8)&0xFF));
        for(byte b:applicationInfomation.getData().getBytes())
        {
            data.add(b);
        }
        byte[] rawData = new byte[data.size()];
        for(int i=0;i<data.size();i++){
            rawData[i] = data.get(i).byteValue();
        }
        return SplitPacketConverter.rawData2Packets(rawData,HEADER);
    }

    @Override
    public byte getHeader() {
        return HEADER;
    }
}
