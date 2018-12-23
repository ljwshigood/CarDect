package com.zzteck.cardect.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.zzteck.cardect.R;

public class BatteryTestActivity extends Activity implements OnClickListener {

	private Context mContext;

	private ImageView mIvINCar, mIvBeforeChange,mIvAfterChange ,mIvBack ;

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
		mTvMainInfo.setText("BATTERY");
		mIvINCar = (ImageView) findViewById(R.id.iv_in_car);
		mIvBeforeChange = (ImageView) findViewById(R.id.iv_before_change);
		mIvAfterChange = (ImageView) findViewById(R.id.iv_after_change);
		mIvBack = (ImageView)findViewById(R.id.iv_back) ;
		mIvINCar.setOnClickListener(this) ;	
		mIvBeforeChange.setOnClickListener(this) ;
		mIvAfterChange.setOnClickListener(this) ;
		mIvBack.setOnClickListener(this);
	}

	private void initData() {

	}

	@Override
	protected void onResume() {
		super.onResume();

		setImageViewConnectStatus() ;

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_battery_test);
		mContext = BatteryTestActivity.this;
		initView();
		initData();
	}

	@Override
	public void onClick(View v) {
		Intent intent = null ;
		switch (v.getId()) {
		case R.id.iv_back :
			finish();
			break ;
		case R.id.iv_in_car:
			intent = new Intent(mContext,BatteryTypeActivity.class) ;
			intent.putExtra("type", 0) ;
			startActivity(intent) ;
			break;
		case R.id.iv_before_change:
			intent = new Intent(mContext,BatteryTypeActivity.class) ;
			intent.putExtra("type", 1) ;
			startActivity(intent) ;
			break;
			
		case R.id.iv_after_change:
			intent = new Intent(mContext,BatteryTypeActivity.class) ;
			intent.putExtra("type", 2) ;
			startActivity(intent) ;
			break;
		default:
			break;
		}
	}

}
