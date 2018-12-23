package com.zzteck.cardect.ui;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ble.api.DataUtil;
import com.zzteck.cardect.R;
import com.zzteck.cardect.util.LeProxy;
import com.zzteck.cardect.util.Utils;

import java.util.List;

public class EngerActivity extends Activity implements OnClickListener {

	private Context mContext;

	private ImageView mIvStart ;
	
	private ImageView mIvBack ;

	@Override
	protected void onResume() {
		super.onResume();
		setImageViewConnectStatus();
	}

	private ImageView mIvConnect ;

	private void setImageViewConnectStatus(){
		mIvConnect = (ImageView)findViewById(R.id.iv_right) ;
		if (mIvConnect != null) {
			mIvConnect.setVisibility(View.VISIBLE);
			if (MApplication.isConnect) {
				mIvConnect.setImageResource(R.mipmap.ic_connect);
			}else{
				mIvConnect.setImageResource(R.mipmap.ic_wait);
			}
		}
	}
	private TextView mTvMainInfo ;

	private void initView() {

		mTvMainInfo = (TextView) findViewById(R.id.tv_main) ;
		mTvMainInfo.setText("START ENGINE");

		mIvBack = (ImageView)findViewById(R.id.iv_back) ;
		mIvStart = (ImageView)findViewById(R.id.iv_start) ;
		mIvStart.setOnClickListener(this) ;
		mIvBack.setOnClickListener(this);
	}

	private String mSelectedAddress;
	  
	 private LeProxy mLeProxy;
	
	 private final BroadcastReceiver mGattReceiver = new BroadcastReceiver() {
	        @Override
	        public void onReceive(Context context, Intent intent) {
	            String address = intent.getStringExtra(LeProxy.EXTRA_ADDRESS);
	            if (LeProxy.ACTION_DATA_AVAILABLE.equals(intent.getAction())) {
	                if (address.equals(mSelectedAddress)) {
	                  //  displayRxData(intent);
	                }
	            } else if (LeProxy.ACTION_MTU_CHANGED.equals(intent.getAction())) {
	                int status = intent.getIntExtra(LeProxy.EXTRA_STATUS, BluetoothGatt.GATT_FAILURE);
	                if (status == BluetoothGatt.GATT_SUCCESS) {
	                    //ToastUtil.showMsg(getActivity(), "MTU has been " + mMtu);
	                } else {
	                   // ToastUtil.showMsg(getActivity(), "MTU update error: " + status);
	                }
	            }
	        }
	    };
	

	    private IntentFilter makeFilter() {
	        IntentFilter filter = new IntentFilter();
	        filter.addAction(LeProxy.ACTION_MTU_CHANGED);
	        filter.addAction(LeProxy.ACTION_DATA_AVAILABLE);
	        return filter;
	    }
	    
	    private void send(String message) {
	    	
	        try {
               byte[] data;
               data = DataUtil.hexToByteArray(message);
               mLeProxy.send(mSelectedAddress, data);

	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	    
	
	private void initData() {

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enger);
		mContext = EngerActivity.this;
		initView();
		initData();
		mLeProxy = LeProxy.getInstance();

		List<BluetoothDevice> deviceList = mLeProxy.getConnectedDevices();
		if(!deviceList.isEmpty()){
			mSelectedAddress = deviceList.get(0).getAddress() ;
		}

        LocalBroadcastManager.getInstance(mContext).registerReceiver(mGattReceiver, makeFilter());
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mGattReceiver);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_start:
			String sendMessage = "4145415310"+"00000000000000000000";
			String checkSum = Utils.makeChecksum(sendMessage) ;
			send(sendMessage+checkSum) ;
			Intent intent = new Intent(mContext,ProgressActivity.class) ;
			startActivity(intent) ;
			break;
		case R.id.iv_back :
			finish();
			break ;
		default:
			break;
		}
	}

}
