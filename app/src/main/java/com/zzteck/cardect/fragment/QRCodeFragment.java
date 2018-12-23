package com.zzteck.cardect.fragment;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.os.EnvironmentCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.zzteck.cardect.R;
import com.zzteck.cardect.ui.DeviceListActivity;
import com.zzteck.cardect.ui.MApplication;
import com.zzteck.cardect.ui.PrintReportActivity;
import com.zzteck.cardect.util.ConstCode;
import com.zzteck.cardect.util.FileUtils;
import com.zzteck.cardect.util.LeProxy;
import com.zzteck.cardect.util.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

public class QRCodeFragment extends Fragment implements OnClickListener {

	private Context mContext;

	private ImageView mIvQRCode ;
	
	private Button mBtnPrint ;
	
	private String mMessage  ;
	
	public QRCodeFragment(){
		super() ;
		//this.mMessage = message ;
	}
	
	private void initView(View view) {
		mBtnPrint = (Button) view.findViewById(R.id.btn_print) ;
		mIvQRCode = (ImageView)view.findViewById(R.id.iv_qrcode) ;
		mBtnPrint.setOnClickListener(this);
	}

	private Bitmap mBitmap ;

	private File mFile ;

	private void initData(String textContent){
	     mBitmap = CodeUtils.createImage(textContent, 400, 400, null);
	     mIvQRCode.setImageBitmap(mBitmap);
		saveBitmapToSD(mBitmap) ;
	}

	protected void saveBitmapToSD(Bitmap bt) {
		File path = Environment.getExternalStorageDirectory();
		mFile = new File(path, System.currentTimeMillis() + ".jpg");
		try {
			FileOutputStream out = new FileOutputStream(mFile);
			bt.compress(Bitmap.CompressFormat.JPEG, 90, out);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
	}



	private View mMainView ;

	private ImageView mIvPrint ;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mMainView = inflater.inflate(R.layout.activity_qrcode, container,false);
		mContext = getActivity() ;
		initView(mMainView);
		mLeProxy = LeProxy.getInstance();
		Bundle bundle = getArguments();
		String printString = "" ;
		if (bundle != null) {
			mMessage = bundle.getString("message");
			String filePath = bundle.getString("filePath") ;
			printString = FileUtils.loadFromSDFile(filePath) ;
		}

		String str = "" ;

		if(!TextUtils.isEmpty(mMessage)){
			String type = mMessage.substring(0,1) ;
			if(type.equals("S")){
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
				String dString = dropInt.toString().substring(0, dropInt.toString().length() - 2)+"."+dropInt.toString().subSequence(dropInt.toString().length() - 2, dropInt.toString().length()) ;
				float vInt = Float.valueOf(vString) ;

				String status = "" ;

				if(Integer.valueOf(strResult) == 1){
					status = "OK" ;
				}else{
					status = "NOT OK" ;
				}
				str = "Tested on: "+ Utils.getTime()+"\n"+
						"Starter Test:\n"+
						"Battery Volt: "+vString +"V"+" \n"+
						"Cranking Volt: "+dString+"V"+" \n"+
						"Cranking Period: "+Float.valueOf(secInt)/ 1000f  +" S" +" \n"+
						"Result: "+status ;

			}else{

				String orgMessage = mMessage.substring(1, mMessage.length()) ;
				String[] splitString = orgMessage.split("\\|") ;

				mMessage = mMessage.replace(" ","").trim() ;
				String volt = mMessage.substring(13,17) ;
				Integer voltInt = Integer.valueOf(volt, 16) ;

				String CCA = mMessage.substring(17,21) ;

				String res = mMessage.substring(21,25) ;//4.5m
				Integer resInt = Integer.valueOf( res, 16) ;

				String pre = mMessage.substring(25,29) ;//156%
				String CCAResult = mMessage.substring(29,31) ;//GOOD

				String vString = voltInt.toString().substring(0,voltInt.toString().length() -2)+"."+voltInt.toString().subSequence(voltInt.toString().length() -2, voltInt.toString().length()) ;

				Float vFloat = Float.valueOf(vString) ;

				String aa = resInt.toString().substring(0, resInt.toString().length()-2)+"."+resInt.toString().subSequence(resInt.toString().length()-2, resInt.toString().length()) ;

				int iCCA = Integer.valueOf(CCA,16) ;

				String title = "" ;

				if(!TextUtils.isEmpty(MApplication.mBarCode)){
					title = "Barcode: "+MApplication.mBarCode +"\n" ;
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

				String resultCCA = "" ;
				switch (Integer.valueOf(CCAResult)) {
					case 0:
						resultCCA = "OK" ;
						break;
					case 1:
						resultCCA = "OK-Recharger" ;
						break;
					case 2:
						resultCCA = "Charge & retest" ;
						break;
					case 3:
						resultCCA = "Recharge" ;
						break;
					case 4:
						resultCCA = "Bad_bat" ;
						break;
					case 5:
						resultCCA = "Bad_Cell" ;
						break;
					case 6:
						resultCCA = "Replace" ;
						break;
					case 7:
						resultCCA = "??" ;
						break;

					default:
						break;
				}
				String inputValue = "" ;
				String mUnit  ="" ;
				switch (Integer.valueOf(splitString[2])) {
					case 0:
						inputValue = splitString[1]+" CCA" ;
						mUnit = "CCA" ;
						break;
					case 1:
						inputValue = splitString[1]+"A EN1" ;
						mUnit = "EN1" ;
						break;
					case 2:
						inputValue = splitString[1]+"A EN2" ;
						mUnit = "EN2" ;
						break;
					case 3:
						inputValue = splitString[1]+"A DIN" ;
						mUnit = "DIN" ;
						break;
					case 4:
						inputValue = splitString[1]+" JIS#" ;
						mUnit = "CCA" ;
						break;
					case 5:
						inputValue = splitString[1]+" CA" ;
						mUnit = "CA" ;
						break;
					case 6:
						inputValue = splitString[1]+"A SAE" ;
						mUnit = "SAE" ;
						break;
					case 7:
						inputValue = splitString[1]+"A IEC" ;
						mUnit = "IEC" ;
						break;
					case 8:
						inputValue = splitString[1]+"MCA" ;
						mUnit = "MCA" ;
						break;
					case 9:
						inputValue = splitString[1]+" CCA" ;
						mUnit = "CCA" ;
						break;

					default:
						break;
				}

				String CCAString = "" ;

				switch (Integer.valueOf(splitString[2])) {
					case 0:
						CCAString = Integer.valueOf( CCA, 16) +" "+mUnit ;
						break;
					case 1:
						CCAString = Integer.valueOf( CCA, 16) +"A "+mUnit ;
						break;
					case 2:
						CCAString = Integer.valueOf( CCA, 16) +"A "+mUnit ;
						break;
					case 3:
						CCAString = Integer.valueOf( CCA, 16) +"A "+mUnit ;
						break;
					case 4:
						CCAString = Integer.valueOf( CCA, 16) +" "+mUnit ;
						break;
					case 5:
						CCAString = Integer.valueOf( CCA, 16) +" "+mUnit ;
						break;
					case 6:
						CCAString = Integer.valueOf( CCA, 16) +"A "+mUnit ;
						break;
					case 7:
						CCAString = Integer.valueOf( CCA, 16) +"A "+mUnit ;
						break;
					case 8:
						CCAString = Integer.valueOf( CCA, 16) +" "+mUnit ;
						break;
					case 9:
						CCAString = Integer.valueOf( CCA, 16) +" "+mUnit ;
						break;

					default:
						break;
				}


				if(!TextUtils.isEmpty(MApplication.mBarCode)){
					title = "Barcode: "+MApplication.mBarCode +"\n" ;
				}
				String plate = "" ;
				if(!TextUtils.isEmpty(MApplication.mCarPlate)){
					plate = "Name/Licence plate: "+MApplication.mCarPlate+"\n\n\n" ;
				}



				str = title +
						"Tested on: "+ Utils.getTime()+"\n"+
						plate+
						"Battery Test:  "+mDeviceType+" "+inputValue+"\n"+
						"Battery Test: "+vString +"V\n"+
						"Measured : "+CCAString+"\n"+
						"Internal Resistance : "+aa+" mΩ\n"+
						"Life: "+Float.valueOf(Integer.valueOf(pre,16)) / 100f +"%"+"\n"+
						"Result : "+resultCCA+"\n" ;

			}
		}

		initData(str+"\n"+printString);

		return mMainView ;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 2222){
			if(mBitmap != null){
				printBitmap(mBitmap) ;
			}
		}
	}

	private void printBitmap(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);  
        byte[] byteArray = baos.toByteArray();  
        Intent intent = new Intent() ;
        intent.putExtra("print",byteArray) ;
        getActivity().sendBroadcast(intent);
	}

	private LeProxy mLeProxy;
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_print :

			List<BluetoothDevice> deviceList = mLeProxy.getConnectedDevices();
			/*if(!deviceList.isEmpty()){
				mLeProxy.disconnect(deviceList.get(0).getAddress());
			}*/

			if(mFile == null){
				return ;
			}
			Intent intent = new Intent(mContext,PrintReportActivity.class) ;
			intent.putExtra("file",mFile.getAbsolutePath()) ;
			getActivity().startActivityForResult(intent,2222);
			/*Intent intent = new Intent(mContext,DeviceListActivity.class) ;
			intent.putExtra("file",mFile.getAbsolutePath()) ;
			getActivity().startActivityForResult(intent,2222);*/

			break ;
		default:
			break;
		}
	}

}
