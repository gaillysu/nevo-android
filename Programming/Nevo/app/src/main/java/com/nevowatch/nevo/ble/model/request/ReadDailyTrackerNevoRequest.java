package com.nevowatch.nevo.ble.model.request;

public class ReadDailyTrackerNevoRequest extends NevoRequest {
	public  final static  byte HEADER = 0x25;
    // tracker no is 0~6
    private int mTrackerNo = 0;

    public ReadDailyTrackerNevoRequest(int trackerno)
    {
        mTrackerNo = trackerno;
    }
	@Override
	public byte[] getRawData() {

		return null;
	}

	@Override
	public byte[][] getRawDataEx() {
        return new byte[][] {
                {0,HEADER,(byte)(mTrackerNo&0xFF),0,
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
