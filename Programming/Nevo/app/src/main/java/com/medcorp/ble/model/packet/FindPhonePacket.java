package com.medcorp.ble.model.packet;

import net.medcorp.library.ble.model.response.MEDRawData;

import java.util.List;

/**
 * Created by med on 16/8/31.
 * this packet comes from watch without any requesting, it is fired by press watch "down" key
 */
public class FindPhonePacket extends Packet {
    public  final static  byte HEADER = 0x45;
    public FindPhonePacket(List<MEDRawData> packets) {
        super(packets);
    }

}
