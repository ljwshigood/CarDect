package com.zzteck.cardect.ui;

import java.util.List;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ble.api.DataUtil;
import com.ble.ble.BleService;
import com.zzteck.cardect.R;
import com.zzteck.cardect.btprinter.BluetoothPrintService;
import com.zzteck.cardect.util.LeProxy;
import com.zzteck.cardect.util.PermissionUtils;
import com.zzteck.cardect.util.ToastUtil;
import com.zzteck.cardect.util.Utils;

public class MainActivity extends Activity implements OnClickListener{
    private static final String TAG = "MainActivity";

    public static final String EXTRA_DEVICE_ADDRESS = "extra_device_address";
    public static final String EXTRA_DEVICE_NAME = "extra_device_name";

    private static final int FRAGMENT_SCAN = 0;
    private static final int FRAGMENT_CONNECTED = 1;
    private static final int FRAGMENT_MTU = 2;

    private static final int REQUEST_ENABLE_BT = 2;

    private BluetoothAdapter mBluetoothAdapter;
    
    private MainActivity mContext;
    
    private ImageView mIvNote , mIvDelete ,mIvCCA ;
    
    private void setDianYaBg(float fv){
    	if(fv <= 11.9f){
    		mTvDianya.setBackgroundResource(R.mipmap.volt_display_red);
    	}else if(fv >= 12f && fv <=12.39f){
    		mTvDianya.setBackgroundResource(R.mipmap.volt_display_yellow);
    	}else if(fv >= 12.40f && fv <= 12.59f){
    		mTvDianya.setBackgroundResource(R.mipmap.volt_display_green);
    	}else if(fv >= 12.60f){
    		mTvDianya.setBackgroundResource(R.mipmap.volt_display_darkgreen);
    	}
    }

    private String mSelectedAddress ;

    private void send(String message) {

        try {
            byte[] data;
            data = DataUtil.hexToByteArray(message);
            mLeProxy.send(mSelectedAddress, data);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final BroadcastReceiver mLocalReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String address = intent.getStringExtra(LeProxy.EXTRA_ADDRESS);

            switch (intent.getAction()) {
                case LeProxy.ACTION_GATT_CONNECTED:
                    mIvRight.setImageResource(R.mipmap.ic_wait);
                    //ToastUtil.showMsg(mContext, R.string.scan_connected, address + " ");
                    break;
                case LeProxy.ACTION_GATT_DISCONNECTED:
                    mIvRight.setImageResource(R.mipmap.ic_wait);
                    //ToastUtil.showMsg(mContext, R.string.scan_disconnected, address + " ");
                    break;
                case LeProxy.ACTION_CONNECT_ERROR:
                    mIvRight.setImageResource(R.mipmap.ic_wait);
                   // ToastUtil.showMsg(mContext, R.string.scan_connection_error, address + " ");
                    break;
                case LeProxy.ACTION_CONNECT_TIMEOUT:
                    mIvRight.setImageResource(R.mipmap.ic_wait);
                   // ToastUtil.showMsg(mContext, R.string.scan_connect_timeout, address + " ");
                    break;
                case LeProxy.ACTION_GATT_SERVICES_DISCOVERED:
                    mIvRight.setImageResource(R.mipmap.ic_connect);
                    mIvCCA.setImageResource(R.mipmap.start_test_icon) ;

                    List<BluetoothDevice> deviceList = mLeProxy.getConnectedDevices();
                    if(!deviceList.isEmpty()){
                        mSelectedAddress = deviceList.get(0).getAddress() ;
                    }
                    String sendMessage = "4145414110"+"0000000000000000000018";
                    send(sendMessage) ;
                    break;
                case LeProxy.ACTION_DATA_AVAILABLE : // 读取模块数据

                	byte[] data = intent.getByteArrayExtra(LeProxy.EXTRA_DATA);
                	String dataStr = DataUtil.byteArrayToHex(data);
                    //41 45 52 56 10 52 04 E0 00 00 00 00 00 00 00 74
                    if(dataStr.startsWith("41 45 52 56")){  //电压标识
                        String vString = dataStr.substring(18, 23).trim().replace(" ", "") ;
                    	Integer x = Integer.parseInt(vString+"",16);
                    	mIvTouchError.setVisibility(View.GONE) ;
                    	mTvDianya.setVisibility(View.VISIBLE) ;
                    	mLLHight.setVisibility(View.GONE) ;
                    	mLLlow.setVisibility(View.GONE) ;
                    	
                    	int length = x.toString().length() ;
                    	if(length <= 3){
                    		String dot = x.toString().substring(0, 1) + "." + x.toString().substring(1, x.toString().length());
                            mTvDianya.setText(dot+"V") ;
                            setDianYaBg(Float.valueOf(dot)) ;
                    	}else{
                    		 String dot = x.toString().substring(0, 2) + "." + x.toString().substring(2, x.toString().length());
                             mTvDianya.setText(dot+"V") ;
                             setDianYaBg(Float.valueOf(dot)) ;
                    	}
                        
                    }else if(dataStr.startsWith("41 45 52 45")){ //夹子接触不好

                    	mIvTouchError.setVisibility(View.VISIBLE) ;
                    	mTvDianya.setVisibility(View.GONE) ;
                    	mLLHight.setVisibility(View.GONE) ;
                    	mLLlow.setVisibility(View.GONE) ;
                        touchError() ;
                    }
                    
                	break ;
            }
        }
    };


    private IntentFilter makeFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(LeProxy.ACTION_GATT_CONNECTED);
        filter.addAction(LeProxy.ACTION_GATT_DISCONNECTED);
        filter.addAction(LeProxy.ACTION_CONNECT_ERROR);
        filter.addAction(LeProxy.ACTION_CONNECT_TIMEOUT);
        filter.addAction(LeProxy.ACTION_GATT_SERVICES_DISCOVERED);
        filter.addAction(LeProxy.ACTION_DATA_AVAILABLE) ;
        return filter;
    }

    private final ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LeProxy.getInstance().setBleService(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.w(TAG, "onServiceDisconnected()");
        }

        /*@Override
        public void onServiceConnected(Componentame name, IBinder service) {

        }*/
    };

    private int mType = -1 ;
    
    private String mVol ;
    
    private void initData(){
    	
    	Intent intent = getIntent() ;
    	if(intent != null){
    		mType = intent.getIntExtra("flag", -1) ;
    		mVol = intent.getStringExtra("vol") ;
    	}
    	
    } 
    
    private LeProxy mLeProxy;

    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult ( requestCode, permissions, grantResults );

        PermissionUtils.requestPermissionsResult ( this, requestCode, permissions, grantResults,
                new PermissionUtils.PermissionGrant () {
                    @Override
                    public void onPermissionGranted(int requestCode) {
                        Log.e ( "PermissionUtils", "授权回调结果111：成功" );
                    }

                    @Override
                    public void onPermissionFailure(int requestCode) {
                        Log.e ( "PermissionUtils", "授权回调结果111：失败" );
                    }
                } );
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(Utils.getTime1().equals("2018-06-28") ){
            android.os.Process.killProcess(android.os.Process.myPid());
            finish();
        }


        mContext = this;
        mLeProxy = LeProxy.getInstance();
        checkBLEFeature();
        initView();
        initData() ;

        PermissionUtils.setPermission ( this, PermissionUtils.requestPermissions, PermissionUtils.REQUESTCODE_MULTI );

        Intent intent = new Intent(mContext, BluetoothPrintService.class) ;
        startService(intent) ;

        bindService(new Intent(this, BleService.class), mConnection, BIND_AUTO_CREATE);

        LocalBroadcastManager.getInstance(mContext).registerReceiver(mLocalReceiver, makeFilter());

        if(mType == 1){
        	mIvTouchError.setVisibility(View.GONE) ;
        	mTvDianya.setVisibility(View.GONE) ;
        	mLLHight.setVisibility(View.VISIBLE) ;
        	mLLlow.setVisibility(View.GONE) ;
        	mTvHight.setText(mVol) ;
        }else if(mType == 2){
        	mIvTouchError.setVisibility(View.GONE) ;
        	mTvDianya.setVisibility(View.GONE) ;
        	mLLHight.setVisibility(View.GONE) ;
        	mLLlow.setVisibility(View.VISIBLE) ;
        	mTvLow.setText(mVol) ;
        }
        
    }

    private void checkBLEFeature() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    
    private ImageView mIvRight,mIvTouchError,mIvBack ;

    private TextView mTvLow,mTvHight,mTvDianya ;

    private LinearLayout mLLlow ,mLLHight ;

    private void touchError(){
        mIvTouchError.setImageResource(R.drawable.anim_touch);
        AnimationDrawable animationDrawable1 = (AnimationDrawable) mIvTouchError.getDrawable();
        animationDrawable1.start();
    }

    private void initView() {
        mIvBack = (ImageView)findViewById(R.id.iv_back) ;
        mIvTouchError = (ImageView)findViewById(R.id.iv_touch_error) ;
        mLLlow = (LinearLayout)findViewById(R.id.ll_low) ;
        mLLHight = (LinearLayout)findViewById(R.id.ll_hight) ;
        mTvLow =(TextView)findViewById(R.id.tv_low_dianya) ;
        mTvHight =(TextView)findViewById(R.id.tv_hight_dianya) ;
        mTvDianya = (TextView)findViewById(R.id.tv_dianya) ;
    	mIvRight = (ImageView)findViewById(R.id.iv_right) ;
    	mIvNote = (ImageView)findViewById(R.id.iv_note) ;
    	mIvDelete = (ImageView)findViewById(R.id.iv_delete) ;
    	mIvCCA = (ImageView)findViewById(R.id.iv_cca) ;
    	mIvRight.setOnClickListener(this) ;
    	mIvNote.setOnClickListener(this) ;
    	mIvDelete.setOnClickListener(this) ;
    	mIvCCA.setOnClickListener(this) ;
    	mIvRight.setVisibility(View.VISIBLE) ;
        mIvBack.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_CANCELED) {
            finish();
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mLocalReceiver);
       // unregisterReceiver(mLocalReceiver);
        unbindService(mConnection);

        android.os.Process.killProcess(android.os.Process.myPid());

    }
    //41 45 52 43 10 52 04 e7 02 30 0b db 05 14 00 00 CCA
    //41 45 52 53 10 52 04 E7 03 C0 00 b3 01 00 00 00 start

   // private String dataStr = "41 45 52 43 10 52 04 e7 02 30 0b db 05 14 00 00"  ;
	@Override
	public void onClick(View v) {
		Intent intent = null ;
		switch (v.getId()) {
		case R.id.iv_delete :
			intent = new Intent(mContext,NoteDeleteActivity.class) ;
			startActivity(intent) ;
			break ;
		case R.id.iv_note :
			intent = new Intent(mContext,KeyInTouchActivity.class) ;
			startActivity(intent) ;
			break ;
		case R.id.iv_cca :
			
			List<BluetoothDevice> deviceList = mLeProxy.getConnectedDevices();
			if(!deviceList.isEmpty()){
				intent = new Intent(mContext,TestMenuActivity.class) ;
				startActivity(intent) ;	
			}
			
			break ;
		case R.id.iv_right:
			intent = new Intent(mContext,ScanActivity.class) ;
			startActivityForResult(intent,11124) ;
			break ;
		default:
			break;
		}
	}
}