package com.nevowatch.nevo.ble.model.request;

public class SetNotificationNevoRequest extends NevoRequest {
	public  final static  byte HEADER = 0x02;
	@Override
	public byte[] getRawData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[][] getRawDataEx() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte getHeader() {
		// TODO Auto-generated method stub
		return HEADER;
	}

}