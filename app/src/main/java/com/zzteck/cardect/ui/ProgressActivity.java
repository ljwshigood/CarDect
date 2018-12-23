package com.zzteck.cardect.ui;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ble.api.DataUtil;
import com.zzteck.cardect.R;
import com.zzteck.cardect.util.LeProxy;
import com.zzteck.cardect.util.Utils;

import java.util.List;

public class ProgressActivity extends Activity implements OnClickListener {

	private Context mContext;

	private ImageView mIvBack ;
	
	private TextView mTvSecond ;

	private ImageView mIvProgress ;

	private LinearLayout mLLProgress ;
	
	private LinearLayout mLLBatteryInfo ;
	
	private TextView mTvBatteryInfo ;

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
		mTvMainInfo.setText("ANALYSING");
		mLLContinue = (LinearLayout)findViewById(R.id.ll_continue)  ;
		mLLProgress = (LinearLayout)findViewById(R.id.ll_progress) ;
		mLLBatteryInfo = (LinearLayout)findViewById(R.id.ll_battery_info) ;
		mIvProgress = (ImageView)findViewById(R.id.iv_progress) ;
		mTvSecond = (TextView)findViewById(R.id.tv_minute) ;
		mIvBack = (ImageView)findViewById(R.id.iv_back) ;
		mTvBatteryInfo = (TextView)findViewById(R.id.tv_battery_info) ;
		mLLBatteryInfo = (LinearLayout)findViewById(R.id.ll_battery) ;
		
		mLLContinue.setOnClickListener(this);
		mIvBack.setOnClickListener(this) ;
	}

	private String mInputString  ;
	
	private int mType ,mFlag ;
	
	private int mPosition ;
	
	private void initData() {
		Intent intent = getIntent() ;
		if(intent != null){
			mType = intent.getIntExtra("type", 0) ;
			mFlag = intent.getIntExtra("flag", 0) ;
			mInputString = intent.getStringExtra("value") ;
			mPosition = intent.getIntExtra("position", 0) ;
		}
	}

	private String mSelectedAddress;
	  
	private LeProxy mLeProxy;

	 private final BroadcastReceiver mGattReceiver = new BroadcastReceiver() {
	        @Override
	        public void onReceive(Context context, Intent intent) {
	            String address = intent.getStringExtra(LeProxy.EXTRA_ADDRESS);

	           if (LeProxy.ACTION_MTU_CHANGED.equals(intent.getAction())) {
	                int status = intent.getIntExtra(LeProxy.EXTRA_STATUS, BluetoothGatt.GATT_FAILURE);
	                if (status == BluetoothGatt.GATT_SUCCESS) {
	                    //ToastUtil.showMsg(getActivity(), "MTU has been " + mMtu);
	                } else {
	                   // ToastUtil.showMsg(getActivity(), "MTU update error: " + status);
	                }
	            }
	            
	            switch (intent.getAction()) {
                case LeProxy.ACTION_GATT_CONNECTED:
                    break;
                case LeProxy.ACTION_GATT_DISCONNECTED:
                    break;
                case LeProxy.ACTION_CONNECT_ERROR:
                    break;
                case LeProxy.ACTION_CONNECT_TIMEOUT:
                    break;
                case LeProxy.ACTION_GATT_SERVICES_DISCOVERED:
                    break;
                case LeProxy.ACTION_DATA_AVAILABLE : // 读取模块数据
                	//TODO 解析模块返回的电压高低，以及电池是否接触良好
                	byte[] data = intent.getByteArrayExtra(LeProxy.EXTRA_DATA);
                	String dataStr = DataUtil.byteArrayToHex(data);

                    if(dataStr.startsWith("41 45 52 43 10 45 01")){ //hight
                    		
                    	mLLBatteryInfo.setVisibility(View.VISIBLE) ;
                    	mLLProgress.setVisibility(View.GONE) ;

						mLLBatteryInfo.setBackgroundResource(R.mipmap.surfacecharge_one);
						mTvBatteryInfo.setTextColor(Color.WHITE);
                    	mTvBatteryInfo.setText("12.90V");
                    	
                    	/*Intent intent3 = new Intent(mContext,MainActivity.class) ;
						intent3.putExtra("flag", 1) ;//hight
						intent3.putExtra("vol", "13.25") ;
                    	startActivity(intent3) ;*/
                    	
                    }else if(dataStr.startsWith("41 45 52 43 10 45 02")){//low
                    	
                    	mLLBatteryInfo.setVisibility(View.VISIBLE) ;
                    	mLLProgress.setVisibility(View.GONE) ;

						mLLBatteryInfo.setBackgroundResource(R.mipmap.lowvoltage);
						mTvBatteryInfo.setTextColor(Color.BLACK);
						mTvBatteryInfo.setText("11.00V");

                    	/*Intent intent4 = new Intent(mContext,MainActivity.class) ;
						intent4.putExtra("flag", 2) ;//hight
						intent4.putExtra("vol", "13.25") ;
                    	startActivity(intent4) ;*/
                    }else if(dataStr.startsWith("41 45 52 43 10 57")){ //Second
						mLLBatteryInfo.setVisibility(View.GONE) ;
						mLLProgress.setVisibility(View.VISIBLE) ;
                    	String second = dataStr.substring(18,20) ;
						int sec = Integer.valueOf(second,16) ;
                    	mTvSecond.setText(sec+"") ;
                    }else if(dataStr.startsWith("41 45 52 43 10 52")){//CCA
                    	Intent intent1 = new Intent(mContext,ResultActivity.class) ;
						/*intent1.putExtra("value", mInputString) ;*/
						intent1.putExtra("message", "B"+dataStr+"|"+mInputString+"|"+mPosition+"|"+mType+"|"+mFlag) ;
						/*intent1.putExtra("position", mPosition) ;
						intent1.putExtra("type", mType) ;
						intent1.putExtra("flag", mFlag) ;*/
                    	startActivity(intent1) ;
						finish();
                    }else if(dataStr.startsWith("41 45 52 53 10 57")){ //second //41 45 52 53 10 57 3B 00 00 00 00 00 00 00 00 CD
						mLLBatteryInfo.setVisibility(View.GONE) ;
						mLLProgress.setVisibility(View.VISIBLE) ;
                    	String second = dataStr.substring(18,20) ;
						int sec = Integer.valueOf(second,16) ;
                    	mTvSecond.setText(sec+"") ;
                    	
                    }else if(dataStr.startsWith("41 45 52 53 10 52")){//start //41 45 52 53 10 52 04 E7 03 C0 00 B3 01 00 00 EF
                    	Intent intent2 = new Intent(mContext,ResultActivity.class) ;
						intent2.putExtra("message", "S"+dataStr) ;
                    	startActivity(intent2) ;
						finish();
                    }
                    
                	break ;
            }
	        }
	    };
	

	    private IntentFilter makeFilter() {
	        IntentFilter filter = new IntentFilter();
	        filter.addAction(LeProxy.ACTION_MTU_CHANGED);
	        filter.addAction(LeProxy.ACTION_DATA_AVAILABLE);
	        filter.addAction(LeProxy.ACTION_GATT_CONNECTED);
	        filter.addAction(LeProxy.ACTION_GATT_DISCONNECTED);
	        filter.addAction(LeProxy.ACTION_CONNECT_ERROR);
	        filter.addAction(LeProxy.ACTION_CONNECT_TIMEOUT);
	        filter.addAction(LeProxy.ACTION_GATT_SERVICES_DISCOVERED);
	        filter.addAction(LeProxy.ACTION_DATA_AVAILABLE) ;
	        
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
	protected void onDestroy() {
		super.onDestroy();
		LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mGattReceiver);
	}

	private LinearLayout mLLContinue ;
	
	private TextView mIvBatteryInfo ;
	
	private LinearLayout mLLBattery ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_progress);
		mContext = ProgressActivity.this;
		mLeProxy = LeProxy.getInstance();
		initView();
		initData();
		LocalBroadcastManager.getInstance(mContext).registerReceiver(mGattReceiver, makeFilter());
		Animation rotate = AnimationUtils.loadAnimation(this, R.anim.process_anim);
		mIvProgress.setAnimation(rotate);

		List<BluetoothDevice> deviceList = mLeProxy.getConnectedDevices();
		if(!deviceList.isEmpty()){
			mSelectedAddress = deviceList.get(0).getAddress() ;
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			String sendMessage = "4145414110"+"0000000000000000000018";
		//	String checkSum = Utils.makeChecksum(sendMessage) ;
			send(sendMessage) ;
			finish() ;
			break;
		case R.id.ll_continue :

			break ;
		default:
			break;
		}
	}

}
