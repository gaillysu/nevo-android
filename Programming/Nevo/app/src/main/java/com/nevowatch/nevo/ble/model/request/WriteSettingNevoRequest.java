package com.nevowatch.nevo.ble.model.request;

public class WriteSettingNevoRequest extends NevoRequest {
	public  final static  byte HEADER = 0x21;
	@Override
	public byte[] getRawData() {
		return null;
	}

	@Override
	public byte[][] getRawDataEx() {
		return new byte[][] {
				   {0,HEADER,0,0,
					0,0,0,0,
					0,0,0,0,
					0,0,0,0,
					0,0,0,0
					},				
				{(byte) 0xFF,HEADER,0,0,
						0,0,0,0,
						0,0,0,0,
						0,0,0,0,
						0,0,0,0
				}					
		      };
	}

	@Override
	public byte getHeader() {
		
		return HEADER;
	}

}
