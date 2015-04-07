package com.nevowatch.nevo.ble.model.packet;

public abstract class NevoRawData implements SensorData {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2770351097320911999L;

	/** The TYPE of data, the getType function should return this value. */
	public final static String TYPE = "NevoTraining";
	
	/*
	 * (non-Javadoc)
	 * @see fr.imaze.sdk.model.receive.SensorData#getType()
	 */
	@Override
	public String getType() {
		return TYPE;
	}
	
	public abstract byte[] getRawData();
	
}
