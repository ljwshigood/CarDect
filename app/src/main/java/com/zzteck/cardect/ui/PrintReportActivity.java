package com.zzteck.cardect.ui;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ble.api.DataUtil;
import com.zzteck.cardect.R;
import com.zzteck.cardect.adapter.CCASetAdapter;
import com.zzteck.cardect.bean.BatteryBean;
import com.zzteck.cardect.btprinter.BluetoothPrintService;
import com.zzteck.cardect.btprinter.util.PrintPngUtil;
import com.zzteck.cardect.util.LeProxy;
import com.zzteck.cardect.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class PrintReportActivity extends Activity implements OnClickListener {

	private Context mContext;

	private ImageView mIvBack ;
	
	private LinearLayout mLLPrint ;
	
	private LinearLayout mLLConnect ; 
	
	private void initView() {
		mLLConnect = (LinearLayout)findViewById(R.id.ll_connect) ;
		mLLPrint = (LinearLayout)findViewById(R.id.ll_print) ;
		mIvBack = (ImageView)findViewById(R.id.iv_back) ;
		mIvBack.setOnClickListener(this) ;
		mLLConnect.setOnClickListener(this);
		mLLPrint.setOnClickListener(this);
	}
	
	private String mPrintString ;

	private byte[] mByteBitmap ;

	private String mFilePath ;

	private void initData() {
		Intent intent = getIntent() ;
		if(intent != null){
			mPrintString = getIntent().getStringExtra("print") ;
			mByteBitmap = getIntent().getByteArrayExtra("print") ;
			mFilePath = getIntent().getStringExtra("file") ;
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_print_report);
		mContext = PrintReportActivity.this;
		initView();
		initData();
	}
	
	private boolean isConnnect ;
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == 1122){
			mLLConnect.setBackgroundResource(R.mipmap.connected);
			isConnnect = true ;
		}
	}
	

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public void onClick(View v) {
		Intent intent = null ;
		switch (v.getId()) {
		case R.id.iv_back:
			finish() ;
			break;
		case R.id.ll_connect :
			intent = new Intent(mContext,DeviceListActivity.class) ;
			startActivityForResult(intent,2233);
			break ;
		case R.id.ll_print :
			if(isConnnect){
				
				if(!TextUtils.isEmpty(mPrintString)){
					intent = new Intent("OP_PRINT") ;
					intent.putExtra("print",strToByteArray(mPrintString+"\n\n\n")) ;
					sendBroadcast(intent);
				}else if(mFilePath != null){
					Bitmap bitMap = BitmapFactory.decodeFile(mFilePath);
					printImg(bitMap);
				}
			}
			break ;
		default:
			break;
		}
	}

	public byte[] strToByteArray(String str) {
		if (str == null) {
			return null;
		}
		byte[] byteArray = str.getBytes();
		return byteArray;
	}

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
}
