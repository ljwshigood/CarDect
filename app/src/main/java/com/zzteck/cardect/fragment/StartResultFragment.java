package com.zzteck.cardect.fragment;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zzteck.cardect.R;
import com.zzteck.cardect.ui.DeviceListActivity;
import com.zzteck.cardect.ui.KeyInTouchActivity;
import com.zzteck.cardect.ui.MApplication;
import com.zzteck.cardect.ui.PrintReportActivity;
import com.zzteck.cardect.ui.ResultActivity;
import com.zzteck.cardect.ui.TestMenuActivity;
import com.zzteck.cardect.util.FileUtils;
import com.zzteck.cardect.util.LeProxy;
import com.zzteck.cardect.util.Utils;
import com.zzteck.cardect.view.WJViewPaper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class StartResultFragment extends Fragment implements OnClickListener {

	private Context mContext;

	private TextView mTvV,mTvVolit,mTvTime ;
	
	private ProgressBar mPbVRed,mPbVYellow ,mPbVGreen ,mPbVDarkGreen;
	
	private ProgressBar mPbVolitRed, mPbVolitYellow ,mPbVolitGreen ,mPbVolitDarkGreen ;
	
	private ProgressBar mPbTimeRed,mPbTimeYellow,mPbTimeGreen ,mPbTimeBlue;
	
	private TextView mTvTitle ;
	
	private ImageView mIvStatus ;
	
	private LinearLayout mLLSave ,mLLPrint ;
	
	private String mMessage ;
	
	private WJViewPaper mViewPaper ;

	public StartResultFragment(){
		super() ;
	}

	private TextView mTvFileName ,mTvTestTime  ;
	
	private void initView(View view) {
		mIvStatus  = (ImageView) view.findViewById(R.id.iv_start_status) ;
		mTvTestTime = (TextView) view.findViewById(R.id.tv_test_time) ;
		mTvFileName = (TextView) view.findViewById(R.id.tv_test_result) ;
		mTvTitle = (TextView)view.findViewById(R.id.tv_start_test) ;
		mViewPaper = (WJViewPaper)view.findViewById(R.id.content) ;
		mTvV = (TextView)view.findViewById(R.id.tv_v) ;
		mTvVolit = (TextView)view.findViewById(R.id.tv_volt) ;
		mTvTime = (TextView)view.findViewById(R.id.tv_time) ;
		
		mPbVRed = (ProgressBar)view.findViewById(R.id.pb_v_red) ;
		mPbVYellow = (ProgressBar)view.findViewById(R.id.pb_v_yellow) ;
		mPbVGreen = (ProgressBar)view.findViewById(R.id.pb_v_green) ;
		mPbVDarkGreen = (ProgressBar)view.findViewById(R.id.pb_v_dark_green) ;
		
		mPbVolitRed = (ProgressBar)view.findViewById(R.id.pb_volt_red) ;
		mPbVolitYellow = (ProgressBar)view.findViewById(R.id.pb_volt_yellow) ;
		mPbVolitGreen = (ProgressBar)view.findViewById(R.id.pb_volt_green) ;
		mPbVolitDarkGreen = (ProgressBar)view.findViewById(R.id.pb_volt_dark_green) ;
		
		mPbTimeRed = (ProgressBar)view.findViewById(R.id.pb_time_red) ;
		mPbTimeYellow = (ProgressBar)view.findViewById(R.id.pb_time_yellow) ;
		mPbTimeGreen = (ProgressBar)view.findViewById(R.id.pb_time_green) ;
		mPbTimeBlue = (ProgressBar)view.findViewById(R.id.pb_time_dark_blue) ;
		
		mLLSave  = (LinearLayout)view.findViewById(R.id.ll_save) ;
		mLLPrint = (LinearLayout)view.findViewById(R.id.ll_print) ;
		mLLSave.setOnClickListener(this) ;
		mLLPrint.setOnClickListener(this) ;
		
	}

	public void initData(String message) {
		/*Intent intent = getIntent() ;
		if(intent != null){
			mMessage = intent.getStringExtra("message") ;
		}*/

		//TODO 数据显示
		//41 45 52 53 10 52 04 E7 03 C0 00 B3 01 00 00 EF
		//41455253105204E703C000B3010000EF

		if(mResultType == 0){
			if(TextUtils.isEmpty(MApplication.mFileName)){
				mTvFileName.setText("");
			}else{
				mTvFileName.setText(MApplication.mFileName);
			}
		}else {
			if(!TextUtils.isEmpty(mFileName)){
				mTvFileName.setText(mFileName);
			}

			if(!TextUtils.isEmpty(mTestTime)){
				mTvTestTime.setText(mTestTime) ;
			}
		}

		if(TextUtils.isEmpty(mTestTime)){
			mTvTestTime.setText(""+Utils.getTime());
		}else{
			mTvTestTime.setText(mTestTime);
		}

		mMessage = mMessage.replace(" ","") ;
		String volt = mMessage.substring(13,17) ;
		Integer voltInt = Integer.valueOf(volt, 16) ;
		
		String drop = mMessage.substring(17,21) ;
		Integer dropInt = Integer.valueOf(drop, 16) ;

		String strSec = mMessage.substring(21,25) ;
		Integer secInt = Integer.valueOf( strSec, 16)*100 ;

		String strResult = mMessage.substring(25,27) ;
		
		//12.90 V
		String vString = voltInt.toString().substring(0, voltInt.toString().length() - 2)+"."+voltInt.toString().subSequence(voltInt.toString().length() - 2,voltInt.toString().length()) ;
		float vInt = Float.valueOf(vString) ;
		if(vInt <= 11.99f){
			mPbVYellow.setVisibility(View.GONE);
			mPbVGreen.setVisibility(View.GONE);
			mPbVDarkGreen.setVisibility(View.GONE);
			mPbVGreen.setVisibility(View.VISIBLE);
			mPbVRed.setProgress((int)vInt);
		}else if(vInt >= 12.00f && vInt <= 12.39f){
			mPbVRed.setVisibility(View.GONE);
			mPbVGreen.setVisibility(View.GONE);
			mPbVDarkGreen.setVisibility(View.GONE);
			mPbVYellow.setVisibility(View.VISIBLE);
			mPbVYellow.setProgress((int)vInt);
		}else if(vInt >= 12.40f && vInt <= 12.59f){
			mPbVRed.setVisibility(View.GONE);
			mPbVYellow.setVisibility(View.GONE);
			mPbVDarkGreen.setVisibility(View.GONE);
			mPbVGreen.setVisibility(View.VISIBLE);
			mPbVGreen.setProgress((int)vInt);
		}else if(vInt >= 12.60f){
			mPbVRed.setVisibility(View.GONE);
			mPbVYellow.setVisibility(View.GONE);
			mPbVGreen.setVisibility(View.GONE);
			mPbVDarkGreen.setVisibility(View.VISIBLE);
			mPbVDarkGreen.setProgress((int)vInt);
		}
		
		mTvV.setText(vString +" V") ;
		
		String dString = dropInt.toString().substring(0, dropInt.toString().length() - 2)+"."+dropInt.toString().subSequence(dropInt.toString().length() - 2, dropInt.toString().length()) ;
		float dFloat = Float.valueOf(dString) ;
		
		if(dFloat <= 9.59f){
			mPbVolitRed.setVisibility(View.VISIBLE);
			mPbVolitYellow.setVisibility(View.GONE);
			mPbVolitGreen.setVisibility(View.GONE);
			mPbVolitDarkGreen.setVisibility(View.GONE);
			
			mPbVolitRed.setProgress((int)dFloat);
			
		}else{
			mPbVolitRed.setVisibility(View.GONE);
			mPbVolitYellow.setVisibility(View.GONE);
			mPbVolitGreen.setVisibility(View.GONE);
			mPbVolitDarkGreen.setVisibility(View.VISIBLE);
			mPbVolitDarkGreen.setProgress((int)dFloat);
		}
		
		mTvVolit.setText(dString + " V") ;
		
		mTvTime.setText(Float.valueOf(secInt)/ 1000f  +" S") ;
		int temp = (int) (Float.valueOf(secInt)/ 1000f+ 30);
		mPbTimeBlue.setProgress(temp);
		switch (Integer.valueOf(strResult)) {
		case 1:
			mIvStatus.setImageResource(R.mipmap.result_ok);
			mTvTitle.setTextColor(Color.GREEN);
			break;
		case 2:
			mIvStatus.setImageResource(R.mipmap.result_not_ok);
			mTvTitle.setTextColor(mContext.getResources().getColor(R.color.red));
			break;
		case 3:
			mIvStatus.setImageResource(R.mipmap.result_not_ok);
			mTvTitle.setTextColor(mContext.getResources().getColor(R.color.red));
			break;

		default:
			break;
		}
		
	}

	private View mMainView ;

	private String mPreFileString= "" ;

	private String mPreCmdString= "" ;

	private void getPreTxtFile(){
		mPreFileString = FileUtils.loadFromSDFile(MApplication.mFileName+".txt") ;
		mPreCmdString = FileUtils.loadFromSDFile(MApplication.mFileName+"json.txt") ;
	}
	private ResultActivity mResultActivity ;

	private int mResultType ;

	private String mFileName ;

	private String mTestTime ;

	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
		mMainView = inflater.inflate(R.layout.activity_start_result, container,false);
		mContext = getActivity();

		mResultActivity = (ResultActivity) mContext ;

		initView(mMainView);

		mLeProxy = LeProxy.getInstance();

		Bundle bundle = getArguments();

		if (bundle != null) {
			mMessage = bundle.getString("message");
			mResultType = bundle.getInt("type") ;
			mFileName = bundle.getString("filename") ;
			mTestTime = bundle.getString("test_time") ;
		}
		initData(mMessage) ;
		getPreTxtFile() ;

		return mMainView ;
	}

	private String mPrintString ;

	public byte[] strToByteArray(String str) {
		if (str == null) {
			return null;
		}
		byte[] byteArray = str.getBytes();
		return byteArray;
	}

	private LeProxy mLeProxy;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 2211){
			if(!TextUtils.isEmpty(mPrintString)){
				Intent intent = new Intent() ;
				intent.putExtra("print",strToByteArray(mPrintString)) ;
				getActivity().sendBroadcast(intent);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_save:

			if(mResultType == 1){
				getActivity().finish() ;
				return ;
			}

			if(TextUtils.isEmpty(MApplication.mFileName)){
				MApplication.retMessage = mMessage ;
				Intent intent1 = new Intent(mContext,KeyInTouchActivity.class) ;
				intent1.putExtra("type", 1) ;
				getActivity().startActivity(intent1) ;
			}else{
				mResultActivity.saveStartResultFile();
				mResultActivity.onBackPressed();
			}

			break;
		case R.id.ll_print :

			String status = "" ;
			String strResult = mMessage.substring(25,27) ;
			if(Integer.valueOf(strResult) == 1){
				status = "OK" ;
			}else{
				status = "NOT OK" ;
			}
			mPrintString = "Tested on: "+Utils.getTime()+"\n"+
					"Starter Test:\n"+
					"Battery Volt: "+mTvV.getText().toString().trim()+" \n"+
					"Cranking Volt: "+mTvVolit.getText().toString().trim()+" \n"+
					"Cranking Period: "+mTvTime.getText().toString().trim() +" \n"+
					"Result: "+status ;

			List<BluetoothDevice> deviceList = mLeProxy.getConnectedDevices();
			if(!deviceList.isEmpty()){
				mLeProxy.disconnect(deviceList.get(0).getAddress());
			}
			Intent intent = new Intent(mContext,PrintReportActivity.class) ;
			intent.putExtra("print",mPrintString) ;
			startActivityForResult(intent,2211);
			break ;

		default:
			break;
		}
	}

}
