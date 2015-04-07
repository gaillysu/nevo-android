package com.nevowatch.nevo.ble.model.request;

public class SetProfileNevoRequest extends NevoRequest {
	public  final static  byte HEADER = 0x20;
	@Override
	public byte[] getRawData() {
	
		return null;
	}

	@Override
	public byte[][] getRawDataEx() {

        int age = 35;
        int height = 175; //cm
        int weight = 77;  //kg
        int sex = 1; //man:1,female:0
        int unit = 1; //unit ???

		return new byte[][] {
				   {0,HEADER,
                   (byte)(age&0xFF),(byte)(height&0xFF),(byte)(weight&0xFF),(byte)(sex&0xFF),
                    0,0,0,0,
                    0,0,0,0,
                    0,0,0,0,
                    0,0
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
