package com.zzteck.cardect.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ble.api.DataUtil;
import com.zzteck.cardect.R;
import com.zzteck.cardect.adapter.BatteryTypeAdapter;
import com.zzteck.cardect.bean.BatteryBean;
import com.zzteck.cardect.util.LeProxy;
import com.zzteck.cardect.util.MyDialog;

public class BatteryTypeActivity extends Activity implements OnClickListener {

	private Context mContext;

	private GridView mGvBattery ;
	
	private BatteryTypeAdapter mBatteryAdapter ;
	
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
		mTvMainInfo.setText("SELECT BATTERY TYPE");
		mIvBack = (ImageView)findViewById(R.id.iv_back) ;
		mGvBattery = (GridView)findViewById(R.id.gv_battery_param) ;
		mIvBack.setOnClickListener(this) ;
	}
	
	private List<BatteryBean> mBatteryList = new ArrayList<>() ;

	private int[] res = new int[]{R.mipmap.sli_wet,R.mipmap.cv,R.mipmap.ic_agm,
			R.mipmap.ic_agm_spiral,
			R.mipmap.ic_efb,R.mipmap.ic_get_cell} ;
	
	
	private int mFlag ;
	
	private int mCurrentPosition ;
	
	private void initData() {
		
		Intent intent = getIntent() ;
		if(intent != null){
			mFlag = intent.getIntExtra("type", 0) ;
		}
		
		for(int i = 0 ;i < res.length;i++){
			BatteryBean bean = new BatteryBean() ;
			bean.setRes(res[i]) ;
			mBatteryList.add(bean) ;
		}
		mBatteryAdapter = new BatteryTypeAdapter(mContext, mBatteryList) ;
		
		mGvBattery.setAdapter(mBatteryAdapter) ;
		mGvBattery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
				
				mCurrentPosition = position ;
				//send("");

				Intent intent = null ;
				switch (mCurrentPosition) {
				case 0:
					intent = new Intent(mContext,CCASetActivity.class) ;
					intent.putExtra("type", mFlag) ;
					intent.putExtra("flag", 0) ;
					mContext.startActivity(intent) ;
					break;
				case 1:
					intent = new Intent(mContext,CCASetActivity.class) ;
					intent.putExtra("type", mFlag) ;
					intent.putExtra("flag", 1) ;
					mContext.startActivity(intent) ;
					break;
				case 2:
					intent = new Intent(mContext,CCASetActivity.class) ;
					intent.putExtra("type", mFlag) ;
					intent.putExtra("flag", 2) ;
					mContext.startActivity(intent) ;
					break;
				case 3:
					intent = new Intent(mContext,CCASetActivity.class) ;
					intent.putExtra("type", mFlag) ;
					intent.putExtra("flag", 3) ;
					mContext.startActivity(intent) ;
					break;
				case 4:
					intent = new Intent(mContext,CCASetActivity.class) ;
					intent.putExtra("type", mFlag) ;
					intent.putExtra("flag", 4) ;
					mContext.startActivity(intent) ;
					break;
				case 5:
					intent = new Intent(mContext,CCASetActivity.class) ;
					intent.putExtra("type", mFlag) ;
					intent.putExtra("flag", 5) ;
					mContext.startActivity(intent) ;
					break;
				default:
					break;
				}
			}
			
		}) ;
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
	 
	 private String mSelectedAddress;
	  
	  private LeProxy mLeProxy;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_battery_type);
		mContext = BatteryTypeActivity.this;
		initView();
		initData();
		mLeProxy = LeProxy.getInstance();
		
		List<BluetoothDevice> deviceList = mLeProxy.getConnectedDevices();
		if(!deviceList.isEmpty()){
			mSelectedAddress = deviceList.get(0).getAddress() ;
		}
		
		//LocalBroadcastManager.getInstance(mContext).registerReceiver(mGattReceiver, makeFilter());
		
	}
	
	private final BroadcastReceiver mGattReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String address = intent.getStringExtra(LeProxy.EXTRA_ADDRESS);
            if (LeProxy.ACTION_DATA_AVAILABLE.equals(intent.getAction())) {
            	
               /* if (address.equals(mSelectedAddress)) {
                  //  displayRxData(intent);
                }*/
            	/*
				Intent intent = null ;
				switch (mCurrentPosition) {
				case 0:
					intent = new Intent(mContext,CCASetActivity.class) ;
					intent.putExtra("type", mFlag) ;
					intent.putExtra("flag", 0) ;
					mContext.startActivity(intent) ;
					break;
				case 1:
					intent = new Intent(mContext,CCASetActivity.class) ;
					intent.putExtra("type", mFlag) ;
					intent.putExtra("flag", 1) ;
					mContext.startActivity(intent) ;
					break;
				case 2:
					intent = new Intent(mContext,CCASetActivity.class) ;
					intent.putExtra("type", mFlag) ;
					intent.putExtra("flag", 2) ;
					mContext.startActivity(intent) ;
					break;
				case 3:
					intent = new Intent(mContext,CCASetActivity.class) ;
					intent.putExtra("type", mFlag) ;
					intent.putExtra("flag", 3) ;
					mContext.startActivity(intent) ;
					break;
				case 4:
					intent = new Intent(mContext,CCASetActivity.class) ;
					intent.putExtra("type", mFlag) ;
					intent.putExtra("flag", 4) ;
					mContext.startActivity(intent) ;
					break;
				case 5:
					intent = new Intent(mContext,CCASetActivity.class) ;
					intent.putExtra("type", mFlag) ;
					intent.putExtra("flag", 5) ;
					mContext.startActivity(intent) ;
					break;
				case 6:
					intent = new Intent(mContext,CCASetActivity.class) ;
					intent.putExtra("type", mFlag) ;
					mContext.startActivity(intent) ;
					break;

				default:
					break;
				}*/
            	
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
    

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;

		default:
			break;
		}
	}

}
