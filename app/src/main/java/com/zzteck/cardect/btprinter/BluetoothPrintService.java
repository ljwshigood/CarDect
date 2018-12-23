/*
 * Copyright (C) 2012 SHPM
 */

package com.zzteck.cardect.btprinter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;


public class BluetoothPrintService extends Service {
	public static final String TOAST = "toast";
	// Debugging
	protected static final String TAG = "BluetoothPrintService";
	protected static final boolean D = true;

	// BT DEVICE
	protected static final String NAME = "BluetoothPrint";

	// action
	public static final String SERVICE_ACTION = "BluetoothPrintService";
	public static final String STATE_STATUS = "state";
	public static final String DISCOVERY_RESULT = "result";
	public static final String DISCOVERY_END = "end";
	public static final String DEVICE_NAME = "device_name";
	public static final String INTENT_ADDRESS = "address";
	public static final String INTENT_PRINT = "print";
	public static final String INTENT_OP = "operate";

	// Unique UUID for this application
	// private static final UUID MY_UUID =
	// UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
	// Unique UUID for SPP application
	protected static final UUID MY_UUID = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");

	public static final int STATE_NONE = 1;
	public static final int STATE_LISTEN = 2;
	public static final int STATE_CONNECTING = 3;
	public static final int STATE_CONNECTED = 4;

	protected final BluetoothAdapter mAdapter = BluetoothAdapter .getDefaultAdapter();
	
	protected AcceptThread mAcceptThread;
	protected ConnectThread mConnectThread;
	protected ConnectedThread mConnectedThread;
	protected int mState;

	public static final int OP_CONNECT = 0;
	public static final int OP_LISTEN = 1;
	public static final int OP_PRINT = 2;
	public static final int OP_DISCOVERY = 3;
	public static final int OP_CANCEL_DISCOVERY = 4;

	public static final int DISCOVERY_DEFAULT = -1;
	public static final int DISCOVERY_NOTHING = 0;
	public static final int DISCOVERY_HAVING = 1;

	protected List<String> mDeviceList = new ArrayList<>();

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		Log.i(TAG, "onCreate");
		super.onCreate();
		mState = STATE_NONE;
		RegisterReceiverBluetooth();
		IntentFilter filter = new IntentFilter() ;
		filter.addAction("OP_CONNECT");
		filter.addAction("OP_LISTEN");
		filter.addAction("OP_PRINT");
		filter.addAction("OP_DISCOVERY");
		filter.addAction("OP_CANCEL_DISCOVERY");
		registerReceiver(mOparateRecever,filter) ;
	}

	private BroadcastReceiver mOparateRecever = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction() ;
			Bundle bundlePara = intent.getExtras();
			if(action.equals("OP_CONNECT")){
				String address = intent.getExtras().getString(INTENT_ADDRESS);
				connect(address);
			}else if(action.equals("OP_LISTEN")){
				intent.putExtra(STATE_STATUS, mState);
				sendServiceBroadcast(intent);
			}else if(action.equals("OP_PRINT")){
				if (mState != STATE_CONNECTED) {
					intent.putExtra(STATE_STATUS, mState);
					Bundle bundle = new Bundle();
					bundle.putString(TOAST,"Is not connected to the device");
					intent.putExtras(bundle);
					sendServiceBroadcast(intent);
					//Toast.makeText(getApplicationContext(),"Is not connected to the device",1).show();
				} else {
					byte[] b = intent.getByteArrayExtra(INTENT_PRINT);
					write(b);
				}
			}else if(action.equals("OP_DISCOVERY")){
				doDiscovery();
			}else if(action.equals("OP_CANCEL_DISCOVERY")){
				if (mAdapter.isDiscovering()) {
					mAdapter.cancelDiscovery();
				}
			}
		}
	} ;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		/*Intent intResult = new Intent();
		if (intent != null) {
			Bundle bundlePara = intent.getExtras();
			if (bundlePara != null) {
				int op = bundlePara.getInt(INTENT_OP);
				switch (op) {
				case OP_CONNECT:
					String address = intent.getExtras().getString(INTENT_ADDRESS);
					connect(address);
					break;
				case OP_LISTEN:
					intResult.putExtra(STATE_STATUS, mState);
					sendServiceBroadcast(intResult);
					break;
				case OP_PRINT:
					if (mState != STATE_CONNECTED) {
						intResult.putExtra(STATE_STATUS, mState);
						Bundle bundle = new Bundle();
						bundle.putString(TOAST,
								"Is not connected to the device");
						intResult.putExtras(bundle);
						sendServiceBroadcast(intResult);
					} else {
						byte[] b = intent.getByteArrayExtra(INTENT_PRINT);
						write(b);
					}
					break;
				case OP_DISCOVERY:
					doDiscovery();
					break;
				case OP_CANCEL_DISCOVERY:
					if (mAdapter.isDiscovering()) {
						mAdapter.cancelDiscovery();
					}
					break;
				}
			}
		}*/
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy");
		super.onDestroy();
		stop();
		unregisterReceiver(mOparateRecever);
	}

	/**
	 * set state
	 * 
	 * @param state
	 */
	protected synchronized void setState(int state) {
		if (D)
			Log.d(TAG, "setState() " + mState + " -> " + state);
		mState = state;

		Intent intent = new Intent();
		intent.putExtra(STATE_STATUS, mState);
		sendServiceBroadcast(intent);
	}

	/**
	 * set state
	 * 
	 * @param state
	 * @param intent
	 */
	protected synchronized void setState(int state, Intent intent) {
		if (D)
			Log.d(TAG, "setState() " + mState + " -> " + state);
		mState = state;

		sendServiceBroadcast(intent);
	}

	/**
	 * return connetcted status
	 */
	public synchronized int getState() {
		return mState;
	}

	/**
	 * sendBroadcast
	 * 
	 * @param intent
	 */
	protected synchronized void sendServiceBroadcast(Intent intent) {
		intent.setAction(SERVICE_ACTION);
		BluetoothPrintService.this.sendBroadcast(intent);
	}

	/**
	 * discovery device
	 */
	protected void doDiscovery() {
		if (D)
			Log.d(TAG, "doDiscovery()");
		if (mAdapter.isDiscovering()) {
			mAdapter.cancelDiscovery();
		}
		mDeviceList = new ArrayList<String>();
		mAdapter.startDiscovery();
	}

	/**
	 * register bluetooth
	 */
	protected void RegisterReceiverBluetooth() {
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		this.registerReceiver(mReceiver, filter);

		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		this.registerReceiver(mReceiver, filter);
	}

	/**
	 * listen
	 */
	protected final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Intent intResult = new Intent();

			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

				String strDevice = device.getName() + "\n"
						+ device.getAddress();
				if (!mDeviceList.contains(strDevice)) {
					mDeviceList.add(strDevice);

					intResult.putExtra(DISCOVERY_RESULT, strDevice);
					sendServiceBroadcast(intResult);
				}
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
					.equals(action)) {
				if (mDeviceList.isEmpty() == true) {
					intResult.putExtra(DISCOVERY_END, DISCOVERY_NOTHING);
					sendServiceBroadcast(intResult);
				} else {
					intResult.putExtra(DISCOVERY_END, DISCOVERY_HAVING);
					sendServiceBroadcast(intResult);
				}
			}
		}
	};

	/**
	 * service start
	 * */
	public synchronized void start() {
		if (D)
			Log.d(TAG, "start");
		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}
		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}
		if (mAcceptThread == null) {
			mAcceptThread = new AcceptThread();
			mAcceptThread.start();
		}
		setState(STATE_LISTEN);
	}

	/**
	 * connect device
	 * 
	 * @param address
	 *            device address
	 */
	public synchronized void connect(String address) {
		if (D)
			Log.d(TAG, "connect to: " + address);

		BluetoothDevice device = mAdapter.getRemoteDevice(address);
		if (mState == STATE_CONNECTING) {
			if (mConnectThread != null) {
				mConnectThread.cancel();
				mConnectThread = null;
			}
		}
		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}
		mConnectThread = new ConnectThread(device);
		mConnectThread.start();
		setState(STATE_CONNECTING);
	}

	/**
	 * connected device
	 * 
	 * @param socket
	 * @param device
	 */
	public synchronized void connected(BluetoothSocket socket,
			BluetoothDevice device) {
		if (D)
			Log.d(TAG, "connected");
		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}
		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}
		if (mAcceptThread != null) {
			mAcceptThread.cancel();
			mAcceptThread = null;
		}
		mConnectedThread = new ConnectedThread(socket);
		mConnectedThread.start();

		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString(DEVICE_NAME, device.getName());
		intent.putExtras(bundle);
		sendServiceBroadcast(intent);

		setState(STATE_CONNECTED);
	}

	/**
	 * stop
	 */
	public synchronized void stop() {
		if (D)
			Log.d(TAG, "stop");
		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}
		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}
		if (mAcceptThread != null) {
			mAcceptThread.cancel();
			mAcceptThread = null;
		}
		if (mAdapter.isDiscovering()) {
			mAdapter.cancelDiscovery();
		}

		this.unregisterReceiver(mReceiver);
		setState(STATE_NONE);
	}

	/**
	 * print
	 * 
	 */
	public void write(byte[] b) {
		ConnectedThread r;

		synchronized (this) {
			if (mState != STATE_CONNECTED)
				return;
			r = mConnectedThread;
		}
		r.write(b);
	}

	/**
	 * connectionFailed
	 */
	protected void connectionFailed() {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString(TOAST, "fail to connect!");
		intent.putExtras(bundle);
		setState(STATE_LISTEN, intent);
	}

	/**
	 * listen
	 */
	protected class AcceptThread extends Thread {
		protected final BluetoothServerSocket mmServerSocket;

		public AcceptThread() {
			BluetoothServerSocket tmp = null;

			try {
				tmp = mAdapter
						.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
			} catch (IOException e) {
				Log.e(TAG, "listen() failed", e);
			}
			mmServerSocket = tmp;
		}

		public void run() {
			if (D)
				Log.d(TAG, "BEGIN mAcceptThread" + this);
			setName("AcceptThread");
			BluetoothSocket socket = null;

			while (mState != STATE_CONNECTED) {
				try {
					socket = mmServerSocket.accept();
				} catch (IOException e) {
					Log.e(TAG, "accept() failed", e);
					break;
				}

				if (socket != null) {
					synchronized (BluetoothPrintService.this) {
						switch (mState) {
						case STATE_LISTEN:
						case STATE_CONNECTING:
							connected(socket, socket.getRemoteDevice());
							break;
						case STATE_NONE:
						case STATE_CONNECTED:
							try {
								socket.close();
							} catch (IOException e) {
								Log.e(TAG, "Can not close the socket锛�", e);
							}
							break;
						}
					}
				}
			}
			if (D)
				Log.i(TAG, "END mAcceptThread");
		}

		public void cancel() {
			if (D)
				Log.d(TAG, "cancel " + this);
			try {
				mmServerSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "close() of server failed", e);
			}
		}
	}

	/**
	 * ConnectThread
	 */
	protected class ConnectThread extends Thread {
		protected final BluetoothSocket mmSocket;
		protected final BluetoothDevice mmDevice;

		public ConnectThread(BluetoothDevice device) {
			mmDevice = device;
			BluetoothSocket tmp = null;

			try {
				tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
			} catch (IOException e) {
				Log.e(TAG, "An exception occurs锛�", e);
			}
			mmSocket = tmp;

		}

		public void run() {
			Log.i(TAG, "BEGIN mConnectThread");
			setName("ConnectThread");

			mAdapter.cancelDiscovery();

			try {
				mmSocket.connect();
			} catch (IOException e) {
				connectionFailed();
				try {
					mmSocket.close();
				} catch (IOException e2) {
					Log.e(TAG,
							"Can not close the socket before the connection fails锛�",
							e2);
				}

				BluetoothPrintService.this.start();
				return;
			}

			synchronized (BluetoothPrintService.this) {
				mConnectThread = null;
			}

			connected(mmSocket, mmDevice);
		}

		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "Close the socket failure锛�", e);
			}
		}
	}

	/**
	 * ConnectedThread
	 */
	protected class ConnectedThread extends Thread {
		protected final BluetoothSocket mmSocket;
		protected final OutputStream mmOutStream;

		public ConnectedThread(BluetoothSocket socket) {
			Log.d(TAG, "create ConnectedThread");
			mmSocket = socket;
			OutputStream tmpOut = null;

			try {
				tmpOut = socket.getOutputStream();
			} catch (IOException e) {
				Log.e(TAG, "Temporary socket is not established锛�", e);
			}

			mmOutStream = tmpOut;
		}

		public void run() {
			Log.i(TAG, "BEGIN mConnectedThread");
		}

		/**
		 * 锛� Write to the connected OutStream.
		 * 
		 * @param buffer
		 *            The bytes to write
		 */
		public void write(byte[] buffer) {

			// try {
			// mmOutStream.write(buffer);
			// } catch (IOException e) {
			// Log.e(TAG, "Exception during write", e);
			// }

			int readlen = 512;
			// byte[] cylebyte = new byte[readlen];

			int totallen = buffer.length;

			for (int srcPos = 0;;) {
				boolean isover = false;
				if ((srcPos + readlen) > totallen) {
					readlen = totallen - srcPos;
					isover = true;
				}
//				MicroLog.debug("srcPos / length :" + srcPos + "/ " + readlen);

				try {
					mmOutStream.write(buffer, srcPos, readlen);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}

				if (isover) {

					Intent intent = new Intent("print_end") ;
					sendBroadcast(intent);
					break;
				}
				srcPos = srcPos + readlen;
			}
		}

		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "close() of connect socket failed", e);
			}
		}
	}

}
