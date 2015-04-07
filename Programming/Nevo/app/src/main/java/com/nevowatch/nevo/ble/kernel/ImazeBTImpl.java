/*
 * COPYRIGHT (C) 2014 MED Enterprises LTD. All Rights Reserved.
 */
package com.nevowatch.nevo.ble.kernel;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import com.nevowatch.nevo.ble.ble.GattAttributes;
import com.nevowatch.nevo.ble.ble.ImazeBTService;
import com.nevowatch.nevo.ble.ble.SupportedService;
import com.nevowatch.nevo.ble.model.packet.SensorData;
import com.nevowatch.nevo.ble.model.request.SensorRequest;
import com.nevowatch.nevo.ble.util.Optional;


@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
/*package*/ class ImazeBTImpl implements ImazeBT {
	/*
	 * Here's how it works under the hood.
	 * ImazeBTImpl is our Kernerl.
	 * Each time it finds a compatible device (After a successful scan) it will create a Service to connect to it.
	 * It can connect to a certain number of Devices at the same time
	 * The communication is as follow :
	 * When the Kernel needs to call a function in the service, it does it this way :
	 * ImazeBTImpl -- ImazeBTService.LocalBinder --> ImazeBTService
	 * 
	 * When the Service need to send data back to the kernel, it calls a callback (That was given to him through the binder)
	 * For example
	 * ImazeBTService -- OnDataReceivedListener --> ImazeBTImpl
	 * 
	 * In this whole package, all the *Impl classes, are Package wide classes and all the callable functions are called through an interface
	 * Also, instanciation is only done Through Builders
	 */
	
	/**
	 * Bluetooth main manager
	 */
	private BluetoothAdapter mBluetoothAdapter;

	/**
	 * The current context, used Only to bind and unbind services
	 */
	private Context mContext;

	/**
	 * The list of callbacks to call when the data is updated
	 */
	private List<OnDataReceivedListener> mCallbacks = new ArrayList<OnDataReceivedListener>();
	
	/**
	 * The callback to call when a device is connected
	 */
	private OnConnectListener mConnectListener;
	
	/**
	 * The callback to call when a device is disconnected
	 */
	private OnDisconnectListener mDisconnectListener;
	
	/**
	 * The callback to call when an unrecoverable exception is raised
	 */
	private OnExceptionListener mExceptionListener;
	
	/**
	 * The list of currently binded services.
	 * Warning though, alway check they haven't stopped
	 */
	private ImazeBTService.LocalBinder mCurrentService;
	
	/**
	 * The list of current service connection
	 */
	private ServiceConnection mCurrentServiceConnection;
	
	/**
	 *  Stops scanning after 10 seconds.
	 */
    private static final long SCAN_PERIOD = 10000;
    
    /*
	 *  here use one List to save the scaned devices's MAC address
	 */
    private List<String> mPreviousAddress  = new ArrayList<String>();
	
	/*
	 * save the supported BLE service,avoid connect the same service with the same model BLE device
	 * more sensors, such as heart rate/ power/ combo,  for every model sensor ,only one device can connect
	 */
    private List<SupportedService> mSupportServicelist = new ArrayList<SupportedService>();
	
    private String saveAddress;
    private String PREF_NAME = "NevoPrefs";
	private String SAVE_MAC_ADDRESS = "savemacaddress";
	private Timer mAutoReconnectTimer = null;
    private int  mTimerIndex = 0;
    private final static int[] mReConnectTimerPattern = new int[]{10000,10000,10000,60000,120000,240000,3600000};
    /**
     * Simple constructor
     * @param context
     * @throws BLENotSupportedException
     * @throws BluetoothDisabledException 
     */
	public ImazeBTImpl(Context context){
		this.mContext = context;
		
		try {
			initBluetoothAdapter();
		} catch (BLENotSupportedException e) {
			if(mExceptionListener!=null) mExceptionListener.onException(e);
		} catch (BluetoothDisabledException e) {
			if(mExceptionListener!=null) mExceptionListener.onException(e);
		}
	}
	
	/*
	 * Functions coming from the interface
	 */
	
	/*
	 * (non-Javadoc)
	 * @see fr.imaze.sdk.ImazeBT#addCallback(fr.imaze.sdk.OnDataReceivedListener)
	 */
	@Override
	public void addCallback(OnDataReceivedListener callback) {
		mCallbacks.add(callback);
	}
	
	/*
	 * (non-Javadoc)
	 * @see fr.imaze.sdk.ImazeBT#removeCallback(fr.imaze.sdk.OnDataReceivedListener)
	 */
	@Override
	public void removeCallback(OnDataReceivedListener callback) {
		mCallbacks.remove(callback);
	}

	/*
	 * (non-Javadoc)
	 * @see fr.imaze.sdk.ImazeBT#startScan(java.util.UUID)
	 */
	@Override
	synchronized public void startScan(List<SupportedService> servicelist) throws BLENotSupportedException, BluetoothDisabledException{
		
		mSupportServicelist = servicelist;
		//We check if bluetooth is enabled and/or if the device isn't ble capable
		try {
			initBluetoothAdapter();
		} catch (BLENotSupportedException e) {
			if(mExceptionListener!=null) mExceptionListener.onException(e);
			throw e;
		} catch (BluetoothDisabledException e) {
			if(mExceptionListener!=null) mExceptionListener.onException(e);
			throw e;
		}

		
        //For some reason we have to do it on the UI thread...
        new Handler(Looper.getMainLooper()).post(new Runnable() {
			
			@Override
			public void run() {
				/*
				 * firstly remove all saved devices
				 */
				mPreviousAddress.clear();
				//We start a scan
				if(mBluetoothAdapter!=null) mBluetoothAdapter.startLeScan(mLeScanCallback);
				
		        // Stops scanning after a pre-defined scan period.
		        new Handler().postDelayed(new Runnable() {
		            @Override
		            public void run() {
		            	if(mBluetoothAdapter!=null) mBluetoothAdapter.stopLeScan(mLeScanCallback);
		            	Log.v(TAG,"stopLeScan");
		            }
		        }, SCAN_PERIOD);
				
			}
		});

	}

	/*
	 * (non-Javadoc)
	 * @see fr.imaze.sdk.kernel.ImazeBT#sendRequest(fr.imaze.sdk.model.request.SensorRequest, fr.imaze.sdk.ble.SupportedService)
	 */
	@Override
	public void sendRequest(SensorRequest request) {
		if(mCurrentService!=null) {
			mCurrentService.sendRequest(request);
		} else {
			 Log.w(ImazeBT.TAG, "Send failed. Service not started" );
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see fr.imaze.sdk.ImazeBT#disconnect(java.lang.String)
	 */
	@Override
	public void disconnect(Optional<String> peripheralAdress) {
		//Two cases :
		if(peripheralAdress!=null&&peripheralAdress.notEmpty()){
			//Either the adress is not null, then we disconnect this adress only
			if(mCurrentService!=null
				&& mCurrentService.isConnected(peripheralAdress.get()) )
			{
				mCurrentService.disconnect(peripheralAdress.get());
			}
		} else {
			killService();
			if(mBluetoothAdapter!=null) mBluetoothAdapter.stopLeScan(mLeScanCallback);
		}
		
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see fr.imaze.sdk.kernel.ImazeBT#isDisconnected()
	 */
	public  boolean isDisconnected()
	{
		if(mCurrentService == null)  return true;
		
		if(mCurrentService.isDisconnected()) return true;
		
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see fr.imaze.sdk.kernel.ImazeBT#getAddressByServiceType(fr.imaze.sdk.ble.SupportedService)
	 */
	public Optional<String> getAddressByServiceType(SupportedService which)
	{
		if(mCurrentService==null) return new Optional<String>();
		
		String uuid = GattAttributes.TransferSupportedService2UUID(which);		
		UUID uuidd = UUID.fromString(uuid);
		
		return mCurrentService.isServiceConnected(uuidd);
	}
	
	/*
	 * (non-Javadoc)
	 * @see fr.imaze.sdk.ImazeBT#disconnectCallback(fr.imaze.sdk.OnDisconnectListener)
	 */
	@Override
	public void connectCallback(OnConnectListener callback) {
		mConnectListener = callback;
	}
	
	/*
	 * (non-Javadoc)
	 * @see fr.imaze.sdk.ImazeBT#disconnectCallback(fr.imaze.sdk.OnDisconnectListener)
	 */
	@Override
	public void disconnectCallback(OnDisconnectListener callback) {
		mDisconnectListener = callback;
	}
	
	/*
	 * (non-Javadoc)
	 * @see fr.imaze.sdk.kernel.ImazeBT#exceptionCallback(fr.imaze.sdk.kernel.OnExceptionListener)
	 */
	@Override
	public void exceptionCallback(OnExceptionListener callback) {
		mExceptionListener = callback;
		
	}
	
	/*
	 * END of the Functions coming from the interface
	 */
	
	/*
	 * killService() , will close all connected BLE, and destory the BT Service
	 * so be careful call this function,  Imaze Zen and Imaze fitness both Bind BT Service
	 * perhaps, we should keep BT service always running backgrand!
	 */
	private void killService(){
		
		try{
		
		// when this Service Class is bound,	unbindService will call Service.onDestroy()	
		// so  no need call mCurrentService.destroy();,but you should redo Service.onUnbind() 
		// to close the BluetoothGatt 
			
		//Discovery should be canceled if we really want to kill the service
		initBluetoothAdapter().cancelDiscovery();
			
		if(mCurrentServiceConnection!=null) {
			mContext.unbindService(mCurrentServiceConnection);
			mCurrentServiceConnection=null;
		}
			
		//Or it is null, so we disconnect all of them
		if(mCurrentService!=null) {
			mCurrentService.destroy();
			mCurrentService=null;
		}

		
		} catch ( Throwable t) {
			t.printStackTrace();
		}
		
	}
	
	/**
	 * This listener is called when new data is received
	 */
	private OnDataReceivedListener mDataReceived = new OnDataReceivedListener() {
		
		@Override
		public void onDataReceived(final SensorData data) {
			//Callback are usually called on the main thread
			new Handler(Looper.getMainLooper()).post(new Runnable() {
				
				@Override
				public void run() {
					//Data just arrived, we send it to all the appropriate callbacks
					for(OnDataReceivedListener callback : mCallbacks){
						try{
							callback.onDataReceived(data);
						} catch (Throwable t){
							t.printStackTrace();
						}
					}
					
				}
			});
		}
	};
	
	/**
	 * This listener is called when the device is connected
	 */
	private OnConnectListener mConnect = new OnConnectListener() {
		
		@Override
		public void onConnect(final String peripheralAdress) {
			//A device just disconnected, let's call the callbacks
			//Callback are usually called on the main thread
			new Handler(Looper.getMainLooper()).post(new Runnable() {
				
				@Override
				public void run() {
					try{
						//Then call the disconnect callback
						setSaveAddress(peripheralAdress);
						if(mConnectListener!=null) mConnectListener.onConnect(peripheralAdress);
					} catch (Throwable t){
						t.printStackTrace();
					}
				}
			});
		}
	};
	
	/**
	 * This listener is called when the device is disconnected
	 */
	private OnDisconnectListener mDisconnect = new OnDisconnectListener() {
		
		@Override
		public void onDisconnect(final String peripheralAdress) {
			//A device just disconnected, let's call the callbacks
			//Callback are usually called on the main thread
			new Handler(Looper.getMainLooper()).post(new Runnable() {
				
				@Override
				public void run() {
					try{
						//Then call the disconnect callback
						if(mDisconnectListener!=null) mDisconnectListener.onDisconnect(peripheralAdress);
					} catch (Throwable t){
						t.printStackTrace();
					}
				}
			});
		}
	};
	
	/**
	 * This listener is called when an unrecoverable exception is raised
	 */
	private OnExceptionListener mException = new OnExceptionListener() {
		
		@Override
		public void onException(final Exception e) {
			//An exception have been raised, let's call the callbacks
			//Callback are usually called on the main thread
			new Handler(Looper.getMainLooper()).post(new Runnable() {
				
				@Override
				public void run() {
					try{
						//Then call the disconnect callback
						if(mExceptionListener!=null) mExceptionListener.onException(e);
					} catch (Throwable t){
						t.printStackTrace();
					}
				}
			});
		}
	};

	/**
	 *  Device scan callback.This callback is called for all devices founds by the scanner
	 */
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
		
		/*
		 * (non-Javadoc)
		 * @see android.bluetooth.BluetoothAdapter.LeScanCallback#onLeScan(android.bluetooth.BluetoothDevice, int, byte[])
		 */
		@Override
		public void onLeScan(final BluetoothDevice device, int rssi,
				byte[] scanRecord) {
			final String deviceAddress = device.getAddress();

			if(mPreviousAddress.contains(deviceAddress)){				
				//We are in a case, Due to this issue : http://stackoverflow.com/questions/19502853/android-4-3-ble-filtering-behaviour-of-startlescan
				//Where the callback has been called twice for the same address
				//We should not do anything
			} else {
				//For each alloweds service, we should have one and only one device.
				//And it should be active at the moment
			
				//We check if the device is not already connected
				boolean alreadyConnected = ImazeBTImpl.this.isAlreadyConnected(deviceAddress);

			
				//We will browse the advertised UUIDs to check if one of them correspond to a supported service
				List<UUID> advertisedUUIDs = parseUUIDs(scanRecord);
				for(UUID u : advertisedUUIDs){
					Log.v(TAG, deviceAddress+" advertises "+u.toString());
				}
				//The address shouldn't be previously connected, no other device should support this service and it should be an allowed service (for this scan at least)
				//Also if a pairing is known to be needed, It should have already been paired : !GattAttributes.shouldPairBeforeUse(advertisedUUIDs) || (GattAttributes.shouldPairBeforeUse(advertisedUUIDs) && device.getBondState()==BluetoothDevice.BOND_BONDED)
				// Either : No need to pair before use Or : (Need to pair before use and we are actually paired)
				if (!alreadyConnected 
						&& (mCurrentService==null || !mCurrentService.isOneOfThosServiceConnected(advertisedUUIDs))
						&& !GattAttributes.supportedBLEServiceByEnum(advertisedUUIDs, mSupportServicelist).isEmpty() 
					 /* && ( !GattAttributes.shouldPairBeforeUse(advertisedUUIDs) || (GattAttributes.shouldPairBeforeUse(advertisedUUIDs) && device.getBondState()==BluetoothDevice.BOND_BONDED) )*/ )
				   {		
					
					Log.d(TAG, "Device "+deviceAddress+" found to support service : "+GattAttributes.supportedBLEServiceByEnum(advertisedUUIDs, mSupportServicelist).get(0));
					
					//If yes, let's bind this device !
					if(mCurrentService == null)
						bindNewService(deviceAddress);
					else
					{	
						mCurrentService.connect(deviceAddress);
					}
					
				}
			}
			if(!mPreviousAddress.contains(deviceAddress))	
			{
				mPreviousAddress.add(deviceAddress);
			}
		}
	};
	
	private boolean isAlreadyConnected(String deviceAddress) {
		//If current service isn't null
		return (mCurrentService!=null
			//And it is still binded
			&& mCurrentService.pingBinder()
			//And the device is still connected
			&& mCurrentService.isConnected(deviceAddress)
			//And the given device address is not null
			&& deviceAddress!=null
			); 
			//Congrats ! No need to connect, the device is already connected !
	}
	
	/**
	 * This function will create a new Service (if no service currently exists)
	 * @param deviceAddress
	 */
	private void bindNewService(final String deviceAddress) {
		Log.v(TAG,"start bindNewService by " + deviceAddress);
		//We will create a Service that will handle the actual Bluetooth low level job
		Intent intent = new Intent(mContext,
				ImazeBTService.class);
		
		//This object will be the bridge between this object and the Service
		//It is used to retreive the binder and unbind the service
		mCurrentServiceConnection = new ServiceConnection() {
			
			@Override
			public void onServiceDisconnected(ComponentName name) {
				Log.v(ImazeBT.TAG, name+" Service disconnected");
			}
			
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				Log.v(ImazeBT.TAG, name+" Service connected");
				
				//If we had this service already connected, we disconnect it
				//here comment by gaillysu, should not call disconnect()
				//disconnect(deviceAddress);
				
				//This object is the bridge to get informations and control the service
				mCurrentService = (ImazeBTService.LocalBinder) service;
				
				//We launch a conenction to the given device
				mCurrentService.initialize( mDataReceived, mConnect, mDisconnect, mException);
				//now connect this device
				mCurrentService.connect(deviceAddress);
			}
		};

		//We start the actual binding
		//Note that the service will restart as long as it is binded, because we have set : Activity.BIND_AUTO_CREATE
		mContext.bindService(intent,mCurrentServiceConnection,Activity.BIND_AUTO_CREATE);
		Log.v(ImazeBT.TAG,"mContext.bindService");
	}
	
	private BluetoothAdapter initBluetoothAdapter() throws BLENotSupportedException, BluetoothDisabledException {
		//If BLE is not supported, we throw an error
		if (!mContext.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE)) {
			throw new BLENotSupportedException(); 
		}

		// Initializes a Bluetooth adapter. For API level 18 and above, get a
		// reference to
		// BluetoothAdapter through BluetoothManager.
		mBluetoothAdapter = ((BluetoothManager) mContext
				.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();

		if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
		    throw new BluetoothDisabledException();
		}
		
		// Checks if Bluetooth is supported on the device.
		if (mBluetoothAdapter == null) {
			throw new BLENotSupportedException(); 
		}
		
		return mBluetoothAdapter;
	}
	
	/**
	 * Set the current context. Useful if we killed the parent activity
	 * @param ctx
	 */
	/*package*/ void setContext(Context ctx){
		this.mContext = ctx;
	}
	
	/*
	 * Util Functions
	 */
	
	/**
	 * This function will help us uncrypt the advertise Data and turn them into readable UUIDs
	 * @param advertisedData
	 * @return a list of UUIDs
	 */
	private List<UUID> parseUUIDs(final byte[] advertisedData) {
	    List<UUID> uuids = new ArrayList<UUID>();

	    int offset = 0;
	    while (offset < (advertisedData.length - 2)) {
	        int len = advertisedData[offset++];
	        if (len == 0)
	            break;

	        int type = advertisedData[offset++];
	        switch (type) {
	        case 0x02: // Partial list of 16-bit UUIDs
	        case 0x03: // Complete list of 16-bit UUIDs
	            while (len > 1) {
	                int uuid16 = advertisedData[offset++];
	                uuid16 += (advertisedData[offset++] << 8);
	                len -= 2;
	                uuids.add(UUID.fromString(String.format(
	                        "%08x-0000-1000-8000-00805f9b34fb", uuid16)));
	            }
	            break;
	        case 0x06:// Partial list of 128-bit UUIDs
	        case 0x07:// Complete list of 128-bit UUIDs
	            // Loop through the advertised 128-bit UUID's.
	            while (len >= 16) {
	                try {
	                    // Wrap the advertised bits and order them.
	                    ByteBuffer buffer = ByteBuffer.wrap(advertisedData,
	                            offset++, 16).order(ByteOrder.LITTLE_ENDIAN);
	                    long mostSignificantBit = buffer.getLong();
	                    long leastSignificantBit = buffer.getLong();
	                    uuids.add(new UUID(leastSignificantBit,
	                            mostSignificantBit));
	                } catch (IndexOutOfBoundsException e) {
	                    // Defensive programming.
	                    Log.e(TAG, e.toString());
	                    continue;
	                } finally {
	                    // Move the offset to read the next uuid.
	                    offset += 15;
	                    len -= 16;
	                }
	            }
	            break;
	        default:
	            offset += (len - 1);
	            break;
	        }
	    }

	    return uuids;
	}

    private void initAutoReconnectTimer(final List<SupportedService> servicelist)
    {
        if(mAutoReconnectTimer!=null)
        {
            mAutoReconnectTimer.cancel();
            mAutoReconnectTimer = null;
        }
        mAutoReconnectTimer = new Timer();
        mAutoReconnectTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    if (isDisconnected()) {
                        Log.w(TAG, "reconnect after........... " + mReConnectTimerPattern[mTimerIndex] / 1000 + "s");
                        mTimerIndex = (mTimerIndex + 1) % mReConnectTimerPattern.length;
                    }
                    else
                    {
                        mTimerIndex = 0;
                    }
                    //check again connect status
                    connect(servicelist);

                } catch (BLENotSupportedException e) {
                    e.printStackTrace();
                } catch (BluetoothDisabledException e) {
                    e.printStackTrace();
                }
            }
        },mReConnectTimerPattern[mTimerIndex]);
    }
	@Override
	public void connect(List<SupportedService> servicelist)
			throws BLENotSupportedException, BluetoothDisabledException {

        initAutoReconnectTimer(servicelist);

		if (!isDisconnected()) {return;}
		
		if(hasSavedAddress() && mCurrentService != null)
		{
			mCurrentService.connect(getSaveAddress());			
		}
		else
		{
			startScan(servicelist);
		}
		
	}

	private void setSaveAddress(String address)
	{
		mContext.getSharedPreferences(PREF_NAME, 0).edit().putString(SAVE_MAC_ADDRESS, address).commit();
	}
	private String getSaveAddress()
	{
		return mContext.getSharedPreferences(PREF_NAME, 0).getString(SAVE_MAC_ADDRESS, "");
	}
	
	@Override
	public boolean hasSavedAddress() {
		if(!mContext.getSharedPreferences(PREF_NAME, 0).getString(SAVE_MAC_ADDRESS, "").equals(""))
		{			
			return true;
		}
		return false;
	}

	@Override
	public void forgetSavedAddress() {	
		if(hasSavedAddress())
		{
			saveAddress = getSaveAddress();
		}
		mContext.getSharedPreferences(PREF_NAME, 0).edit().putString(SAVE_MAC_ADDRESS, "").commit();
	}

	@Override
	public void restoreSavedAddress() {
		if(!saveAddress.equals(""))
			mContext.getSharedPreferences(PREF_NAME, 0).edit().putString(SAVE_MAC_ADDRESS, saveAddress).commit();
		
	}

	
	/*
	 * End of Util Functions
	 */

}
