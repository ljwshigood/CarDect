package com.zzteck.cardect.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zzteck.cardect.R;
import com.zzteck.cardect.fragment.BatteryResultFragment;
import com.zzteck.cardect.fragment.QRCodeFragment;
import com.zzteck.cardect.fragment.StartResultFragment;
import com.zzteck.cardect.util.ConstCode;
import com.zzteck.cardect.util.FileUtils;
import com.zzteck.cardect.util.ToastUtil;
import com.zzteck.cardect.util.Utils;
import com.zzteck.cardect.view.WJViewPaper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ResultActivity extends FragmentActivity implements OnClickListener {

	private Context mContext;

	private ImageView mIvBack ;
	
	private WJViewPaper mWJViewPaper ;
	
	private Fragment mQRCodeFragment ;

	private List<Fragment> mFragment = new ArrayList<>();
	
	private List<String> mMessageList;
	
	private String mTestMesssage ;
	
	private String mInputValue ;
	
	private TextView mTvValue ;
	
	private int mPosition ;
	
	private int mType ,mFlag ;
	
	private String mMessageFilePath ,mTxtFilePath ;

	private int mResultType ;
	
	private void initData(){
		
		Intent intent = getIntent() ;
		mResultType = intent.getIntExtra("type",0) ;
		mTxtFilePath = intent.getStringExtra("filePath") ;
		mMessageFilePath = intent.getStringExtra("real_filePath") ;
		
		mTestMesssage = intent.getStringExtra("message") ;
		
		mMessageList = intent.getStringArrayListExtra("message_list") ;

        mFragment.clear();

        mQRCodeFragment = new QRCodeFragment();
		if(!TextUtils.isEmpty(mTestMesssage)){
			String type = mTestMesssage.substring(0,1) ;

			if(type.equals("S")){

				StartResultFragment mStartFragment = new StartResultFragment();
				Bundle bundle = new Bundle();
				bundle.putString("message", mTestMesssage);
				bundle.putInt("type",mResultType);
				if(!TextUtils.isEmpty(MApplication.mFileName)){
					bundle.putString("filename",MApplication.mFileName);
				}
				mStartFragment.setArguments(bundle);
				mFragment.add ( mStartFragment );

			}else{
				String orgMessage = mTestMesssage.substring(1, mTestMesssage.length()) ;
				String[] splitString = orgMessage.split("\\|") ;
				BatteryResultFragment mBatteryFragment = new BatteryResultFragment();
				Bundle bundle = new Bundle();
				bundle.putString("message", mTestMesssage);
				bundle.putString("inputValue", splitString[1]);
				bundle.putString("mPosition", splitString[2]);
				bundle.putString("mType", splitString[3]);
				bundle.putString("mFlag", splitString[4]);
				bundle.putInt("type",mResultType);
				if(!TextUtils.isEmpty(MApplication.mFileName)){
					bundle.putString("filename",MApplication.mFileName);
				}
				bundle.putInt("type",mResultType);
				mBatteryFragment.setArguments(bundle);

				mInputValue = splitString[1] ;
				mPosition= Integer.valueOf(splitString[2]) ;
				mType= Integer.valueOf(splitString[3]) ;
				mFlag = Integer.valueOf(splitString[4]) ;

				mFragment.add ( mBatteryFragment );
			}
		}

     	if(mMessageList != null ){
			for(int i = 0 ;i < mMessageList.size() ;i++){
				String message = mMessageList.get(i) ;
				String type1 = message.substring(0,1) ;
				if(type1.equals("S")){
					StartResultFragment mStartFragment = new StartResultFragment();

					Bundle bundle = new Bundle();
				//	bundle.putString("message", message.substring(1, message.length()));
					bundle.putString("message",message);
					bundle.putInt("type",mResultType);
					if(!TextUtils.isEmpty(mTxtFilePath)){
						bundle.putString("filename",new File(mTxtFilePath).getName());
						bundle.putInt("type",mResultType);

						ArrayList<String> readTxtFileList = FileUtils.ReadTxtFile(mTxtFilePath) ;
						if(readTxtFileList != null && readTxtFileList.size() > 0){
							bundle.putString("test_time", readTxtFileList.get(0));
						}

					}
					mStartFragment.setArguments(bundle);

					mFragment.add ( mStartFragment );
				}else{
					BatteryResultFragment mBatteryFragment = new BatteryResultFragment();
					String orgMessage = message.substring(1, message.length()) ;

					String[] splitString = orgMessage.split("\\|") ;

					Bundle bundle = new Bundle();
					bundle.putString("message", "B"+splitString[0]);
					bundle.putString("inputValue", splitString[1]);
					bundle.putString("mPosition", splitString[2]);
					bundle.putString("mType", splitString[3]);
					bundle.putString("mFlag", splitString[4]);
					bundle.putInt("type",mResultType);
					if(!TextUtils.isEmpty(mTxtFilePath)){
						bundle.putString("filename",new File(mTxtFilePath).getName());
					}
					
					ArrayList<String> readTxtFileList = FileUtils.ReadTxtFile(mTxtFilePath) ;
					if(readTxtFileList != null && readTxtFileList.size() > 0){
						bundle.putString("test_time", readTxtFileList.get(0));
					}
					
					bundle.putInt("type",mResultType);
					mBatteryFragment.setArguments(bundle);

					mFragment.add ( mBatteryFragment );
				}
			}
		}

        if(!TextUtils.isEmpty(mTestMesssage) || mMessageList != null ){
			Bundle bundle = new Bundle();
			bundle.putString("message", mTestMesssage);
			if(!TextUtils.isEmpty(mTxtFilePath)){
				bundle.putString("filePath",mTxtFilePath);
			}
			mQRCodeFragment.setArguments(bundle);
       	   mFragment.add ( mQRCodeFragment );   	
        }


		FragmentPagerAdapter fragmentPagerAdapter = new FragmentPagerAdapter( getSupportFragmentManager () ) {
			@Override
			public Fragment getItem(int position) {
				return mFragment.get ( position );
			}

			@Override
			public int getCount() {
				return mFragment.size ();
			}
		};

		mWJViewPaper.setAdapter ( fragmentPagerAdapter );
		
		
		mWJViewPaper.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {
				position++ ;
				mTvPage.setText(""+position +"/"+ mFragment.size() +"");
			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});

		mTvPage.setText( "1" +"/"+ mFragment.size() +"");
	}

	private String mPreFileString= "" ;

	private String mPreCmdString= "" ;

	private String generationMessage(){

		String retString = "" ;

		if(!TextUtils.isEmpty(mTestMesssage)){

			String type = mTestMesssage.substring(0,1) ;

			if(type.equals("S")) {

				mTestMesssage = mTestMesssage.replace(" ","") ;
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

				mTestMesssage = mTestMesssage.replace(" ", "").trim();
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
						mDeviceType = "SLI/WET" ;
						break;
					case 1:
						mDeviceType = "CV" ;
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
						break ;
					case 7:
						ccaResult = "??";
						break ;


				}

				String oum = resInt.toString().substring(0, resInt.toString().length()-2)+"."+resInt.toString().subSequence(resInt.toString().length()-2, resInt.toString().length()) ;
				float vOum = Float.valueOf(oum) ;
				String title3  = "" ;
				if(!TextUtils.isEmpty(MApplication.mBarCode)){
					title3 = "Barcode: "+MApplication.mBarCode +"\n" ;
				}
				String plate = "" ;
				if(!TextUtils.isEmpty(MApplication.mCarPlate)){
					plate = "Name/Licence plate: "+MApplication.mCarPlate+"\n\n\n" ;
				}

				retString = title3 +
						"Tested on: "+ Utils.getTime()+"\n"+
						plate+
						"Battery Test:  "+mDeviceType+" "+splitString[1]+"A "+mUnit+"\n"+
						"Battery Test: "+vString+ " V" +"\n"+
						"Measured : "+Integer.valueOf( CCA, 16) +"A "+mUnit+"\n"+
						"Internal Resistance : "+oum + "mΩ"+"\n"+
						"Life: "+Float.valueOf(Integer.valueOf(pre,16)) / 100f +"%"+"\n"+
						"Result : "+ccaResult+"\n" ;
			}
		}
		return retString ;
	}


	public void saveBatteryResultFile(){
		getPreTxtFile();
		String tem =  generationMessage() ;
		String printStr =  "" ;
		String writeMessage= "" ;

		if(TextUtils.isEmpty(mPreFileString)){
			printStr = tem ;
		}else{
			printStr = mPreFileString +"\n"+ tem ;
		}
		if(TextUtils.isEmpty(mPreCmdString)){
			writeMessage = mTestMesssage+ "|"+mInputValue+"|"+mPosition+"|"+mType+"|"+mFlag ;
		}else{
			writeMessage = mPreCmdString+"\n" +mTestMesssage+ "|"+mInputValue+"|"+mPosition+"|"+mType+"|"+mFlag;
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

	private int mRequestCode = 0 ;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mRequestCode = requestCode ;
	}
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(mRequestCode == 2200){

			}else if(mRequestCode == 2211){

			}else if(mRequestCode == 2222){

			}
		}
	} ;
	public void saveStartResultFile(){
		getPreTxtFile();
		String tem =  generationMessage() ;
		String printStr = "" ;
		String writeMessage = "" ;
		if(TextUtils.isEmpty(mPreFileString)){
			printStr = tem ;
		}else{
			printStr = mPreFileString +"\n"+ tem ;
		}
		if(TextUtils.isEmpty(mPreCmdString)){
			writeMessage = mTestMesssage ;
		}else{
			writeMessage = mPreCmdString+"\n" +mTestMesssage ;
		}

		try {
			FileUtils.writeFileToSDCard(MApplication.mFileName + ".txt", printStr);
			FileUtils.writeFileToSDCard2(MApplication.mFileName + "json.txt", writeMessage);
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	private TextView mTvPage ;

	private TextView mTvMainInfo ;

	private void initView() {

		mTvMainInfo = (TextView) findViewById(R.id.tv_main) ;
		mTvMainInfo.setText("RESULTS");
		mTvPage = (TextView)findViewById(R.id.tv_page) ;
		mWJViewPaper = (WJViewPaper)findViewById(R.id.content) ;
		mIvBack = (ImageView)findViewById(R.id.iv_back) ;
		mIvBack.setOnClickListener(this) ;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);
		mContext = ResultActivity.this;
		initView();

		initData();
		getPreTxtFile() ;

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if(mResultType == 1){
			finish();
		}else {
			Intent intent = new Intent(mContext, TestMenuActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			if(mResultType == 1){
				finish();
			}else {
				Intent intent = new Intent(mContext, TestMenuActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
			}
			break;
		case R.id.ll_print :
			break ;

		default:
			break;
		}
	}

}
