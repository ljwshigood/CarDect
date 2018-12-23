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

import com.ble.api.DataUtil;
import com.zzteck.cardect.R;
import com.zzteck.cardect.util.LeProxy;

import java.util.List;

public class BatteryInfoDectActivity extends Activity implements OnClickListener {

	private Context mContext;

	private ImageView mIvBack ;
	
	private LinearLayout mLLContinue ;
	
	private ImageView mIvBatteryInfo ;
	
	private void initView() {
		mIvBatteryInfo = (ImageView)findViewById(R.id.iv_battery_info) ;
		mLLContinue = (LinearLayout)findViewById(R.id.ll_continue) ;
		mIvBack = (ImageView)findViewById(R.id.iv_back) ;
		mIvBack.setOnClickListener(this) ;
		mLLContinue.setOnClickListener(this);
	}

	private int mType,mFlag ;
	
	
	private void initData() {
		Intent intent = getIntent() ;
		if(intent != null){
			mType = intent.getIntExtra("type", 0) ;
			mFlag = intent.getIntExtra("flag", 0) ;
		}
		
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
	                   // mMtu = intent.getIntExtra(LeProxy.EXTRA_MTU, 23);
	                    //updateTxData();
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
	    
	    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_battery_dect);
		mContext = BatteryInfoDectActivity.this;
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
		case R.id.iv_back:
			finish() ;
			break;
			case R.id.ll_continue:
			Intent intent = new Intent(mContext,CCASetActivity.class) ;
			intent.putExtra("type", mType) ;
			intent.putExtra("flag", mFlag) ;
			mContext.startActivity(intent) ;
			break ;
		default:
			break;
		}
	}

}
