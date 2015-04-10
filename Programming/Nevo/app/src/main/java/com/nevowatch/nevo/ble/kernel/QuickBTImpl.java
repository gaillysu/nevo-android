package com.nevowatch.nevo.ble.kernel;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.UUID;

import com.nevowatch.nevo.ble.model.request.SensorRequest;
import com.nevowatch.nevo.ble.util.QueuedMainThreadHandler;


/*package*/
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
class QuickBTImpl implements QuickBT {

	final QueuedMainThreadHandler mQueuedMainThread = QueuedMainThreadHandler.getInstance();
	
	//Initialised values
	boolean mInitSuccessful = false;
	BluetoothDevice mBluetoothDevice;
	Context mContext;
	
	
	//Reconfigurable values
	UUID mServiceUuid;
	UUID mCharacteristicUuid;
	byte[] mValue;
	
	//This depends on your external hardware
	int MINIMAL_CONNECTION_TIME = 1000;
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public QuickBTImpl(String address, final Context ctx) {

		//If any argument is null, let's not proceed
		if(address==null || address.equals("") || ctx==null) {
			
			Log.e(TAG,"An argument is null");
			
			return;
		}
		
    	BluetoothManager bluetoothManager = (BluetoothManager) ctx.getSystemService(Context.BLUETOOTH_SERVICE);
    	
        if (bluetoothManager == null) {
        	
             Log.e(TAG, "Unable to initialize BluetoothManager.");
             
             return;
        }


        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
     
        if (bluetoothAdapter == null) {
        	
        	Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
        	
        	return;
        }
		
        final BluetoothDevice d = bluetoothAdapter.getRemoteDevice(address);
     
        if(d == null || d.getAddress() == null) {
        	
        	Log.e(TAG, "Unable to obtain BluetoothDevice.");
        	
        	return;
        }
        
        mBluetoothDevice = d;
        mContext = ctx;
        
        mInitSuccessful = true;   	
        
		Log.d(TAG,"Init success !");
	}
	
	/* (non-Javadoc)
	 * @see fr.imaze.sdk.kernel.QuickBT#send(fr.imaze.sdk.model.request.SensorRequest)
	 */
	@Override
	public void send(SensorRequest request) {
		
		Log.d(TAG,"Attempting to connect");
		
		if(mInitSuccessful == false) {
        	
        	Log.e(TAG, "Init failed, can't send.");
        	
        	return;
		}
		
		if(request.getServiceUUID() == null || request.getCharacteristicUUID() == null || request.getRawData() == null || request.getRawData().length==0) {
        	
        	Log.e(TAG, "Init failed, can't send.");
        	
        	return;
		}
		
		mValue = request.getRawData();
		mServiceUuid = request.getServiceUUID();
		mCharacteristicUuid = request.getCharacteristicUUID();
		
		mQueuedMainThread.post( new Runnable() {
			
			@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
			public void run() {
				
				if(mBluetoothDevice == null || mContext == null || mBluetoothDevice.getAddress() == null) {
		        	
		        	Log.e(TAG, "Parameters changed while waiting");
		        	
		        	return;
				}
				
				Log.d(TAG,"Connecting... "+mBluetoothDevice.getAddress());
				
				mBluetoothDevice.connectGatt(mContext, false, mGattCallback);
			}
			
		});
	}
	
	
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
    	
    	@Override
    	public void onConnectionStateChange(final android.bluetooth.BluetoothGatt gatt, int status, int newState) {
    		
    		Log.d(TAG,"Connection changed");
    		
    		if(newState != BluetoothProfile.STATE_CONNECTED) {
    			
    			Log.d(TAG,"Not connected, newState:" + newState + ",status:" + status);

    			//only call gatt.disconnect() not enough, must call gatt.close() for release Ble link in low level.
    			if(newState == BluetoothProfile.STATE_DISCONNECTED)
    			{
    				gatt.close();    			
    			}
    			return ;
    		}
    		
    		if( gatt == null || gatt.getDevice() == null || gatt.getDevice().getAddress() == null ) {
    			
    			Log.e(TAG,"Connection error");
    			
    			return ;
    		}
    		

    		mQueuedMainThread.next();
    		
    		mQueuedMainThread.post( new Runnable() {
    				
    			@Override
    			public void run() {
    				Log.d(TAG,"Connected. Discovering services.");
    				
    	    		gatt.discoverServices();
    			}
    		});
    		
    	};
    	
    	@Override
    	public void onServicesDiscovered(final android.bluetooth.BluetoothGatt gatt, int status) {
    		Log.d(TAG,"Discovery successfull, trying to write value");
    		
    		if( gatt == null || gatt.getDevice() == null || gatt.getDevice().getAddress() == null ) {
    			
    			Log.e(TAG,"Connection error");
    			
    			return ;
    		}
    		
    		
    		BluetoothGattService service = gatt.getService( mServiceUuid );
    		
    		if( service == null ) {
    			
    			Log.e(TAG,gatt.getDevice().getAddress()+" doesn't have service "+mServiceUuid);
    			
    			return;
    		}
    		
    		
    		final BluetoothGattCharacteristic bluetoothGattCharacteristic = service.getCharacteristic( mCharacteristicUuid );

			if( bluetoothGattCharacteristic == null )  {
    			
    			Log.e(TAG,gatt.getDevice().getAddress()+" doesn't have characteristic "+mCharacteristicUuid+" for service "+mServiceUuid);
    			
    			return;
    		}
			
			//Now we've found the right characteristic, we modify it, then send it to the device
			bluetoothGattCharacteristic.setValue( mValue );
			
			mQueuedMainThread.next();
			
			mQueuedMainThread.post( new Runnable() {
				
				@Override
				public void run() {
					
	    			gatt.writeCharacteristic(bluetoothGattCharacteristic);
	    			
				}
			});

    	};
    	
    	@Override
    	public void onCharacteristicWrite(final BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
    		
    		Log.d(TAG, "Write success, let's disconnect");
    		
    		if( gatt == null || gatt.getDevice() == null || gatt.getDevice().getAddress() == null ) {
    			
    			Log.e(TAG,"Connection error");
    			
    			return ;
    		}
    		
    		//The characteristic have been modified, let's simply disconnect
    		mQueuedMainThread.next();
    		
            //For some reason we have to do it on the UI thread...
            //But we don't do it in the Queued Handler, because we can't reliabily mQueuedMainThread.next(); on disconnect
            //Because a disconnection can come from a lot of things
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
    			
    			@Override
    			public void run() {
    				Log.d(TAG, "Disconnecting");

    				gatt.disconnect();
    				gatt.close(); 
    			}
    		},MINIMAL_CONNECTION_TIME);
    		
    	};
    	
    };

}
