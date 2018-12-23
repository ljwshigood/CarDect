package com.zzteck.cardect.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.Exchanger;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.zzteck.cardect.R;
import com.zzteck.cardect.btprinter.BluetoothPrintService;
import com.zzteck.cardect.btprinter.util.PrintPngUtil;

/**
 * 设备搜索画面
 */
public class DeviceListActivity extends Activity {
	// 设备地址常量
	public static String EXTRA_DEVICE_ADDRESS = "device_address";

	// 成员
	private BluetoothAdapter mBtAdapter;
	private ArrayAdapter<String> mPairedDevicesArrayAdapter;
	private ArrayAdapter<String> mNewDevicesArrayAdapter;

	private ImageView mIvBack ;

	private String mPrintString ;

	private byte[] mByteBitmap ;

	private String mFilePath ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 设置画面
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.device_list);

		// 初始化参数
		setResult(Activity.RESULT_CANCELED);

		Button scanButton = (Button) findViewById(R.id.button_scan);
		scanButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				doDiscovery();
				v.setVisibility(View.GONE);
			}
		});
		Intent intent = getIntent() ;
		if(intent != null){
			mPrintString = getIntent().getStringExtra("print") ;
			mByteBitmap = getIntent().getByteArrayExtra("print") ;
			mFilePath = getIntent().getStringExtra("file") ;
		}

		mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this,R.layout.device_name);
		mNewDevicesArrayAdapter = new ArrayAdapter<String>(this,R.layout.device_name);

		ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
		pairedListView.setAdapter(mPairedDevicesArrayAdapter);
		pairedListView.setOnItemClickListener(mDeviceClickListener);

		ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
		newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
		newDevicesListView.setOnItemClickListener(mDeviceClickListener);

		IntentFilter filter = new IntentFilter(BluetoothPrintService.SERVICE_ACTION);
		filter.addAction("print_end");
		this.registerReceiver(mReceiver, filter);

		mBtAdapter = BluetoothAdapter.getDefaultAdapter();

		Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

		if (pairedDevices.size() > 0) {
			ArrayList<String> lstAddress = new ArrayList<String>(100);
			for (BluetoothDevice device : pairedDevices) {
				if (!lstAddress.contains(device.getAddress())) {
					lstAddress.add(device.getAddress());
					mPairedDevicesArrayAdapter.add(device.getName() + "\n"
							+ device.getAddress());
				}
			}
		} else {
			String noDevices = "none_paired";
			mPairedDevicesArrayAdapter.add(noDevices);
		}
		mIvBack = (ImageView)findViewById(R.id.iv_back) ;
		mIvBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
 	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		// 服务启动（停止检索设备）
		Intent it = new Intent("com.bluetooth.service_print");
		Bundle bundle = new Bundle();
		bundle.putInt(BluetoothPrintService.INTENT_OP,BluetoothPrintService.OP_CANCEL_DISCOVERY);
		it.putExtras(bundle);
		startService(it);
		// 注销广播监听
		this.unregisterReceiver(mReceiver);
	}

	/**
	 * 开始检索
	 */
	private void doDiscovery() {
		// 设置抬头
		setProgressBarIndeterminateVisibility(true);
		//setTitle(R.string.scanning);
		//findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

		// 服务启动（检索设备）
		Intent it = new Intent("com.bluetooth.service_print");
		Bundle bundle = new Bundle();
		bundle.putInt(BluetoothPrintService.INTENT_OP,
				BluetoothPrintService.OP_DISCOVERY);
		it.putExtras(bundle);
		startService(it);

	}

	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(!TextUtils.isEmpty(mPrintString)){
				Intent intent = new Intent("OP_PRINT") ;
				intent.putExtra("print",strToByteArray(mPrintString+"\n\n\n")) ;
				sendBroadcast(intent);
			}else if(mFilePath != null){
				Bitmap bitMap = BitmapFactory.decodeFile(mFilePath);
				printImg(bitMap);
			}
		}
	} ;

	private void printImg(Bitmap bitMap) {
		byte[] b = null;

		PrintPngUtil util = new PrintPngUtil(bitMap);
		b = util.printGrayMode();
		if (b != null) {

			Intent intent = new Intent("OP_PRINT") ;
			Bundle bundle = new Bundle();
			bundle.putByteArray(BluetoothPrintService.INTENT_PRINT, b);
			intent.putExtras(bundle);
			sendBroadcast(intent);

			/*// 服务启动（打印）
			Intent it = new Intent("com.bluetooth.service_print");
			Bundle bundle = new Bundle();
			bundle.putInt(BluetoothPrintService.INTENT_OP,
					BluetoothPrintService.OP_PRINT);

			bundle.putByteArray(BluetoothPrintService.INTENT_PRINT, b);
			it.putExtras(bundle);
			startService(it);*/
		}
	}

	public byte[] strToByteArray(String str) {
		if (str == null) {
			return null;
		}
		byte[] byteArray = str.getBytes();
		return byteArray;
	}

	private ProgressDialog pd ;


	private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {

			Intent intent = new Intent("OP_CANCEL_DISCOVERY") ;
			sendBroadcast(intent);
			String info = ((TextView) v).getText().toString();
			if (info.equals("none_found")) {
				return;
			}
			String address = info.substring(info.length() - 17);
			connectDevice(address);
			/*mHandler.sendEmptyMessageDelayed(0,5000) ;

			pd = new ProgressDialog(DeviceListActivity.this);
			pd.setMessage("print......");
			pd.setCancelable(false);
			pd.show();*/


		}
	};

	// 广播监听
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothPrintService.SERVICE_ACTION.equals(action)) {
				String strDevice = intent.getStringExtra(BluetoothPrintService.DISCOVERY_RESULT);
				int intResult = intent.getIntExtra(
						BluetoothPrintService.DISCOVERY_END,
						BluetoothPrintService.DISCOVERY_DEFAULT);
				int status = intent.getIntExtra(BluetoothPrintService.STATE_STATUS, 0) ;
				if (strDevice != null && strDevice != "") {
					mNewDevicesArrayAdapter.add(strDevice);
				}else if (intResult != BluetoothPrintService.DISCOVERY_DEFAULT) {
					/*if (intResult != BluetoothPrintService.DISCOVERY_HAVING) {
						String strNothing = "none_found";
						mNewDevicesArrayAdapter.add(strNothing);
					}*/
				}else if(status == BluetoothPrintService.STATE_CONNECTED){
					DeviceListActivity.this.setResult(1122);
					finish(); 
				}
			}else if(action.equals("print_end")){
				if(mFilePath != null){
					new File(mFilePath).delete();
				}
				if(pd != null){
					pd.dismiss();
				}
			}
		}
	};

	private void connectDevice(String address) {
		
		// 服务启动（连接设备）
		/*Intent it = new Intent("com.bluetooth.service_print");
		Bundle bundle = new Bundle();
		bundle.putInt(BluetoothPrintService.INTENT_OP,BluetoothPrintService.OP_CONNECT);
		bundle.putString(BluetoothPrintService.INTENT_ADDRESS, address);
		it.putExtras(bundle);
		startService(it);*/
		Intent intent = new Intent("OP_CONNECT")  ;
		intent.putExtra("address",address) ;
		sendBroadcast(intent);

	}
}
