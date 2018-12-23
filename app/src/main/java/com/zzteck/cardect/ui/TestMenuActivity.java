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
import com.zzteck.cardect.util.MyDialog;

public class TestMenuActivity extends Activity implements OnClickListener {

	private Context mContext;

	private ImageView mIvBattery, mIvEnger, mIvBack;

	private TextView mTvMainInfo ;

	private void initView() {
		mTvMainInfo = (TextView) findViewById(R.id.tv_main) ;
		mTvMainInfo.setText("SELECT TEST");
		mIvBattery = (ImageView) findViewById(R.id.iv_battery);
		mIvEnger = (ImageView) findViewById(R.id.iv_enger);
		mIvBack = (ImageView) findViewById(R.id.iv_back);
		mIvBattery.setOnClickListener(this);
		mIvEnger.setOnClickListener(this);
		mIvBack.setOnClickListener(this);

	}

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

	private void initData() {

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_menu);
		mContext = TestMenuActivity.this;
		initView();
		initData();
	}

	@Override
	public void onBackPressed() {
		//super.onBackPressed();
		final MyDialog dialog1 = new MyDialog(mContext) ;
		dialog1.setTitle("") ;
		dialog1.setContent("DO NOT CONTINE TEST") ;

		dialog1.setOnPositiveListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				MApplication.mFileName = "" ;
				finish() ;
			}
		}) ;

		dialog1.setOnNegativeListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				dialog1.dismiss() ;
			}
		}) ;

		dialog1.show() ;
	//	return true ;
	}

	@Override
	public void onClick(View v) {
		Intent intent = null ;
		switch (v.getId()) {
		case R.id.iv_back :
			final MyDialog dialog1 = new MyDialog(mContext) ;
			dialog1.setTitle("") ;
			dialog1.setContent("DISCONTINUE TEST") ;

			dialog1.setOnPositiveListener(new OnClickListener() {

				@Override
				public void onClick(View view) {
					MApplication.mFileName = "" ;
					finish() ;
				}
			}) ;

			dialog1.setOnNegativeListener(new OnClickListener() {

				@Override
				public void onClick(View view) {
					dialog1.dismiss() ;
				}
			}) ;

			dialog1.show() ;
			break ;
		case R.id.iv_battery:
			intent = new Intent(mContext,BatteryTestActivity.class) ;
			startActivity(intent) ;
			break;
		case R.id.iv_enger:
			intent = new Intent(mContext,EngerActivity.class) ;
			startActivity(intent) ;
			break;

		default:
			break;
		}
	}
}
