package com.zzteck.cardect.ui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.zzteck.cardect.R;
import com.zzteck.cardect.util.FileUtils;
import com.zzteck.cardect.util.Utils;

import org.w3c.dom.Text;

public class KeyInTouchActivity extends Activity implements OnClickListener {

	private Context mContext;

	private ImageView mIvScan, mIvOk ;

	private EditText mEtBatteryMode,mEtCarPlate,mEtName,mEtWorkShop ;

	private ImageView mIvBack ;

	private TextView mEtBarCode ;

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
	
	private String generationMessage(){

		String retString = "" ;

		if(!TextUtils.isEmpty(MApplication.retMessage)){
			String type = MApplication.retMessage.substring(0,1) ;
			if(type.equals("S")) {
				String mTestMesssage = MApplication.retMessage.replace(" ","") ;
				String volt = mTestMesssage.substring(13,17) ;
				Integer voltInt = Integer.valueOf(volt, 16) ;

				String drop = mTestMesssage.substring(17,21) ;
				Integer dropInt = Integer.valueOf(drop, 16) ;

				String strSec = mTestMesssage.substring(21,25) ;
				Integer secInt = Integer.valueOf( strSec, 16)*100 ;

				String strResult = mTestMesssage.substring(25,27) ;

				//12.90 V
				String vString = voltInt.toString().substring(0, voltInt.toString().length() - 2)+"."+voltInt.toString().subSequence(voltInt.toString().length() - 2,voltInt.toString().length()) ;
				float vInt = Float.valueOf(vString) ;
				String dString = dropInt.toString().substring(0, dropInt.toString().length() - 2)+"."+dropInt.toString().subSequence(dropInt.toString().length() - 2, dropInt.toString().length()) ;
				float dFloat = Float.valueOf(dString) ;
				String status = "" ;

				if(Integer.valueOf(strResult) == 1){
					status = "OK" ;
				}else{
					status = "NOT OK" ;
				}

				retString = "Tested on: "+Utils.getTime()+"\n"+
						"Starter Test:\n"+
						"Battery Volt: "+vString +" V"+" \n"+
						"Cranking Volt: "+dString + " V"+" \n"+
						"Cranking Period: "+Float.valueOf(secInt)/ 1000f  +" S"+" \n"+
						"Result: "+status ;
			}else{

				String mTestMesssage = MApplication.retMessage.replace(" ", "").trim();
				String volt = mTestMesssage.substring(13, 17);
				Integer voltInt = Integer.valueOf(volt, 16);

				String CCA = mTestMesssage.substring(17, 21);

				String res = mTestMesssage.substring(21, 25);//4.5m
				Integer resInt = Integer.valueOf(res, 16);

				String pre = mTestMesssage.substring(25, 29);//156%
				String CCAResult = mTestMesssage.substring(29, 31);//GOOD

				String vString = voltInt.toString().substring(0, voltInt.toString().length() - 2) + "." + voltInt.toString().subSequence(voltInt.toString().length() - 2, voltInt.toString().length());

				float vFloat = Float.valueOf(vString);

				String title1 = "" ;

				String orgMessage = mTestMesssage.substring(1, mTestMesssage.length()) ;
				String[] splitString = orgMessage.split("\\|") ;

				String mUnit = "" ;

				switch (Integer.valueOf(splitString[2])) {
					case 0:
						mUnit = "CCA" ;
						break;
					case 1:
						mUnit = "EN1" ;
						break;
					case 2:
						mUnit = "EN2" ;
						break;
					case 3:
						mUnit = "DIN" ;
						break;
					case 4:
						mUnit = "JIS#" ;
						break;
					case 5:
						mUnit = "CA" ;
						break;
					case 6:
						mUnit = "SAE" ;
						break;
					case 7:
						mUnit = "IEC" ;
						break;
					case 8:
						mUnit = "MCA" ;
						break;
					case 9:
						mUnit = "CCA" ;
						break;

					default:
						break;
				}

				String mDeviceType = "" ;

				switch (Integer.valueOf(splitString[4])) {
					case 0:
						mDeviceType = "SLI" ;
						break;
					case 1:
						mDeviceType = "WET" ;
						break;
					case 2:
						mDeviceType = "AGM FLAT" ;
						break;
					case 3:
						mDeviceType = "AGM SPIRAL" ;
						break;
					case 4:
						mDeviceType = "EFB" ;
						break;
					case 5:
						mDeviceType = "GET CELL" ;
						break;
					default:
						break;
				}
				String ccaResult = "" ;
				switch (Integer.valueOf(CCAResult)) {
					case 0:
						ccaResult = "OK";
						break;
					case 1:
						ccaResult = "OK-Recharger";
						break ;
					case 2:
						ccaResult = "Charge & retest";
						break ;
					case 3:
						ccaResult = "Recharge";
						break ;
					case 4:
						ccaResult = "Bad_bat";
						break ;
					case 5:
						ccaResult = "Bad_Cell";
						break ;
					case 6:
						ccaResult = "Replace";

					case 7:
						ccaResult = "??";
						break ;


				}

				String oum = resInt.toString().substring(0, resInt.toString().length()-2)+"."+resInt.toString().subSequence(resInt.toString().length()-2, resInt.toString().length()) ;
				float vOum = Float.valueOf(oum) ;

				if(!TextUtils.isEmpty(MApplication.mBarCode)){
					title1 = "Barcode: "+MApplication.mBarCode +"\n" ;
				}
				String plate = "" ;
				if(!TextUtils.isEmpty(mEtCarPlate.getText().toString().trim())){
					plate = "Name/Licence plate: "+mEtCarPlate.getText().toString().trim()+"\n\n\n" ;
				}

				retString = title1 +
						"Tested on: "+ Utils.getTime()+"\n"+
						 plate+
						"Battery Test:  "+mDeviceType+" "+splitString[1]+"A "+mUnit+"\n"+
						"Battery Test: "+vString+ " V" +"V\n"+
						"Measured : "+Integer.valueOf( CCA, 16) +"A "+mUnit+"\n"+
						"Internal Resistance : "+oum + "mΩ"+"\n"+
						"Life: "+Float.valueOf(Integer.valueOf(pre,16)) / 100f +"%"+"\n"+
						"Result : "+ccaResult+"\n" ;
			}
		}
		return retString ;
	}

	private TextView mTvMainInfo ;

	private void initView() {
		mTvMainInfo = (TextView) findViewById(R.id.tv_main) ;
		mTvMainInfo.setText("KEY-IN PARTICULARS");
		mEtBatteryMode =(EditText)findViewById(R.id.et_battery_mode) ;
		mEtCarPlate =(EditText)findViewById(R.id.et_car_plate) ;
		mEtName =(EditText)findViewById(R.id.et_name) ;
		mEtWorkShop =(EditText)findViewById(R.id.et_workshop) ;
		
		mIvBack = (ImageView)findViewById(R.id.iv_back) ;
		mIvOk = (ImageView)findViewById(R.id.iv_ok) ;
		mIvScan = (ImageView)findViewById(R.id.iv_scan) ;
		mEtBarCode = (TextView)findViewById(R.id.et_bar_code) ;

		mEtBatteryMode = (EditText)findViewById(R.id.et_battery_mode) ;
		mEtCarPlate = (EditText)findViewById(R.id.et_car_plate) ;
		mEtName = (EditText)findViewById(R.id.et_name) ;
		mEtWorkShop = (EditText)findViewById(R.id.et_workshop) ;

		mIvOk.setOnClickListener(this);
		mIvScan.setOnClickListener(this);
		mIvBack.setOnClickListener(this) ;
		mEtBarCode.setEnabled(false) ;

	}

	private int mType ;
	
	private void initData() {
		Intent intent = getIntent() ;

		if(intent != null){
			mType = intent.getIntExtra("type", 0) ;
			//getPreTxtFile();
		}
	}

	private String mPreFileString= "" ;

	private String mPreCmdString= "" ;

	private boolean isMatchChar(String string){
    	String regEx = "[0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ]"; 
    	Pattern p = Pattern.compile(regEx) ;
    	Matcher m = p.matcher(string);  
    	return m.matches();
    }

    private String mPrint = "" ;

	public void saveBatteryResultFile(){
		String printStr = "" ;
		if(TextUtils.isEmpty(mPreFileString)){
			printStr = mPrint ;
		}else {
			printStr = mPreFileString + "\n" + mPrint;
		}
		String writeMessage = "" ;
		if(TextUtils.isEmpty(mPreCmdString)){
			writeMessage = MApplication.retMessage ;
		}else{
			writeMessage = mPreCmdString+"\n" +MApplication.retMessage ;
		}

		try {
			FileUtils.writeFileToSDCard(MApplication.mFileName + ".txt", printStr);
			FileUtils.writeFileToSDCard2(MApplication.mFileName + "json.txt", writeMessage);
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	private void getPreTxtFile(){
		mPreFileString = FileUtils.loadFromSDFile(Environment.getExternalStorageDirectory().getAbsoluteFile()+"/zzteck/"+MApplication.mFileName+".txt") ;
		mPreCmdString = FileUtils.loadFromSDFile(Environment.getExternalStorageDirectory().getAbsoluteFile()+"/zzteckjson/"+MApplication.mFileName+"json.txt") ;
	}

	public void saveStartResultFile(){

		String printStr = "" ;
		String writeMessage = "" ;
		if(TextUtils.isEmpty(mPreFileString)){
			printStr = mPrint ;
		}else{
			printStr = mPreFileString +"\n"+ mPrint ;
		}

		if(TextUtils.isEmpty(mPreCmdString)){
			writeMessage = MApplication.retMessage ;
		}else{
			writeMessage = mPreCmdString+"\n" +MApplication.retMessage ;
		}

		try {
			FileUtils.writeFileToSDCard(MApplication.mFileName + ".txt", printStr);
			FileUtils.writeFileToSDCard2(MApplication.mFileName + "json.txt", writeMessage);
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_key_intouch);
		mContext = KeyInTouchActivity.this;
		initView();
		initData();
		mPrint = generationMessage() ;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(data != null){

			Bundle bundle = data.getExtras();
			if (bundle == null) {
				return;
			}
			if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
				String result = bundle.getString(CodeUtils.RESULT_STRING);
				mEtBarCode.setText(result) ;
			} else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
				mEtBarCode.setText("") ;
			}
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if(mType != 0){
			Intent intent1 = new Intent(mContext,TestMenuActivity.class) ;
			intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) ;
			startActivity(intent1);
		}
		finish() ;
	}

	private String getFileName(){
		String fileName = "" ;

		if(!TextUtils.isEmpty(mEtBarCode.getText().toString().trim())){
			fileName = mEtBarCode.getText().toString().trim() ;
		}else if(!TextUtils.isEmpty(mEtBatteryMode.getText().toString().trim())){
			fileName = mEtBatteryMode.getText().toString().trim() ;
		}else if(!TextUtils.isEmpty(mEtCarPlate.getText().toString().trim())){
			fileName = mEtCarPlate.getText().toString().trim() ;
		}else if(!TextUtils.isEmpty(mEtName.getText().toString().trim())){
			fileName = mEtName.getText().toString().trim() ;
		}else if(!TextUtils.isEmpty(mEtWorkShop.getText().toString().trim())){
			fileName = mEtWorkShop.getText().toString().trim() ;
		}
		return fileName ;
	}

	@Override
	public void onClick(View v) {
		Intent intent = null ;
		switch (v.getId()) {
			case R.id.iv_back:
				if(mType != 0){
					Intent intent1 = new Intent(mContext,TestMenuActivity.class) ;
					intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) ;
					startActivity(intent1);
				}
				finish() ;
				break ;
		case R.id.iv_ok:
			if(mType == 0){
				String fileName = getFileName() ;
				if(!TextUtils.isEmpty(fileName)){
					MApplication.mFileName = fileName ;
					intent = new Intent(mContext,TestMenuActivity.class) ;
					startActivity(intent) ;
					finish();
				}
			}else{
				String fileName = getFileName() ;
				if(!TextUtils.isEmpty(fileName)){
					MApplication.mFileName = fileName ;
					String type = MApplication.retMessage.substring(0,1) ;

					getPreTxtFile() ;
					if(type.equals("S")) {
						saveStartResultFile();
					}else{
						saveBatteryResultFile();
					}

					MApplication.retMessage = "" ;
					MApplication.mBatteryMode = mEtBatteryMode.getText().toString().trim() ;
					MApplication.mCarPlate = mEtCarPlate.getText().toString().trim() ;
					MApplication.mName = mEtName.getText().toString().trim() ;
					MApplication.mWorkShop = mEtWorkShop.getText().toString().trim() ;

					Intent intent3 = new Intent(mContext, TestMenuActivity.class);
					intent3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent3);
					finish();
				}
			}
			break;
		case R.id.iv_scan:
			intent = new Intent(mContext,SecondActivity.class) ;
			startActivityForResult(intent,1122) ;
			break;

		default:
			break;
		}
	}

}
