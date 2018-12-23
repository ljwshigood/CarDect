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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.zzteck.cardect.R;
import com.zzteck.cardect.bean.BatteryResultBean;
import com.zzteck.cardect.impl.IPrintListener;
import com.zzteck.cardect.ui.DeviceListActivity;
import com.zzteck.cardect.ui.KeyInTouchActivity;
import com.zzteck.cardect.ui.MApplication;
import com.zzteck.cardect.ui.PrintReportActivity;
import com.zzteck.cardect.ui.ResultActivity;
import com.zzteck.cardect.ui.TestMenuActivity;
import com.zzteck.cardect.util.FileUtils;
import com.zzteck.cardect.util.LeProxy;
import com.zzteck.cardect.util.Utils;

import java.io.IOException;
import java.util.List;

public class BatteryResultFragment extends Fragment implements OnClickListener {

	private Context mContext;

	private ProgressBar mPbVRed,mPbVYellow,mPbVDarkGreen,mPbVGreen ;
	
	private ProgressBar mPbVolitRed,mPbVolitYellow,mPbVolitGreen ,mPbVolitDarkGreen;
	
	private ProgressBar mPbTimeRed,mPbTimeYellow,mPbTimeGreen,mPbTimeDarkGreen ;
	
	private LinearLayout mLLSave ,mLLPrint ;
	
	private TextView mTvResult ;
	
	private TextView mTvStatus ;
	
	private ImageView mIvDevice ;
	
	private TextView mTvV,mTvVolit,mTvTime ;
	
	private ProgressBar mPbTitleYellow,mPbTitleGreen ,mPbTitleDarkGreen ;

	public IPrintListener getmIPrintListener() {
		return mIPrintListener;
	}

	public void setmIPrintListener(IPrintListener mIPrintListener) {
		this.mIPrintListener = mIPrintListener;
	}

	private IPrintListener mIPrintListener ;

	public BatteryResultFragment(){
		super() ;
	}
	
	private ImageView mIvResult ;
	
	private void initView(View view) {
		mLLCapcity = (LinearLayout)view.findViewById(R.id.ll_setcapacity) ;
		mLLStatus = (LinearLayout)view.findViewById(R.id.ll_status) ;
		mRlPbThree = (RelativeLayout)view.findViewById(R.id.rl_pb_three) ;
		mIvResult = (ImageView)view.findViewById(R.id.iv_result) ;
		mTvTestTime = (TextView)view.findViewById(R.id.tv_test_time) ;
		mTvFileName = (TextView)view.findViewById(R.id.tv_test_result) ;
		mTvV = (TextView)view.findViewById(R.id.tv_v) ;
		mTvVolit = (TextView)view.findViewById(R.id.tv_volt) ;
		mTvTime = (TextView)view.findViewById(R.id.tv_time) ;
		
		mTvValue = (TextView)view.findViewById(R.id.tv_value) ;
		mTvResult =(TextView)view.findViewById(R.id.tv_battery_result) ;
		mIvDevice = (ImageView)view.findViewById(R.id.iv_device) ;
		mTvStatus = (TextView)view.findViewById(R.id.tv_status) ;
		
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
		mPbTimeDarkGreen = (ProgressBar)view.findViewById(R.id.pb_time_dark_green) ;
		
		mPbTitleYellow = (ProgressBar)view.findViewById(R.id.pb_title_yellow) ;
		mPbTitleGreen = (ProgressBar)view.findViewById(R.id.pb_title_green) ;
		mPbTimeDarkGreen = (ProgressBar)view.findViewById(R.id.pb_title_dark_green) ;
		
		mLLSave= (LinearLayout)view.findViewById(R.id.ll_save) ;
		mLLPrint = (LinearLayout)view.findViewById(R.id.ll_print) ;
		
		mLLSave.setOnClickListener(this) ;
		mLLPrint.setOnClickListener(this) ;
		
	}

	private String mMessage ;
	
	private String mInputValue ;
	
	private TextView mTvValue ;
	
	private int mPosition ;
	
	private int mType ,mFlag ;
	
	private String mUnit ;
	
	private void setTimeProgress(int progress){
		if(progress <= 44){
			mPbTimeRed.setVisibility(View.VISIBLE);
			mPbTimeYellow.setVisibility(View.GONE);
			mPbTimeGreen.setVisibility(View.GONE);
			mPbTimeDarkGreen.setVisibility(View.GONE);

			mPbTimeRed.setProgress((int)(Float.valueOf(Integer.valueOf(progress+"",16)) / 100f) + 40);

		}else if(progress >= 45 && progress <= 59){

			mPbTimeRed.setVisibility(View.GONE);
			mPbTimeYellow.setVisibility(View.VISIBLE);
			mPbTimeGreen.setVisibility(View.GONE);
			mPbTimeDarkGreen.setVisibility(View.GONE);

			mPbTimeYellow.setProgress((int)(Float.valueOf(Integer.valueOf(progress+"",16)) / 100f) + 40);

		}else if(progress >= 60 && progress <= 69){
			mPbTimeRed.setVisibility(View.GONE);
			mPbTimeYellow.setVisibility(View.GONE);
			mPbTimeGreen.setVisibility(View.VISIBLE);
			mPbTimeDarkGreen.setVisibility(View.GONE);

			mPbTimeGreen.setProgress((int)(Float.valueOf(Integer.valueOf(progress+"",16)) / 100f) + 40);
		}else if(progress >= 70){
			mPbTimeRed.setVisibility(View.GONE);
			mPbTimeYellow.setVisibility(View.GONE);
			mPbTimeGreen.setVisibility(View.GONE);
			mPbTimeDarkGreen.setVisibility(View.VISIBLE);

			mPbTimeDarkGreen.setProgress((int)(Float.valueOf(Integer.valueOf(progress+"",16)) / 100f) + 40);
		}

	}
	
	
	public void initData(String message,String inputValue,int position,int type,int flag) {
		/*Intent intent = getIntent() ;
		if(intent != null){
			mMessage = intent.getStringExtra("message") ;
			mInputValue = intent.getStringExtra("value") ;
			mPosition= intent.getIntExtra("position", 0) ;
			mType= intent.getIntExtra("type", 0) ;
			mFlag = intent.getIntExtra("flag", 0) ;
		}*/
		
		/*mMessage = message ;
		mInputValue = inputValue ;
		mPosition= position ;
		mType= type ;
		mFlag = flag ;*/
		
		switch (mPosition) {
		case 0:
			mTvValue.setText(mInputValue+" CCA") ;
			mUnit = "CCA" ;
			break;
		case 1:
			mTvValue.setText(mInputValue+"A EN1") ;
			mUnit = "EN1" ;
			break;
		case 2:
			mTvValue.setText(mInputValue+"A EN2") ;
			mUnit = "EN2" ;
			break;
		case 3:
			mTvValue.setText(mInputValue+"A DIN") ;
			mUnit = "DIN" ;
			break;
		case 4:
			mTvValue.setText(mInputValue+" JIS#") ;
			mUnit = "CCA" ;
			break;
		case 5:
			mTvValue.setText(mInputValue+" CA") ;
			mUnit = "CA" ;
			break;
		case 6:
			mTvValue.setText(mInputValue+"A SAE") ;
			mUnit = "SAE" ;
			break;
		case 7:
			mTvValue.setText(mInputValue+"A IEC") ;
			mUnit = "IEC" ;
			break;
		case 8:
			mTvValue.setText(mInputValue+" MCA") ;
			mUnit = "MCA" ;
			break;
		case 9:
			mTvValue.setText(mInputValue+" CCA") ;
			mUnit = "CCA" ;
			break;
		
		default:
			break;
		}
		
		
		//TODO 数据显示 41455243105204E702300BDB0514019A
		mMessage = mMessage.replace(" ","").trim() ;
		String volt = mMessage.substring(13,17) ;
		Integer voltInt = Integer.valueOf(volt, 16) ;
		
		String CCA = mMessage.substring(17,21) ;
		
		String res = mMessage.substring(21,25) ;//4.5m
		Integer resInt = Integer.valueOf( res, 16) ;
		
		String pre = mMessage.substring(25,29) ;//156%
		String CCAResult = mMessage.substring(29,31) ;//GOOD
		
		String vString = voltInt.toString().substring(0,voltInt.toString().length() -2)+"."+voltInt.toString().subSequence(voltInt.toString().length() -2, voltInt.toString().length()) ;
		
		float vFloat = Float.valueOf(vString) ;
		
		if(vFloat <= 11.99){
			mPbVRed.setVisibility(View.GONE);
			mPbVYellow.setVisibility(View.GONE);
			mPbVGreen.setVisibility(View.GONE);
			mPbVDarkGreen.setVisibility(View.VISIBLE);
			mPbVDarkGreen.setProgress((int)vFloat);
		}else if(vFloat >= 12 && vFloat < 12.39){
			mPbVRed.setVisibility(View.GONE);
			mPbVYellow.setVisibility(View.VISIBLE);
			mPbVGreen.setVisibility(View.GONE);
			mPbVDarkGreen.setVisibility(View.GONE);
			mPbVYellow.setProgress((int)vFloat);
		}else if(vFloat >= 12.40 && vFloat < 12.59){
			mPbVRed.setVisibility(View.GONE);
			mPbVYellow.setVisibility(View.GONE);
			mPbVGreen.setVisibility(View.VISIBLE);
			mPbVDarkGreen.setVisibility(View.GONE);
			mPbVGreen.setProgress((int)vFloat);
		}else if(vFloat >= 12.60){
			mPbVRed.setVisibility(View.GONE);
			mPbVYellow.setVisibility(View.GONE);
			mPbVGreen.setVisibility(View.GONE);
			mPbVDarkGreen.setVisibility(View.VISIBLE);
			mPbVDarkGreen.setProgress((int)vFloat);
		}
		

		int iCCA = Integer.valueOf(CCA,16) ;
		
		//12.90
		mTvV.setText(vString+ " V") ;
		//780



		switch (mPosition) {
			case 0:
				mTvResult.setText(Integer.valueOf( CCA, 16) +" "+mUnit) ;
				break;
			case 1:
				mTvResult.setText(Integer.valueOf( CCA, 16) +"A "+mUnit) ;
				break;
			case 2:
				mTvResult.setText(Integer.valueOf( CCA, 16) +"A "+mUnit) ;
				break;
			case 3:
				mTvResult.setText(Integer.valueOf( CCA, 16) +"A "+mUnit) ;
				break;
			case 4:
				mTvResult.setText(Integer.valueOf( CCA, 16) +" "+mUnit) ;
				break;
			case 5:
				mTvResult.setText(Integer.valueOf( CCA, 16) +" "+mUnit) ;
				break;
			case 6:
				mTvResult.setText(Integer.valueOf( CCA, 16) +"A "+mUnit) ;
				break;
			case 7:
				mTvResult.setText(Integer.valueOf( CCA, 16) +"A "+mUnit) ;
				break;
			case 8:
				mTvResult.setText(Integer.valueOf( CCA, 16) +" "+mUnit) ;
				break;
			case 9:
				mTvResult.setText(Integer.valueOf( CCA, 16) +" "+mUnit) ;
				break;

			default:
				break;
		}

		if(!TextUtils.isEmpty(mInputValue) && mInputValue.equals("null")){
			mInputValue = "";
		}
		if(!TextUtils.isEmpty(mInputValue) && !mInputValue.equals("null")){
			if(iCCA >= Integer.valueOf(mInputValue)){
				mPbTitleYellow.setVisibility(View.GONE);
				mPbTitleGreen.setVisibility(View.VISIBLE);
				mPbTitleGreen.setProgress(iCCA+150);
			}else{
				mPbTitleYellow.setVisibility(View.VISIBLE);
				mPbTitleGreen.setVisibility(View.GONE);
				mPbTitleYellow.setProgress(iCCA+150);
			}
		}

		String oum = resInt.toString().substring(0, resInt.toString().length()-2)+"."+resInt.toString().subSequence(resInt.toString().length()-2, resInt.toString().length()) ;
		float vOum = Float.valueOf(oum) ;
		
		if(vOum <= 14.99){
			mPbVolitDarkGreen.setVisibility(View.VISIBLE);
			mPbVolitGreen.setVisibility(View.GONE);
			mPbVolitRed.setVisibility(View.GONE);
			mPbVolitYellow.setVisibility(View.GONE);
			mPbVolitDarkGreen.setProgress((int)vOum);	
		}else if(vOum >= 15.00 && vOum <=26.00 ){
			mPbVolitDarkGreen.setVisibility(View.GONE);
			mPbVolitGreen.setVisibility(View.GONE);
			mPbVolitRed.setVisibility(View.GONE);
			mPbVolitYellow.setVisibility(View.VISIBLE);
			mPbVolitYellow.setProgress((int)vOum);	
		}else if(vOum >= 27){
			mPbVolitDarkGreen.setVisibility(View.GONE);
			mPbVolitGreen.setVisibility(View.GONE);
			mPbVolitRed.setVisibility(View.VISIBLE);
			mPbVolitYellow.setVisibility(View.GONE);
			mPbVolitRed.setProgress((int)vOum);	
		}
		//
		mTvVolit.setText(oum + "mΩ") ;
		
		mTvTime.setText(Float.valueOf(Integer.valueOf(pre,16)) / 100f +"%") ;
		
		switch (Integer.valueOf(CCAResult)) {
		case 0:
			mIvResult.setImageResource(R.mipmap.icon_good);
			mTvStatus.setText("OK");
			mTvStatus.setTextColor(mContext.getResources().getColor(R.color.dark_green));
			setTimeProgress((int)(Float.valueOf(Integer.valueOf(pre,16)) / 100f) + 40);
			
			break;
		case 1:
			mTvStatus.setText("OK-Recharger") ;
			mTvStatus.setTextColor(Color.GREEN);
			mIvResult.setImageResource(R.mipmap.icon_recharge);
			setTimeProgress((int)(Float.valueOf(Integer.valueOf(pre,16)) / 100f) + 40);
			
			break;
		case 2:
			mTvStatus.setText("Charge & retest") ;
			mTvStatus.setTextColor(Color.GREEN);
			mIvResult.setImageResource(R.mipmap.icon_ok_recharge);
			setTimeProgress((int)(Float.valueOf(Integer.valueOf(pre,16)) / 100f) + 40);
			
			break;
		case 3:
			mTvStatus.setText("Recharge") ;
			mTvStatus.setTextColor(Color.YELLOW);
			mIvResult.setImageResource(R.mipmap.icon_recharge);
			setTimeProgress((int)(Float.valueOf(Integer.valueOf(pre,16)) / 100f) + 40);			
			break;
		case 4:
			mTvStatus.setText("Bad_bat") ;
			mTvStatus.setTextColor(Color.RED);
			mIvResult.setImageResource(R.mipmap.icon_replace);
			setTimeProgress((int)(Float.valueOf(Integer.valueOf(pre,16)) / 100f) + 40);			
			break;
		case 5:
			mTvStatus.setText("Bad_Cell") ;
			mTvStatus.setTextColor(Color.RED);
			mIvResult.setImageResource(R.mipmap.icon_replace);
			setTimeProgress((int)(Float.valueOf(Integer.valueOf(pre,16)) / 100f) + 40);			
			break;
		case 6:
			mTvStatus.setText("Replace") ;
			mTvStatus.setTextColor(Color.RED);
			mIvResult.setImageResource(R.mipmap.icon_replace);
			setTimeProgress((int)(Float.valueOf(Integer.valueOf(pre,16)) / 100f) + 40);			
			break;
		case 7:
			mTvStatus.setText("??") ;
			mTvStatus.setTextColor(Color.RED);
			mIvResult.setImageResource(R.mipmap.icon_replace);
			setTimeProgress((int)(Float.valueOf(Integer.valueOf(pre,16)) / 100f) + 40);			
			break;

		default:
			break;
		}
		
		switch (mFlag) {
			case 0:
				mIvDevice.setImageResource(R.mipmap.sli_wet) ;
				mDeviceType = "SLI" ;
				break;
			case 1:
				mIvDevice.setImageResource(R.mipmap.cv) ;
				mDeviceType = "WET" ;
				break;
			case 2:
				mIvDevice.setImageResource(R.mipmap.ic_agm) ;
				mDeviceType = "AGM FLAT" ;
				break;
			case 3:
				mIvDevice.setImageResource(R.mipmap.ic_agm_spiral) ;
				mDeviceType = "AGM SPIRAL" ;
				break;
			case 4:
				mIvDevice.setImageResource(R.mipmap.ic_efb) ;
				mDeviceType = "EFB" ;
				break;
			case 5:
				mIvDevice.setImageResource(R.mipmap.ic_get_cell) ;
				mDeviceType = "GET CELL" ;
				break;
			default:
				break;
		}
		
	}
	
	private String mDeviceType ;

	private LeProxy mLeProxy;
	 
	private View mMainView ;
	
	private String mPreFileString= "" ;
	
	private String mPreCmdString= "" ;

	private TextView mTvTestTime ;
	
	private void getPreTxtFile(){
		mPreFileString = FileUtils.loadFromSDFile(MApplication.mFileName+".txt") ;
		mPreCmdString = FileUtils.loadFromSDFile(MApplication.mFileName+"json.txt") ;
	}

	private ResultActivity mResultActivity ;

	private int mResultType ;

	private String mFileName = "" ;

	private TextView mTvFileName ;

	private String mTimeTestTime ;

	private LinearLayout mLLStatus ;

	private RelativeLayout mRlPbThree  ;

	private LinearLayout mLLCapcity ;

	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
		mMainView = inflater.inflate(R.layout.activity_battrey_result, container,false);
		mContext = getActivity() ;
		mResultActivity = (ResultActivity)mContext ;
		initView(mMainView);

		Bundle bundle = getArguments();

		if (bundle != null) {
			mMessage = bundle.getString("message");
			mInputValue = bundle.getString("inputValue") ;
			mPosition= Integer.valueOf(bundle.getString("mPosition")) ;
			mType= Integer.valueOf(bundle.getString("mType")) ;
			mFlag = Integer.valueOf(bundle.getString("mFlag")) ;
			mResultType = bundle.getInt("type") ;
			mFileName = bundle.getString("filename") ;
			mTimeTestTime = bundle.getString("test_time") ;
		}

		if(!TextUtils.isEmpty(mInputValue) && mInputValue.equals("null") ){
			mInputValue = "" ;
		}

		if(!TextUtils.isEmpty(mFileName)){
			mTvFileName.setText(mFileName);
		}

		if(!TextUtils.isEmpty(mTimeTestTime) && !mTimeTestTime.equals("null") ){
			mTvTestTime.setText(mTimeTestTime);
		}else {
			mTvTestTime.setText(""+Utils.getTime());
		}
		mLeProxy = LeProxy.getInstance();
		getPreTxtFile() ;
		initData(mMessage,mInputValue,mPosition,mType,mFlag) ;

		if(mPosition == 9){
			mLLStatus.setVisibility(View.GONE) ;
			mRlPbThree.setVisibility(View.GONE);
			mLLCapcity.setVisibility(View.GONE);
		}else{
			mLLStatus.setVisibility(View.VISIBLE) ;
			mRlPbThree.setVisibility(View.VISIBLE);
			mLLCapcity.setVisibility(View.VISIBLE);
		}


		return mMainView ;
	}

	private String  mPrintString = "" ;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 2200){
			if(!TextUtils.isEmpty(mPrintString)){
				Intent intent = new Intent() ;
				intent.putExtra("print",strToByteArray(mPrintString)) ;
				getActivity().sendBroadcast(intent);
			}
		}
	}

	public byte[] strToByteArray(String str) {
		if (str == null) {
			return null;
		}
		byte[] byteArray = str.getBytes();
		return byteArray;
	}

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.ll_print:
			
			String title = "" ;
			
			if(!TextUtils.isEmpty(MApplication.mBarCode)){
				title = "Barcode: "+MApplication.mBarCode +"\n" ;
			}
			String plate = "" ;
			if(!TextUtils.isEmpty(MApplication.mCarPlate)){
				plate = "Name/Licence plate: "+MApplication.mCarPlate+"\n\n\n" ;
			}

			mPrintString = title +
					"Tested on: "+ Utils.getTime()+"\n"+
					 plate+
					"Battery Test:  "+mDeviceType+" "+mTvValue.getText().toString().trim()+"\n"+
					"Battery Test: "+mTvV.getText().toString().trim() +"V\n"+
					"Measured : "+mTvResult.getText().toString().trim()+"\n"+
					"Internal Resistance : "+mTvVolit.getText().toString().trim()+"\n"+
					"Life: "+mTvTime.getText().toString().trim()+"\n"+
					"Result : "+mTvStatus.getText().toString().trim()+"\n" ;
			
			List<BluetoothDevice> deviceList = mLeProxy.getConnectedDevices();
			/*if(!deviceList.isEmpty()){
				mLeProxy.disconnect(deviceList.get(0).getAddress());
			}*/
			Intent intent1 = new Intent(mContext,PrintReportActivity.class) ;
			intent1.putExtra("print",mPrintString) ;
			getActivity().startActivityForResult(intent1,2200);
			break ;
		case R.id.ll_save :
			if(mResultType == 1){
				getActivity().finish() ;
				return ;
			}
			if(TextUtils.isEmpty(MApplication.mFileName)){
				MApplication.retMessage = mMessage ;
				Intent intent = new Intent(mContext,KeyInTouchActivity.class) ;
				intent.putExtra("type", 1) ;
				startActivity(intent) ;
			}else{
				
				String title1 = "" ;

				if(!TextUtils.isEmpty(MApplication.mBarCode)){
					title1 = "Barcode: "+MApplication.mBarCode +"\n" ;
				}
				String plate1 = "" ;
				if(!TextUtils.isEmpty(MApplication.mCarPlate)){
					plate1 = "Name/Licence plate: "+MApplication.mCarPlate+"\n\n\n" ;
				}
				
				 String str1 =  title1 +
						 "Tested on: "+ Utils.getTime()+"\n"+
						 plate1+
						 "Battery Test:  "+mDeviceType+" "+mTvValue.getText().toString().trim()+"\n"+
						 "Battery Test: "+mTvV.getText().toString().trim() +"V\n"+
						 "Measured : "+mTvResult.getText().toString().trim()+"\n"+
						 "Internal Resistance : "+mTvVolit.getText().toString().trim()+"\n"+
						 "Life: "+mTvTime.getText().toString().trim()+"\n"+
						 "Result : "+mTvStatus.getText().toString().trim()+"\n";

				mResultActivity.saveBatteryResultFile();

				Intent intent = new Intent(getActivity(), TestMenuActivity.class) ;
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) ;
				startActivity(intent);
				getActivity().finish() ;
				 
			}
			break ;
		default:
			break;
		}
	}

}
