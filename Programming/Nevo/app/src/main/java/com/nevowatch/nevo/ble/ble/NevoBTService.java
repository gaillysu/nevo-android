/*
 * COPYRIGHT (C) 2014 MED Enterprises LTD. All Rights Reserved.
 */

package com.nevowatch.nevo.ble.ble;

import android.annotation.TargetApi;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.binary.StringUtils;

import com.nevowatch.nevo.ble.kernel.BLEUnstableException;
import com.nevowatch.nevo.ble.kernel.NevoBT;
import com.nevowatch.nevo.ble.kernel.OnConnectListener;
import com.nevowatch.nevo.ble.kernel.OnDataReceivedListener;
import com.nevowatch.nevo.ble.kernel.OnExceptionListener;
import com.nevowatch.nevo.ble.kernel.OnFirmwareVersionListener;
import com.nevowatch.nevo.ble.model.packet.DataFactory;
import com.nevowatch.nevo.ble.model.packet.SensorData;
import com.nevowatch.nevo.ble.model.request.SensorRequest;
import com.nevowatch.nevo.ble.util.Constants;
import com.nevowatch.nevo.ble.util.Optional;
import com.nevowatch.nevo.ble.util.QueuedMainThreadHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;


/**
 * Service for managing connection and data communication with a GATT server
 * hosted on a given Bluetooth LE device.
 * WARNING ! DO NOT RENAME OF MOVE THIS CLASS, BECAUSE IT HAVE TO BE DECLARED IN THE MANIFEST
 * /!\/!\/!\Backbone Class : Modify with care/!\/!\/!\
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class NevoBTService extends Service {
    
    /**
     * Bluetooth adapter
     */
    private BluetoothAdapter mBluetoothAdapter;
    
     
    /**
     * save all the connected BLE Gatt profiles, use LinkedHashMap<> class, default access order is FIFO
     * make sure one service only connected one device
     */
    private  Map<String,BluetoothGatt> mBluetoothGattMap = new LinkedHashMap<String,BluetoothGatt>();
    
	/**
	 * A handler the will ensure : 
	 * 1- that all command are executed on the UI thread
	 * 2- that the next command isn't executed before the previous callback is called
	 * Due to this issue : http://stackoverflow.com/questions/18011816/has-native-android-ble-gatt-implementation-synchronous-nature
	 */
	private QueuedMainThreadHandler mQueuedMainThread;
    
    /**
     * Call this listener when data is received
     */
    private OnDataReceivedListener mDataReceived;
    
    /**
     * Call this listener when we are connected
     */
    private OnConnectListener mConnected;
    
    /**
     * Call this listener when an unrecoverabe exception is raised
     */
    private OnExceptionListener mException;

    /**
     * call this listenser when read FW done
     * used for: 1--- alert update message in syncController
     *           2--- when OTA finished, refresh the new FW version to screen view
     */
    private OnFirmwareVersionListener mFirmware;
    /**
     * Try to reconnect every 3 secs
     */
    private static final int RETRY_DELAY = 3000;

    private  String mFirmwareVersion = null;
    private  String mSoftwareVersion = null;
	
	/**
	 * This binder is the bridge between the ImazeBTImpl and this Service
	 * @author Hugo
	 *
	 */
	public class LocalBinder extends Binder{
		
		/**
		 * Sets all the required callbacks
		 */
		public void initialize(OnDataReceivedListener dataReceived, OnConnectListener connect, OnExceptionListener exception, OnFirmwareVersionListener firmware){
			NevoBTService.this.initialize(dataReceived, connect, exception ,firmware);
		}
		
		/**
		 * @return the current connection state
		 */
		public boolean isConnected(String address){
			return mBluetoothGattMap.containsKey(address);
		}
		
	    /**
	     * @return true if no device is currently connected
	     */
		public boolean isDisconnected(){
			return NevoBTService.this.mBluetoothGattMap.isEmpty();
		}
		
		/**
		 * Connect to the given device (if possible)
		 */
		public void connect(String address){
			NevoBTService.this.autoConnect(address);
		}
		
		/**
		 * Disconnects to the given device (if possible)
		 */
		public void disconnect(String address){
			NevoBTService.this.disconnect(address);
		}
		
		/**
		 * Helps to kill this object
		 */
		public void destroy(){
			stopSelf();
			close();
		}

	    /**
	     * Checks if a device already covers the given service.
	     * @return the address of the connected device (if any) or an empty Optional if there's no device currently covering this service
	     */
		public Optional<String> isServiceConnected(UUID uuid){
			return NevoBTService.this.isServiceConnected(uuid);
		}
		
	    /**
	     * Checks if a device already covers on of the given services
	     * @return the address of the connected device (if any) or an empty Optional if there's no device currently covering this service
	     */
		public boolean isOneOfThosServiceConnected(List<UUID> uuids){
			for(UUID uuid : uuids) {
				if(NevoBTService.this.isServiceConnected(uuid).notEmpty()) return true;
			}
			
			return false;
		}
		
		/**
		 * Sends a request to the device that supports the given service (if any)
		 * @param deviceRequest
		 */
		public void sendRequest(SensorRequest deviceRequest){		
			NevoBTService.this.sendRequest(deviceRequest);
		}

        /**
         *
         * @return BLE firmware version
        */
        public String getFirmwareVersion()
        {
            return NevoBTService.this.getFirmwareVersion();
        }

        /**
         *
         * @return MCU software version
         */
        public String getSoftwareVersion()
        {
            return NevoBTService.this.getSoftwareVersion();
        }

        /**
         * Pings the currently attached device (if any) in order to check if it is connected
         */
        public void ping() { NevoBTService.this.ping(); }

	}

	/*
     * (non-Javadoc)
     * @see android.app.Service#onBind(android.content.Intent)
     */
	@Override
	public IBinder onBind(Intent intent) {
		Log.v(NevoBT.TAG,"ImazeBTService onBind() called");
		return new LocalBinder();
	}
    
	/*
	 * (non-Javadoc)
	 * @see android.app.Service#onUnbind(android.content.Intent)
	 */
    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
    	Log.v(NevoBT.TAG,"ImazeBTService onUnbind() called");
        close();
        return super.onUnbind(intent);
    }
 
	/**
	 * Initializes a reference to the local Bluetooth adapter.
	 * @param dataReceived - called when data is received
	 * @param connect - called when a device connects
	 * @param exception - called when an unrecoverable exception occurs
	 */
	private boolean initialize(OnDataReceivedListener dataReceived, OnConnectListener connect, OnExceptionListener exception,OnFirmwareVersionListener firmware) {
    	
    	mDataReceived = dataReceived;
    	
    	mConnected = connect;
    	
    	mException = exception;

        mFirmware = firmware;
    	
    	mQueuedMainThread = QueuedMainThreadHandler.getInstance(QueuedMainThreadHandler.QueueType.NevoBT);

    	BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
           if (bluetoothManager == null) {
                Log.e(NevoBT.TAG, "Unable to initialize BluetoothManager.");
                return false;
           }

 
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(NevoBT.TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }
 
        return true;
    }
	
	/**
	 * Automatically try to connect to this adress.
	 * It will try until it succeeds.
	 * @param address
	 */
	private void autoConnect(final String address){
		//All discoveries should be canceled before we try to connect
		mBluetoothAdapter.cancelDiscovery();
		
		//If it doesn't connect, we'll retry a bit later
		if(!connect(address)){
			Log.v(NevoBT.TAG, "Reschedueling a connection");
			new Timer().schedule(new TimerTask() {          
			    @Override
			    public void run() {
			    	//Time to retry !
			    	Log.v(NevoBT.TAG, "Retrying to connect");
			        autoConnect(address);     
			    }
			}, RETRY_DELAY);

		}
	}
 
    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     * If the device is unavailable, we'll connect to it as soon as possible.
     *
     * @param address The device address of the destination device.
     *
     * @return Return true if the connection is initiated successfully. The connection result
     *         is reported asynchronously through the
     *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     *         callback.
     */
	private boolean connect(final String address) {
        if (mBluetoothAdapter == null) {
            Log.w(NevoBT.TAG, "BluetoothAdapter not initialized");
            return false;
        }
 
        // if the device has a Gatt service, it means we are connected already first close it, and make a new connection
        if(mBluetoothGattMap.get(address)!=null)
        {
           	mBluetoothGattMap.get(address).close();        	
        	mBluetoothGattMap.remove(address);
        }
        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(NevoBT.TAG, "Device not found.  Unable to connect.");
            return false;
        }
        
        // We don't know if the device is available, so we try to connect to it
        //We should do this on the UI thread (for some reason)... http://stackoverflow.com/questions/6369287/accessing-ui-thread-handler-from-a-service
        mQueuedMainThread.post(new Runnable() {;
			@Override
			public void run() {
				Log.d(NevoBT.TAG, "Connecting to Gatt : "+address);
				device.connectGatt(NevoBTService.this, false, mGattCallback);
			}
		});


        return true;
    }
 
    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
	private void disconnect(final String address) {
        if (mBluetoothAdapter == null) {
            Log.w(NevoBT.TAG, "BluetoothAdapter not initialized");
            return;
        }
        //For some reason we have to do it on the UI thread...
        //But we don't do it in the Queued Handler, because we can't reliabily mQueuedMainThread.next(); on disconnect
        //Because a disconnection can come from a lot of things
        new Handler(Looper.getMainLooper()).post(new Runnable() {
			
			@Override
			public void run() {
				Log.i(NevoBT.TAG, "Disconnecting "+address);
				if(mBluetoothGattMap.containsKey(address)) 
				{
					mBluetoothGattMap.get(address).disconnect();
				}
			}
		});

    }
    
    /**
     * Implements callback methods for GATT events that the app cares about.  For example,
     * connection change and services discovered.
     */
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, int status, int newState) {
        	if(gatt == null || gatt.getDevice() == null || gatt.getDevice().getAddress() == null) {
        		//If the gatt is null, something's wrong. Let's just stop here.
            	Log.w(NevoBT.TAG,"mBluetoothGatt is null");
            	return;
            }
        	
        	final String address = gatt.getDevice().getAddress();
        	
            if (newState == BluetoothProfile.STATE_CONNECTED) {
            	mQueuedMainThread.next();
      		
            	//mBluetoothGattMap.put(address, gatt);
            	
                Log.i(NevoBT.TAG, "Connected to GATT server : "+ address);
                   
                // Attempts to discover services after successful connection.
                Log.v(NevoBT.TAG, "Attempting to start service discovery");
                mQueuedMainThread.post(new Runnable() {
                	@Override
            		public void run() {
            			Log.d(NevoBT.TAG, "Discovering services : "+ address);
            			if(gatt!=null) gatt.discoverServices();
            		}
                });
                return;
 
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            	
                Log.e(NevoBT.TAG, "Disconnected from GATT server : "+ address);
                
                if(mConnected!=null && gatt!=null) mConnected.onConnectionStateChanged(false,address);

                //close this server for next reconnect!!!
                if(gatt!=null) {gatt.close();}
                mBluetoothGattMap.remove(address);
                //we don't know why the Gatt server disconnected, so no need again connect, for example: BLE devices power off or go away               
                return;
            } else {
            	
            	Log.e(NevoBT.TAG, "Unknown state for "+ address);
            	//No matter what, if the device is not connected, we remove it from the list of connected devices
            	mBluetoothGattMap.remove(address);
            }
        }
 
        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, int status) {
        	mQueuedMainThread.next();
        	
        	if(gatt == null || gatt.getDevice() == null || gatt.getDevice().getAddress() == null) {
        		//If the gatt is null, something's wrong. Let's just stop here.
            	Log.w(NevoBT.TAG,"mBluetoothGatt is null");
            	return;
            }

        	final String address = gatt.getDevice().getAddress();
        	
        	Log.d(NevoBT.TAG, "Services discovered : "+address);
        	
        	//WARNING ! For some reasons, device.connectGatt(this, true, mGattCallback); will give us services with empty characteristics...
        	//Looks like the bluetooh layers crash, with a aclStateChangeCallback: Device is NULL  (at least on android 4.3)
        	
        	if(getSupportedGattServices(gatt).isEmpty()) Log.w(NevoBT.TAG, "No services discovered for : "+address);
        	else Log.v(NevoBT.TAG,  getSupportedGattServices(gatt).size() +  " services discovered for : "+address);
        	
        	//At least one characteristic should be chosen, or there's a problem
        	boolean characteristicChosen = false;
        	
        	for(BluetoothGattService service : getSupportedGattServices(gatt)){
        		       		      			
            	if(service.getCharacteristics().isEmpty()) Log.w(NevoBT.TAG, "No characteristic discovered for : "+service.getUuid());
            	else Log.v(NevoBT.TAG, service.getCharacteristics().size() + " characteristic discovered for : "+service.getUuid() );
            	
        		//Since it takes some time to connect to a device, maybe in the mean time we've just connected to another device with similar services.
        		//Let's check if there's an address connected to one of those services
        		Optional<String> device = isServiceConnected(service.getUuid());
        		//If yes, maybe it's this device address. If not, then we shouldn't connect this device, let's disconnect.
        		if(device.notEmpty()&&!device.get().equals(address)) {
                    Log.w(NevoBT.TAG, "disconnect the second BLE device (same service UUID,eg:  the 2nd. nevo): "+address);
        			disconnect(address);
        			return;
        		}
            	
            	//For each characteristics of each supported services, we'll try to get notified
            	for(final BluetoothGattCharacteristic characteristic : service.getCharacteristics()){
               		
    				final String uuid = characteristic.getUuid().toString();
                    //read firmware/software version
    				if(service.getUuid().toString().equals(GattAttributes.DEVICEINFO_UDID))
                    {
                        if(characteristic.getUuid().toString().equals(GattAttributes.DEVICEINFO_FIRMWARE_VERSION)
                           || characteristic.getUuid().toString().equals(GattAttributes.DEVICEINFO_SOFTWARE_VERSION))
                        {
                            mQueuedMainThread.post(new Runnable() {
                                @Override
                                public void run() {
                                    Log.v(NevoBT.TAG,"start read version: " + uuid);
                                    gatt.readCharacteristic(characteristic);
                                }
                            });
                        }
                    }

    				//Is this characteristic supported ?
    				Log.v(NevoBT.TAG,"Characteristic UUID:" + uuid);
    				if (GattAttributes.supportedBLECharacteristic(uuid))
    				{    
    					Log.i(NevoBT.TAG, "Activating supported characteristic : "+address+" "+uuid);
    					setCharacteristicNotification(gatt, characteristic, true);   
    					characteristicChosen = true;
	
    				}
    				
    				//For all the characteristics that needs to be initialised, we do so
    				if (GattAttributes.shouldInitBLECharacteristic(uuid)) {
                        mQueuedMainThread.post(new Runnable() {
                			@Override
                			public void run() {
                				if(gatt!=null) gatt.writeCharacteristic(GattAttributes.initBLECharacteristic(uuid, characteristic));
                			}
                        });
    				}
            	}
            }
        	
        	if(!characteristicChosen){
        		Log.w(NevoBT.TAG,"No characteristic chosen, maybe the bluetooth is unstable : "+address);
        		mException.onException(new BLEUnstableException());
        	}
            else
            {
                //here only connect one nevo, the first nevo by scan to find out
                mBluetoothGattMap.put(gatt.getDevice().getAddress(), gatt);
                if(mConnected!=null && gatt!=null) mConnected.onConnectionStateChanged(true,gatt.getDevice().getAddress());
            }
        	
        }
 
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
        	mQueuedMainThread.next();
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (UUID.fromString(GattAttributes.DEVICEINFO_FIRMWARE_VERSION).equals(characteristic.getUuid())){
                    mFirmwareVersion = StringUtils.newStringUsAscii(characteristic.getValue());
                    Log.i(NevoBT.TAG,"FIRMWARE VERSION **************** "+mFirmwareVersion);
                    mFirmware.firmwareVersionReceived(Constants.DfuFirmwareTypes.APPLICATION,mFirmwareVersion);
                }
                else if (UUID.fromString(GattAttributes.DEVICEINFO_SOFTWARE_VERSION).equals(characteristic.getUuid())){
                    mSoftwareVersion = StringUtils.newStringUsAscii(characteristic.getValue());
                    Log.i(NevoBT.TAG,"SOFTWARE VERSION **************** "+mSoftwareVersion);
                    mFirmware.firmwareVersionReceived(Constants.DfuFirmwareTypes.SOFTDEVICE,mSoftwareVersion);
                }
            }
        }
 
        @Override
        public void onCharacteristicChanged(final BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
        	dataReceived(characteristic, gatt.getDevice().getAddress());
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        	mQueuedMainThread.next();
        };

        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        	mQueuedMainThread.next();
        };
        
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        	mQueuedMainThread.next();
        };
        
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
        	mQueuedMainThread.next();
        };
        
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
        	mQueuedMainThread.next();
        };
        
    };
 
    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    private void close() {
        if(mQueuedMainThread!=null) mQueuedMainThread.clear();
        /*
         * perhapse unbindService and LocalBinder.destroy() both call it
         */
        if(mBluetoothGattMap!=null &&mBluetoothGattMap.isEmpty() == false)
        {
        for(BluetoothGatt b : mBluetoothGattMap.values()) b.close();
        mBluetoothGattMap.clear();        
        }
    }
    
    /**
     * Broadcast the data updates
     * @param characteristic
     */
    private void dataReceived(final BluetoothGattCharacteristic characteristic, final String address) {

    	SensorData data = DataFactory.fromBluetoothGattCharacteristic(characteristic, address);
        
        if(data!=null&&mDataReceived!=null) mDataReceived.onDataReceived(data);
    }
 
    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    //Not used, but kept for possible later use
    private void readCharacteristic(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || gatt == null) {
            Log.w(NevoBT.TAG, "BluetoothAdapter not initialized");
            return;
        }
        int charaProp = characteristic.getProperties();   
        
        Log.v(NevoBT.TAG, "characteristic.getProperties() is: " + charaProp);
        
		if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ)== BluetoothGattCharacteristic.PROPERTY_READ)
		{	
            mQueuedMainThread.post(new Runnable() {
    			@Override
    			public void run() {
    				 Log.v(NevoBT.TAG, "Reading characteristic");
    				if(gatt!=null) gatt.readCharacteristic(characteristic);
    			}
            });
		}
		

    }
 
    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled If true, enable notification.  False otherwise.
     */
    private void setCharacteristicNotification(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic,
                                              final boolean enabled) {
        if (mBluetoothAdapter == null || gatt == null) {
            Log.w(NevoBT.TAG, "BluetoothAdapter not initialized");
            return;
        }
        mQueuedMainThread.post(new Runnable() {
			@Override
			public void run() {
				if(gatt!=null) gatt.setCharacteristicNotification(characteristic, enabled);
		        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
					@Override
					public void run() {
						mQueuedMainThread.next();
					}
				},1000);
			}
        });
 
        final BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(GattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
        if(descriptor!=null){
        	descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mQueuedMainThread.post(new Runnable() {
    			@Override
    			public void run() {
    				if(gatt!=null) gatt.writeDescriptor(descriptor);
    			}
            });
        }

    }
 
    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    private List<BluetoothGattService> getSupportedGattServices(BluetoothGatt gatt) {
        if (gatt == null) return new ArrayList<BluetoothGattService>();
 
        return gatt.getServices();
    }
    
    /**
     * Checks if a device already covers the given service.
     * @param service the service that we are looking up. We'll try to see if a connected device provides this service.
     * @return the address of the connected device (if any) or an empty Optional if there's no device currently covering this service
     */
    private Optional<String> isServiceConnected(UUID service) {
    	if(mBluetoothGattMap == null || mBluetoothGattMap.isEmpty())  return new Optional<String>();
    	
    	Collection<BluetoothGatt>  gatts = mBluetoothGattMap.values();
    	
		for(BluetoothGatt gatt : gatts)
	    {
			
			for(BluetoothGattService ser : gatt.getServices())
			{
				if(ser.getUuid().equals(service) 
					&& GattAttributes.supportedBLEService(service.toString()))
				{
					return new Optional<String>(gatt.getDevice().getAddress());
				}
			}
	    }
    	
    	return new Optional<String>();
	}
    
    private void sendRequest(SensorRequest deviceRequest) {
		UUID serviceUUID = deviceRequest.getServiceUUID();
		UUID characteristicUUID = deviceRequest.getInputCharacteristicUUID();
		byte[] rawData = deviceRequest.getRawData();
		byte[][] rawDatas = deviceRequest.getRawDataEx();
			
    	if(mBluetoothGattMap == null || mBluetoothGattMap.isEmpty())  {
    		Log.w(NevoBT.TAG, "Send failed. No device connected" );
    		return;
    	}
    	
    	boolean sent = false;
		
		for(BluetoothGatt gatt : mBluetoothGattMap.values())
	    {
			//For each connected device, we'll see if they have the right service
			BluetoothGattService service = gatt.getService(serviceUUID);
			
			if(service!=null) {
				BluetoothGattCharacteristic characteristic = service.getCharacteristic(characteristicUUID);
				if(characteristic!=null) {
					//Now we've found the right characteristic, we modify it, then send it to the device
					if(rawDatas != null)
					{
						  for(byte[] data : rawDatas)
						  {
							  Log.i(NevoBT.TAG, "Send requestEx "+ new String(Hex.encodeHex(data)));
							  characteristic.setValue(data);		
							  gatt.writeCharacteristic(characteristic);
						  }
					}
					else
					{
						if(rawData != null)
						{
							Log.i(NevoBT.TAG, "Send request "+ new String(Hex.encodeHex(rawData)));
							characteristic.setValue(rawData);
                            if(characteristicUUID.equals(UUID.fromString(GattAttributes.NEVO_OTA_CALLBACK_CHARACTERISTIC)))
                            {
                                //enable response for write ota control command
                                characteristic.setWriteType(BluetoothGattCharacteristic.PROPERTY_WRITE);
                                gatt.writeCharacteristic(characteristic);
                            }
                            else
							    gatt.writeCharacteristic(characteristic);
						}
					}
					
					sent=true;
				}
			}
	    }
		
		if(!sent) Log.w(NevoBT.TAG, "Send failed. No device have the right service and characteristic" );
		
    }

    /**
     * This function will send a read request to the device in order to see if it is still active
     * @return
     */
    private void ping()
    {

        UUID serviceUUID = UUID.fromString(GattAttributes.DEVICEINFO_UDID);
        UUID characteristicUUID = UUID.fromString(GattAttributes.DEVICEINFO_FIRMWARE_VERSION);

        if(mBluetoothGattMap == null || mBluetoothGattMap.isEmpty())  {
            Log.w(NevoBT.TAG, "Get failed. No device connected" );
            return;
        }

        boolean sent = false;

        for(BluetoothGatt gatt : mBluetoothGattMap.values())
        {
            //For each connected device, we'll see if they have the right service
            BluetoothGattService service = gatt.getService(serviceUUID);

            if(service!=null) {
                BluetoothGattCharacteristic characteristic = service.getCharacteristic(characteristicUUID);
                if(characteristic!=null) {
                    //Now we've found the right characteristic, we modify it, then send it to the device
                    readCharacteristic(gatt,characteristic);

                    sent=true;
                }
            }
        }

        if(!sent) Log.w(NevoBT.TAG, "Get failed. No device have the right service and characteristic" );
    }

    /**
     *
     * @return BLE firmware version
     */
    private String getFirmwareVersion()
    {
        return mFirmwareVersion;
    }

    /**
     *
     * @return MCU software version
     */
    private String getSoftwareVersion()
    {
        return mSoftwareVersion;
    }

}
