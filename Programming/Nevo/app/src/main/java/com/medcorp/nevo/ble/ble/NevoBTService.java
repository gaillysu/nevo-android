/*
 * COPYRIGHT (C) 2014 MED Enterprises LTD. All Rights Reserved.
 */

package com.medcorp.nevo.ble.ble;

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

import com.medcorp.nevo.ble.exception.BLEUnstableException;
import com.medcorp.nevo.ble.kernel.NevoBT;
import com.medcorp.nevo.ble.listener.OnConnectListener;
import com.medcorp.nevo.ble.listener.OnDataReceivedListener;
import com.medcorp.nevo.ble.listener.OnExceptionListener;
import com.medcorp.nevo.ble.listener.OnFirmwareVersionListener;
import com.medcorp.nevo.ble.model.packet.DataFactory;
import com.medcorp.nevo.ble.model.packet.SensorData;
import com.medcorp.nevo.ble.model.request.SensorRequest;
import com.medcorp.nevo.ble.util.Constants;
import com.medcorp.nevo.ble.util.Optional;
import com.medcorp.nevo.ble.util.QueuedMainThreadHandler;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.binary.StringUtils;

import java.lang.reflect.Method;
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
	private BluetoothAdapter bluetoothAdapter;


	/**
	 * save all the connected BLE Gatt profiles, use LinkedHashMap<> class, default access order is FIFO
	 * make sure one service only connected one device
	 */
	private  Map<String,BluetoothGatt> bluetoothGattMap = new LinkedHashMap<String,BluetoothGatt>();

	private QueuedMainThreadHandler queuedMainThread;

	private OnDataReceivedListener dataReceivedListener;

	private OnConnectListener onConnectListener;

	private OnExceptionListener onExceptionListener;

	private OnFirmwareVersionListener onFirmwareVersionListener;

	private static final int RETRY_DELAY = 3000;

	private  String firmwareVersion = null;
	private  String softwareVersion = null;

	/**
	 * This binder is the bridge between the ImazeBTImpl and this Service
	 * @author Hugo
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
			return bluetoothGattMap.containsKey(address);
		}

		/**
		 * @return true if no device is currently connected
		 */
		public boolean isDisconnected(){
			return NevoBTService.this.bluetoothGattMap.isEmpty();
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

		dataReceivedListener = dataReceived;

		onConnectListener = connect;

		onExceptionListener = exception;

		onFirmwareVersionListener = firmware;

		queuedMainThread = QueuedMainThreadHandler.getInstance(QueuedMainThreadHandler.QueueType.NevoBT);

		BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		if (bluetoothManager == null) {
			Log.e(NevoBT.TAG, "Unable to initialize BluetoothManager.");
			return false;
		}


		bluetoothAdapter = bluetoothManager.getAdapter();
		if (bluetoothAdapter == null) {
			Log.e(NevoBT.TAG, "Unable to obtain a BluetoothAdapter.");
			return false;
		}

		return true;
	}

	/**
	 * Automatically try to connect to this address.
	 * It will try until it succeeds.
	 * @param address
	 */
	private void autoConnect(final String address){
		//All discoveries should be canceled before we try to connect
		bluetoothAdapter.cancelDiscovery();

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
		if (bluetoothAdapter == null) {
			Log.w(NevoBT.TAG, "BluetoothAdapter not initialized");
			return false;
		}

		// if the device has a Gatt service, it means we are connected already first close it, and make a new connection
		if(bluetoothGattMap.get(address)!=null)
		{
			bluetoothGattMap.get(address).close();
			bluetoothGattMap.remove(address);
		}
		final BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
		if (device == null) {
			Log.w(NevoBT.TAG, "Device not found.  Unable to connect.");
			return false;
		}

		// We don't know if the device is available, so we try to connect to it
		//We should do this on the UI thread (for some reason)... http://stackoverflow.com/questions/6369287/accessing-ui-thread-handler-from-a-service
		queuedMainThread.post(new Runnable() {
			;

			@Override
			public void run() {
				Log.d(NevoBT.TAG, "Connecting to Gatt : " + address);
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
		if (bluetoothAdapter == null) {
			Log.w(NevoBT.TAG, "BluetoothAdapter not initialized");
			return;
		}
		//For some reason we have to do it on the UI thread...
		//But we don't do it in the Queued Handler, because we can't reliabily queuedMainThread.next(); on disconnect
		//Because a disconnection can come from a lot of things
		new Handler(Looper.getMainLooper()).post(new Runnable() {

			@Override
			public void run() {
				Log.i(NevoBT.TAG, "Disconnecting "+address);
				if(bluetoothGattMap.containsKey(address))
				{
					bluetoothGattMap.get(address).disconnect();
				}
			}
		});

	}

	/**
	 * Clears the device cache. After uploading new firmware the DFU target will have other services than before.
	 *
	 * @param gatt
	 *            the GATT device to be refreshed
	 */
	private void refreshDeviceCache(final BluetoothGatt gatt) {
		/*
		 * There is a refresh() method in BluetoothGatt class but for now it's hidden. We will call it using reflections.
		 */
		try {
			final Method refresh = gatt.getClass().getMethod("refresh");
			if (refresh != null) {
				final boolean success = (Boolean) refresh.invoke(gatt);
				Log.i(NevoBT.TAG,"Refreshing result: " + success);
			}
		} catch (Exception e) {
			Log.i(NevoBT.TAG,"An exception occurred while refreshing device", e);
		}
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
				queuedMainThread.next();

				bluetoothGattMap.put(address, gatt);

				Log.i(NevoBT.TAG, "Connected to GATT server : "+ address);

				// Attempts to discover services after successful connection.
				Log.v(NevoBT.TAG, "Attempting to start service discovery");
				//fixed by Gailly, add 200ms defer to do discover services, let all services get ready
				queuedMainThread.postDelayed(new Runnable() {
					@Override
					public void run() {
						Log.d(NevoBT.TAG, "Discovering services : " + address);
						if (gatt != null) gatt.discoverServices();
					}
				}, 200);
				return;

			} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {

				Log.e(NevoBT.TAG, "Disconnected from GATT server : " + address);

				if(onConnectListener !=null && gatt!=null) onConnectListener.onConnectionStateChanged(false,address);

				//close this server for next reconnect!!!
				if(gatt!=null) {refreshDeviceCache(gatt);gatt.close();}
				bluetoothGattMap.remove(address);
				//we don't know why the Gatt server disconnected, so no need again connect, for example: BLE devices power off or go away
				return;
			} else {

				Log.e(NevoBT.TAG, "Unknown state for "+ address);
				//No matter what, if the device is not connected, we remove it from the list of connected devices
				bluetoothGattMap.remove(address);
			}
		}

		@Override
		public void onServicesDiscovered(final BluetoothGatt gatt, int status) {
			queuedMainThread.next();

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
							queuedMainThread.post(new Runnable() {
								@Override
								public void run() {
									Log.v(NevoBT.TAG, "start read version: " + uuid);
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
						queuedMainThread.post(new Runnable() {
							@Override
							public void run() {
								if (gatt != null)
									gatt.writeCharacteristic(GattAttributes.initBLECharacteristic(uuid, characteristic));
							}
						});
					}
				}
			}

			if(!characteristicChosen){
				Log.w(NevoBT.TAG,"No characteristic chosen, maybe the bluetooth is unstable : "+address);
				onExceptionListener.onException(new BLEUnstableException());
			}
			else
			{
				//here only connect one nevo, the first nevo by scan to find out
				bluetoothGattMap.put(gatt.getDevice().getAddress(), gatt);
				if(onConnectListener !=null && gatt!=null) onConnectListener.onConnectionStateChanged(true,gatt.getDevice().getAddress());
			}

		}

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt,
										 BluetoothGattCharacteristic characteristic,
										 int status) {
			queuedMainThread.next();
			if (status == BluetoothGatt.GATT_SUCCESS) {
				if (UUID.fromString(GattAttributes.DEVICEINFO_FIRMWARE_VERSION).equals(characteristic.getUuid())){
					firmwareVersion = StringUtils.newStringUsAscii(characteristic.getValue());
					Log.i(NevoBT.TAG,"FIRMWARE VERSION **************** "+ firmwareVersion);
					onFirmwareVersionListener.firmwareVersionReceived(Constants.DfuFirmwareTypes.APPLICATION, firmwareVersion);
				}
				else if (UUID.fromString(GattAttributes.DEVICEINFO_SOFTWARE_VERSION).equals(characteristic.getUuid())){
					softwareVersion = StringUtils.newStringUsAscii(characteristic.getValue());
					Log.i(NevoBT.TAG,"SOFTWARE VERSION **************** "+ softwareVersion);
					onFirmwareVersionListener.firmwareVersionReceived(Constants.DfuFirmwareTypes.SOFTDEVICE, softwareVersion);
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
			queuedMainThread.next();
		};

		public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
			queuedMainThread.next();
		};

		public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
			queuedMainThread.next();
		};

		public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
			queuedMainThread.next();
		};

		public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
			queuedMainThread.next();
		};

	};

	/**
	 * After using a given BLE device, the app must call this method to ensure resources are
	 * released properly.
	 */
	private void close() {
		if(queuedMainThread !=null) queuedMainThread.clear();
        /*
         * perhapse unbindService and LocalBinder.destroy() both call it
         */
		if(bluetoothGattMap!=null &&bluetoothGattMap.isEmpty() == false)
		{ //use disconnect() replace close(),disconnect() will invoke callback function, but close can't
			for(BluetoothGatt b : bluetoothGattMap.values()) b.disconnect();
			bluetoothGattMap.clear();
		}
	}

	/**
	 * Broadcast the data updates
	 * @param characteristic
	 */
	private void dataReceived(final BluetoothGattCharacteristic characteristic, final String address) {

		SensorData data = DataFactory.fromBluetoothGattCharacteristic(characteristic, address);

		if(data!=null&& dataReceivedListener !=null) dataReceivedListener.onDataReceived(data);
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
		if (bluetoothAdapter == null || gatt == null) {
			Log.w(NevoBT.TAG, "BluetoothAdapter not initialized");
			return;
		}
		int charaProp = characteristic.getProperties();

		Log.v(NevoBT.TAG, "characteristic.getProperties() is: " + charaProp);

		if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ)== BluetoothGattCharacteristic.PROPERTY_READ)
		{
			queuedMainThread.post(new Runnable() {
				@Override
				public void run() {
					Log.v(NevoBT.TAG, "Reading characteristic");
					if (gatt != null) gatt.readCharacteristic(characteristic);
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
		if (bluetoothAdapter == null || gatt == null) {
			Log.w(NevoBT.TAG, "BluetoothAdapter not initialized");
			return;
		}
		queuedMainThread.post(new Runnable() {
			@Override
			public void run() {
				if (gatt != null) gatt.setCharacteristicNotification(characteristic, enabled);
				new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
					@Override
					public void run() {
						queuedMainThread.next();
					}
				}, 1000);
			}
		});

		final BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(GattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
		if(descriptor!=null){
			descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
			queuedMainThread.post(new Runnable() {
				@Override
				public void run() {
					if (gatt != null) gatt.writeDescriptor(descriptor);
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
		if (gatt == null) {
			return new ArrayList<BluetoothGattService>();
		}
		return gatt.getServices();
	}

	/**
	 * Checks if a device already covers the given service.
	 * @param service the service that we are looking up. We'll try to see if a connected device provides this service.
	 * @return the address of the connected device (if any) or an empty Optional if there's no device currently covering this service
	 */
	private Optional<String> isServiceConnected(UUID service) {
		if(bluetoothGattMap == null || bluetoothGattMap.isEmpty())  return new Optional<String>();

		Collection<BluetoothGatt>  gatts = bluetoothGattMap.values();

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
		final byte[] rawData = deviceRequest.getRawData();
		byte[][] rawDatas = deviceRequest.getRawDataEx();

		if(bluetoothGattMap == null || bluetoothGattMap.isEmpty())  {
			Log.w(NevoBT.TAG, "Send failed. No device connected" );
			return;
		}

		boolean sent = false;

		for(final BluetoothGatt gatt : bluetoothGattMap.values())
		{
			//For each connected device, we'll see if they have the right service
			BluetoothGattService service = gatt.getService(serviceUUID);

			if(service!=null) {
				final BluetoothGattCharacteristic characteristic = service.getCharacteristic(characteristicUUID);
				if(characteristic!=null) {
					//Now we've found the right characteristic, we modify it, then send it to the device
					if(rawDatas != null)
					{
						for(final byte[] data : rawDatas)
						{
							//make sure every packet is sent one by one with the low level Queue: QueueType.NevoBT
							queuedMainThread.post(new Runnable() {
								@Override
								public void run() {
									Log.i(NevoBT.TAG, "Send requestEx " + new String(Hex.encodeHex(data)));
									characteristic.setValue(data);
									gatt.writeCharacteristic(characteristic);
								}
							});
						}
					}
					else
					{
						if(rawData != null)
						{
							//make sure every packet is sent one by one with the low level Queue: QueueType.NevoBT
							queuedMainThread.post(new Runnable() {
								@Override
								public void run() {
									Log.i(NevoBT.TAG, "Send request " + new String(Hex.encodeHex(rawData)));
									characteristic.setValue(rawData);
									gatt.writeCharacteristic(characteristic);
								}
							});
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

		if(bluetoothGattMap == null || bluetoothGattMap.isEmpty())  {
			Log.w(NevoBT.TAG, "Get failed. No device connected" );
			return;
		}

		boolean sent = false;

		for(BluetoothGatt gatt : bluetoothGattMap.values())
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

		if(!sent) {
			Log.w(NevoBT.TAG, "Get failed. No device have the right service and characteristic" );
		}
	}

	private String getFirmwareVersion()
	{
		return firmwareVersion;
	}

	private String getSoftwareVersion()
	{
		return softwareVersion;
	}

}
