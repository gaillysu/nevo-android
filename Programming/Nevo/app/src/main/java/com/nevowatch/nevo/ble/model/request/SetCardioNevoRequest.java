package com.nevowatch.nevo.ble.model.request;

public class SetCardioNevoRequest extends NevoRequest {
	public  final static  byte HEADER = 0x23;
	@Override
	public byte[] getRawData() {
		return null;
	}

	@Override
	public byte[][] getRawDataEx() {

        int maxHR  = 210;
        int restHR = 65;
        int zone_HR_H  = 180;
        int zone_HR_L  = 60;

        return new byte[][] {
				   {0,HEADER,
                   (byte)(maxHR&0xFF),(byte)(restHR&0xFF),
                   (byte)(zone_HR_H&0xFF),(byte)(zone_HR_L&0xFF),
                    0,0,
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
