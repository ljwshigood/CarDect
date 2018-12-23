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
import com.zzteck.cardect.util.LeProxy;
import com.zzteck.cardect.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class CCASetActivity extends Activity implements OnClickListener {

	private Context mContext;

	private ImageView mIvBack ;
	
	private EditText mEtValue ;

	private GridView mGvParam ;
	
	private LinearLayout mLLStartTest ;
	
	private CCASetAdapter mCCASetAdapter;

	private int[] resNomal = new int[]{R.mipmap.cca_a,R.mipmap.en1_a,R.mipmap.en2_a,R.mipmap.din_a,
			R.mipmap.jis_a,R.mipmap.ca_a,R.mipmap.sae_a,R.mipmap.iec_a,R.mipmap.mca_a,R.mipmap.unknown_a} ;
	
	
	private int[] resPress = new int[]{R.mipmap.cca_b,R.mipmap.en1_b,R.mipmap.en2_b,R.mipmap.din_b,
			R.mipmap.jis_b,R.mipmap.ca_b,R.mipmap.sae_b,R.mipmap.iec_b,R.mipmap.mca_b,R.mipmap.unknown_b} ;
	

	private LinearLayout mLLSetValue ;

	private TextView mTvUnit ;

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
		mTvMainInfo.setText("SELECT RATING");
		mTvUnit = (TextView)findViewById(R.id.tv_unit) ;
		mLLStartTest = (LinearLayout)findViewById(R.id.ll_start_test) ;
		mLLSetValue = (LinearLayout)findViewById(R.id.ll_set_value) ;
		mGvParam = (GridView)findViewById(R.id.gv_battery_param) ;
		mIvBack = (ImageView)findViewById(R.id.iv_back) ;
		mEtValue = (EditText)findViewById(R.id.et_value) ;
		mIvBack.setOnClickListener(this) ;
		mLLStartTest.setOnClickListener(this) ;
	}

	private List<BatteryBean> mBatteryList = new ArrayList<>() ;

	private int mType ,mFlag ;
	
	private String mTypeString ,mFlagString ;

	private String mMessage = "00";

	private int mCurrentPosition ;
	
	private void initData() {
		
		Intent intent = getIntent() ;
		
		if(intent != null){
			mType = intent.getIntExtra("type", 0) ;
			mFlag = intent.getIntExtra("flag", 0) ;
			
			switch (mType) {
			case 0:
				mTypeString = "00" ;
				break;
			case 1:
				mTypeString = "01" ;
				break;
			case 2:
				mTypeString = "02" ;
				break;
			default:
				break;
			}
			
			switch (mFlag) {
			case 0:
				mFlagString = "00";
				break;
			case 1:
				mFlagString = "01";
				break;
			case 2:
				mFlagString = "02";
				break;
			case 3:
				mFlagString = "03";
				break;
			case 4:
				mFlagString = "04";
				break;	
			case 5:
				mFlagString = "05";
				break;
			default:
				break;
			}
			
		}
		
		for(int i = 0 ;i < resNomal.length;i++){
			BatteryBean bean = new BatteryBean() ;
			bean.setRes(resNomal[i]) ;
			bean.setResPress(resPress[i]) ;
			mBatteryList.add(bean) ;
		}

		mCCASetAdapter = new CCASetAdapter(mContext, mBatteryList) ;
		mGvParam.setAdapter(mCCASetAdapter) ;
		mGvParam.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				for(int i = 0 ;i < mBatteryList.size();i++){
					BatteryBean bean = mBatteryList.get(i) ;
					if(i == position){
						bean.setSelect(true) ;
					}else{
						bean.setSelect(false) ;
					}
				}
				
				mCCASetAdapter.notifyDataSetChanged();
				
				mCurrentPosition = position ;
				switch (position) {
				case 0:
					mMessage = "00" ;
					mLLSetValue.setVisibility(View.VISIBLE) ;
					mLLStartTest.setVisibility(View.VISIBLE) ;
					mTvUnit.setText("CCA");
					break;
				case 1:
					mMessage = "01" ;
					mLLSetValue.setVisibility(View.VISIBLE) ;
					mLLStartTest.setVisibility(View.VISIBLE) ;
					mTvUnit.setText("EN 1");
					break;
				case 2:
					mMessage = "02" ;
					mLLSetValue.setVisibility(View.VISIBLE) ;
					mLLStartTest.setVisibility(View.VISIBLE) ;
					mTvUnit.setText("EN 2");
					break;
				case 3:
					mMessage = "03" ;
					mLLSetValue.setVisibility(View.VISIBLE) ;
					mLLStartTest.setVisibility(View.VISIBLE) ;
					mTvUnit.setText("DIN");
					break;
				case 4:
					mMessage = "04" ;
					mLLSetValue.setVisibility(View.VISIBLE) ;
					mLLStartTest.setVisibility(View.VISIBLE) ;
					mTvUnit.setText("JIS#");
					break;
				case 5:
					mMessage = "05" ;
					mLLSetValue.setVisibility(View.VISIBLE) ;
					mLLStartTest.setVisibility(View.VISIBLE) ;
					mTvUnit.setText("CA");
					break;
				case 6:
					mMessage = "06" ;
					mLLSetValue.setVisibility(View.VISIBLE) ;
					mLLStartTest.setVisibility(View.VISIBLE) ;
					mTvUnit.setText("SAE");
					break;
				case 7:
					mMessage = "07" ;
					mLLSetValue.setVisibility(View.VISIBLE) ;
					mLLStartTest.setVisibility(View.VISIBLE) ;
					mTvUnit.setText("IEC");
					break;
				case 8:
					mMessage = "08" ;
					mLLSetValue.setVisibility(View.VISIBLE) ;
					mLLStartTest.setVisibility(View.VISIBLE) ;
					mTvUnit.setText("MCA");
					break;
				case 9:
					mMessage = "09" ;
					mLLSetValue.setVisibility(View.GONE) ;
					mLLStartTest.setVisibility(View.GONE) ;
					mTvUnit.setText("CCA");
					break;

				default:
					break;
				}
				
				if(position == 9){

					if(!TextUtils.isEmpty(mMessage)){
						String sendMessage = "4145414310"+mTypeString+mMessage+"0000"+mFlagString+"0000000000" ;
						String checkSum = Utils.makeChecksum(sendMessage) ;
						send(sendMessage+checkSum) ;
						Intent intent = new Intent(mContext,ProgressActivity.class) ;
						intent.putExtra("position", position) ;
						
						startActivity(intent) ;
					}
					mLLSetValue.setVisibility(View.INVISIBLE);
				}else{
					mLLSetValue.setVisibility(View.VISIBLE);
				}
			}
		}) ;
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
		setContentView(R.layout.activity_cca_set);
		mContext = CCASetActivity.this;
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
		case R.id.ll_start_test :

			if(!TextUtils.isEmpty(mEtValue.getText().toString().trim())){

				if(Integer.valueOf(mEtValue.getText().toString().trim()) >= 100 && Integer.valueOf(mEtValue.getText().toString().trim()) <= 2000){
					String hex = Utils.int2Hex(Integer.valueOf(mEtValue.getText().toString().trim())) ;
					switch (hex.length()) {
						case 1:
							hex = "000"+hex ;
							break;
						case 2:
							hex = "00"+hex ;
							break;
						case 3:
							hex = "0"+hex ;
							break;

						default:
							break;
					}
					//hex ="0320" ;
					String sendMessage = "4145414310"+mTypeString+mMessage+hex+mFlagString+"0000000000" ;
					String checkSum = Utils.makeChecksum(sendMessage) ;

					send(sendMessage+checkSum) ;
					Intent intent = new Intent(mContext,ProgressActivity.class) ;
					intent.putExtra("value", mEtValue.getText().toString().trim()) ;
					intent.putExtra("type", mType) ;
					intent.putExtra("flag", mFlag) ;
					intent.putExtra("position", mCurrentPosition) ;
					startActivity(intent) ;
				}
			}
			
			break ;
		default:
			break;
		}
	}

}
