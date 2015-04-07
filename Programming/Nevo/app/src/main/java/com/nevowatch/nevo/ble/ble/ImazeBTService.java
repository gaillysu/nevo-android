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

import com.nevowatch.nevo.ble.kernel.BLEUnstableException;
import com.nevowatch.nevo.ble.kernel.ImazeBT;
import com.nevowatch.nevo.ble.kernel.OnConnectListener;
import com.nevowatch.nevo.ble.kernel.OnDataReceivedListener;
import com.nevowatch.nevo.ble.kernel.OnDisconnectListener;
import com.nevowatch.nevo.ble.kernel.OnExceptionListener;
import com.nevowatch.nevo.ble.model.packet.DataFactory;
import com.nevowatch.nevo.ble.model.packet.SensorData;
import com.nevowatch.nevo.ble.model.request.SensorRequest;
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
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class ImazeBTService extends Service {
    
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
     * Call this listener when we are disconnected
     */
    private OnDisconnectListener mDisconnected;
    
    /**
     * Call this listener when an unrecoverabe exception is raised
     */
    private OnExceptionListener mException;
    
    /**
     * Try to reconnect every 3 secs
     */
    private static final int RETRY_DELAY = 3000;
	
	/**
	 * This binder is the bridge between the ImazeBTImpl and this Service
	 * @author Hugo
	 *
	 */
	public class LocalBinder extends Binder{
		
		/**
		 * Sets all the required callbacks
		 * @param mConnect 
		 */
		public void initialize(OnDataReceivedListener dataReceived, OnConnectListener connect, OnDisconnectListener disconnected, OnExceptionListener exception){
			ImazeBTService.this.initialize(dataReceived, connect , disconnected, exception);
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
			return ImazeBTService.this.mBluetoothGattMap.isEmpty();
		}
		
		/**
		 * Connect to the given device (if possible)
		 */
		public void connect(String address){
			ImazeBTService.this.autoConnect(address);
		}
		
		/**
		 * Disconnects to the given device (if possible)
		 */
		public void disconnect(String address){
			ImazeBTService.this.disconnect(address);
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
	     * @param service the service that we are looking up. We'll try to see if a connected device provides this service.
	     * @return the address of the connected device (if any) or an empty Optional if there's no device currently covering this service
	     */
		public Optional<String> isServiceConnected(UUID uuid){
			return ImazeBTService.this.isServiceConnected(uuid);
		}
		
	    /**
	     * Checks if a device already covers on of the given services
	     * @param service the service that we are looking up. We'll try to see if a connected device provides this service.
	     * @return the address of the connected device (if any) or an empty Optional if there's no device currently covering this service
	     */
		public boolean isOneOfThosServiceConnected(List<UUID> uuids){
			for(UUID uuid : uuids) {
				if(ImazeBTService.this.isServiceConnected(uuid).notEmpty()) return true;
			}
			
			return false;
		}
		
		/**
		 * Sends a request to the device that supports the given service (if any)
		 * @param deviceRequest
		 */
		public void sendRequest(SensorRequest deviceRequest){		
			ImazeBTService.this.sendRequest(deviceRequest);
		}

	}

	/*
     * (non-Javadoc)
     * @see android.app.Service#onBind(android.content.Intent)
     */
	@Override
	public IBinder onBind(Intent intent) {
		Log.v(ImazeBT.TAG,"ImazeBTService onBind() called");
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
    	Log.v(ImazeBT.TAG,"ImazeBTService onUnbind() called");
        close();
        return super.onUnbind(intent);
    }
 
	/**
	 * Initializes a reference to the local Bluetooth adapter.
	 * @param disconnected - called when a device is disconnected 
	 * @param dataReceived - called when data is received
	 * @param connect - called when a device connects
	 * @param exception - called when an unrecoverable exception occurs
	 */
	private boolean initialize(OnDataReceivedListener dataReceived, OnConnectListener connect, OnDisconnectListener disconnected, OnExceptionListener exception) {
    	
    	mDataReceived = dataReceived;
    	
    	mConnected = connect;
    	
    	mDisconnected = disconnected;
    	
    	mException = exception;
    	
    	mQueuedMainThread = QueuedMainThreadHandler.getInstance();

    	BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
           if (bluetoothManager == null) {
                Log.e(ImazeBT.TAG, "Unable to initialize BluetoothManager.");
                return false;
           }

 
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(ImazeBT.TAG, "Unable to obtain a BluetoothAdapter.");
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
			Log.v(ImazeBT.TAG, "Reschedueling a connection");
			new Timer().schedule(new TimerTask() {          
			    @Override
			    public void run() {
			    	//Time to retry !
			    	Log.v(ImazeBT.TAG, "Retrying to connect");
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
            Log.w(ImazeBT.TAG, "BluetoothAdapter not initialized");
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
            Log.w(ImazeBT.TAG, "Device not found.  Unable to connect.");
            return false;
        }
        
        // We don't know if the device is available, so we try to connect to it
        //We should do this on the UI thread (for some reason)... http://stackoverflow.com/questions/6369287/accessing-ui-thread-handler-from-a-service
        mQueuedMainThread.post(new Runnable() {;
			@Override
			public void run() {
				Log.d(ImazeBT.TAG, "Connecting to Gatt : "+address);
				device.connectGatt(ImazeBTService.this, false, mGattCallback);
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
            Log.w(ImazeBT.TAG, "BluetoothAdapter not initialized");
            return;
        }
        //For some reason we have to do it on the UI thread...
        //But we don't do it in the Queued Handler, because we can't reliabily mQueuedMainThread.next(); on disconnect
        //Because a disconnection can come from a lot of things
        new Handler(Looper.getMainLooper()).post(new Runnable() {
			
			@Override
			public void run() {
				Log.i(ImazeBT.TAG, "Disconnecting "+address);
				if(mBluetoothGattMap.containsKey(address)) 
				{
					mBluetoothGattMap.get(address).disconnect();
				}
			}
		});

    }
    
    //TODO WARNING ! THIS CALLBACK IS NOT CALLED IF THE DEVICE'S BLUTOOTH IS TURNED OFF... (maybe a bluetooth manager can register callbacks to know that ??)
    /**
     * Implements callback methods for GATT events that the app cares about.  For example,
     * connection change and services discovered.
     */
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, int status, int newState) {
        	if(gatt == null || gatt.getDevice() == null || gatt.getDevice().getAddress() == null) {
        		//If the gatt is null, something's wrong. Let's just stop here.
            	Log.w(ImazeBT.TAG,"mBluetoothGatt is null");
            	return;
            }
        	
        	final String address = gatt.getDevice().getAddress();
        	
            if (newState == BluetoothProfile.STATE_CONNECTED) {
            	mQueuedMainThread.next();
      		
            	mBluetoothGattMap.put(address, gatt);
            	
                Log.i(ImazeBT.TAG, "Connected to GATT server : "+ address);
                   
                // Attempts to discover services after successful connection.
                Log.v(ImazeBT.TAG, "Attempting to start service discovery");
                mQueuedMainThread.post(new Runnable() {
                	@Override
            		public void run() {
            			Log.d(ImazeBT.TAG, "Discovering services : "+ address);
            			if(gatt!=null) gatt.discoverServices();
            		}
                });
                return;
 
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            	
                Log.e(ImazeBT.TAG, "Disconnected from GATT server : "+ address);
                
                if(mDisconnected!=null && gatt!=null) mDisconnected.onDisconnect(gatt.getDevice().getName());

                //close this server for next reconnect!!!
                if(gatt!=null) gatt.close();
                mBluetoothGattMap.remove(address);
                //we don't know why the Gatt server disconnected, so no need again connect, for example: BLE devices power off or go away               
                return;
            } else {
            	
            	Log.e(ImazeBT.TAG, "Unknown state for "+ address);
            	//No matter what, if the device is not connected, we remove it from the list of connected devices
            	mBluetoothGattMap.remove(address);
            }
        }
 
        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, int status) {
        	mQueuedMainThread.next();
        	
        	if(gatt == null || gatt.getDevice() == null || gatt.getDevice().getAddress() == null) {
        		//If the gatt is null, something's wrong. Let's just stop here.
            	Log.w(ImazeBT.TAG,"mBluetoothGatt is null");
            	return;
            }
        	
            
            if(mConnected!=null && gatt!=null) mConnected.onConnect(gatt.getDevice().getAddress());
            
        	
        	final String address = gatt.getDevice().getAddress();
        	
        	Log.d(ImazeBT.TAG, "Services discovered : "+address);
        	
        	//WARNING ! For some reasons, device.connectGatt(this, true, mGattCallback); will give us services with empty characteristics...
        	//Looks like the bluetooh layers crash, with a aclStateChangeCallback: Device is NULL  (at least on android 4.3)
        	
        	if(getSupportedGattServices(gatt).isEmpty()) Log.w(ImazeBT.TAG, "No services discovered for : "+address);
        	else Log.v(ImazeBT.TAG,  getSupportedGattServices(gatt).size() +  " services discovered for : "+address);
        	
        	//At least one characteristic should be chosen, or there's a problem
        	boolean characteristicChosen = false;
        	
        	for(BluetoothGattService service : getSupportedGattServices(gatt)){
        		       		      			
            	if(service.getCharacteristics().isEmpty()) Log.w(ImazeBT.TAG, "No characteristic discovered for : "+service.getUuid());
            	else Log.v(ImazeBT.TAG, service.getCharacteristics().size() + " characteristic discovered for : "+service.getUuid() );
            	
        		//Since it takes some time to connect to a device, maybe in the mean time we've just connected to another device with similar services.
        		//Let's check if there's an address connected to one of those services
        		Optional<String> device = isServiceConnected(service.getUuid());
        		//If yes, maybe it's this device address. If not, then we shouldn't connect this device, let's disconnect.
        		if(device.notEmpty()&&!device.get().equals(address)) {
        			disconnect(address);
        			return;
        		}
            	
            	//For each characteristics of each supported services, we'll try to get notified
            	for(final BluetoothGattCharacteristic characteristic : service.getCharacteristics()){
               		
    				final String uuid = characteristic.getUuid().toString();
    				
    				//Is this characteristic supported ?
    				Log.v(ImazeBT.TAG,"Characteristic UUID:" + uuid);
    				if (GattAttributes.supportedBLECharacteristic(uuid))
    				{    
    					Log.i(ImazeBT.TAG, "Activating supported characteristic : "+address+" "+uuid);
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
        		Log.w(ImazeBT.TAG,"No characteristic chosen, maybe the bluetooth is unstable : "+address);
        		mException.onException(new BLEUnstableException());
        	}
            
        	
        }
 
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
        	mQueuedMainThread.next();
            if (status == BluetoothGatt.GATT_SUCCESS) {
            	dataReceived(characteristic, gatt.getDevice().getAddress());
            }
        }
 
        @Override
        public void onCharacteristicChanged(final BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
        	dataReceived(characteristic, gatt.getDevice().getAddress());

        	if(characteristic.getUuid().toString().equals(GattAttributes.RUNNING_SPEED_AND_CADENCE_STATUS_CHARACTERISTIC))
        	{
        		final BluetoothGattService runningservice = gatt.getService(UUID.fromString(GattAttributes.RUNNING_SPEED_AND_CADENCE_SERVICE));
        		final BluetoothGattCharacteristic  runningMeasureCharacteristic = runningservice.getCharacteristic(UUID.fromString(GattAttributes.RUNNING_SPEED_AND_CADENCE_CHARACTERISTIC));
        		mQueuedMainThread.post(new Runnable() {
        			@Override
        			public void run() {
        				ImazeBTService.this.setCharacteristicNotification(gatt, runningMeasureCharacteristic, true);
        			}
        		});
			}

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
    /*private void readCharacteristic(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || gatt == null) {
            Log.w(ImazeBT.TAG, "BluetoothAdapter not initialized");
            return;
        }
        int charaProp = characteristic.getProperties();   
        
        Log.v(ImazeBT.TAG, "characteristic.getProperties() is: " + charaProp);
        
		if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ)== BluetoothGattCharacteristic.PROPERTY_READ)
		{	
            mQueuedMainThread.post(new Runnable() {
    			@Override
    			public void run() {
    				 Log.v(ImazeBT.TAG, "Reading characteristic");
    				if(gatt!=null) gatt.readCharacteristic(characteristic);
    			}
            });
		}
		

    }*/
 
    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled If true, enable notification.  False otherwise.
     */
    private void setCharacteristicNotification(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic,
                                              final boolean enabled) {
        if (mBluetoothAdapter == null || gatt == null) {
            Log.w(ImazeBT.TAG, "BluetoothAdapter not initialized");
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
    		Log.w(ImazeBT.TAG, "Send failed. No device connected" );
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
							  Log.v(ImazeBT.TAG, "Send requestEx "+ new String(Hex.encodeHex(data)));
							  characteristic.setValue(data);		
							  gatt.writeCharacteristic(characteristic);
						  }
					}
					else
					{
						if(rawData != null)
						{
							Log.v(ImazeBT.TAG, "Send request "+ new String(Hex.encodeHex(rawData)));
							characteristic.setValue(rawData);					
							gatt.writeCharacteristic(characteristic);
						}
					}
					
					sent=true;
				}
			}
	    }
		
		if(!sent) Log.w(ImazeBT.TAG, "Send failed. No device have the right service and characteristic" );
		
    }

}