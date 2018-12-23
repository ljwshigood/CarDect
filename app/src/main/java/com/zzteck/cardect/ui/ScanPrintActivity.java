package com.zzteck.cardect.ui;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ble.api.DataUtil;
import com.ble.ble.LeScanRecord;
import com.zzteck.cardect.R;
import com.zzteck.cardect.bean.LeDevice;
import com.zzteck.cardect.util.LeProxy;
import com.zzteck.cardect.util.ToastUtil;

import java.util.ArrayList;

public class ScanPrintActivity extends Activity implements View.OnClickListener{
    private final static String TAG = "ScanFragment";
    private static final long SCAN_PERIOD = 5000;

    private LeDeviceListAdapter mLeDeviceListAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler = new Handler();
    private boolean mScanning;
    private LeProxy mLeProxy;

    private SwipeRefreshLayout mRefreshLayout;
    
    private Context mContext ;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);
                if (state == BluetoothAdapter.STATE_ON) {
                    scanLeDevice(true);
                }
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_scan) ;
        mLeDeviceListAdapter = new LeDeviceListAdapter();
    	initView();
        mLeProxy = LeProxy.getInstance();
        mContext = ScanPrintActivity.this ;
        BluetoothManager bm = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bm.getAdapter();

        LocalBroadcastManager.getInstance(mContext).registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));

    }
    
    @Override
    public void onResume() {
        super.onResume();
        scanLeDevice(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        scanLeDevice(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mReceiver);

     //   unregisterReceiver(mReceiver);
    }

    private ImageView mIvBack ;

    private ListView mLvDevice ;

    private void initView() {
        mIvBack  = (ImageView)findViewById(R.id.iv_back) ;
        mIvBack.setOnClickListener(this);

        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                scanLeDevice(true);
            }
        });

        mLvDevice = (ListView) findViewById(R.id.listView1);
        mLvDevice.setAdapter(mLeDeviceListAdapter);
        mLvDevice.setOnItemClickListener(mOnItemClickListener);
        mLvDevice.setOnItemLongClickListener(mOnItemLongClickListener);
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            if (mBluetoothAdapter.isEnabled()) {
                if (mScanning) return;
                mScanning = true;
                mRefreshLayout.setRefreshing(true);
                mLeDeviceListAdapter.clear();
                mHandler.postDelayed(mScanRunnable, SCAN_PERIOD);
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            } else {
                ToastUtil.showMsg(mContext, R.string.scan_bt_disabled);
            }
        } else {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            mRefreshLayout.setRefreshing(false);
            mHandler.removeCallbacks(mScanRunnable);
            mScanning = false;
        }
    }

    private final Runnable mScanRunnable = new Runnable() {

        @Override
        public void run() {
            scanLeDevice(false);
        }
    };

    private final OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //单击连接设备
            scanLeDevice(false);
            LeDevice device = mLeDeviceListAdapter.getItem(position);
            boolean ok = mLeProxy.connect(device.getAddress(), false);
            if(ok){
                setResult(1122);
                finish();
            }
            Log.e(TAG, device.getAddress() + " connect():" + ok);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 233) {
            scanLeDevice(true);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private final OnItemLongClickListener mOnItemLongClickListener = new OnItemLongClickListener() {

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            //长按查看广播数据
            /*LeDevice device = mLeDeviceListAdapter.getItem(position);
            showAdvDetailsDialog(device);*/
            return true;
        }
    };

    //显示广播数据
    private void showAdvDetailsDialog(LeDevice device) {
        LeScanRecord record = device.getLeScanRecord();

        StringBuilder sb = new StringBuilder();
        sb.append(device.getAddress() + "\n\n");
        sb.append('[' + DataUtil.byteArrayToHex(record.getBytes()) + "]\n\n");
        sb.append(record.toString());

        TextView textView = new TextView(ScanPrintActivity.this);
        textView.setPadding(32, 32, 32, 32);
        textView.setText(sb.toString());

        Dialog dialog = new Dialog(ScanPrintActivity.this);
        dialog.setTitle(device.getName());
        dialog.setContentView(textView);
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back :
                finish();
                break ;
        }
    }

    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<LeDevice> mLeDevices;
        private LayoutInflater mInflater;

        LeDeviceListAdapter() {
            mLeDevices = new ArrayList<>();
            mInflater = getLayoutInflater();
        }

        void addDevice(LeDevice device) {
            if (!mLeDevices.contains(device)) {
                mLeDevices.add(device);
            }
        }

        void clear() {
            mLeDevices.clear();
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public LeDevice getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;

            if (view == null) {
                view = mInflater.inflate(R.layout.item_device_list, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                viewHolder.deviceRssi = (TextView) view.findViewById(R.id.txt_rssi);
                viewHolder.connect = (TextView) view.findViewById(R.id.btn_connect);
                viewHolder.connect.setVisibility(View.VISIBLE);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            LeDevice device = mLeDevices.get(i);
            String deviceName = device.getName();
            if (!TextUtils.isEmpty(deviceName))
                viewHolder.deviceName.setText(deviceName);
            else
                viewHolder.deviceName.setText(R.string.unknown_device);
            viewHolder.deviceAddress.setText(device.getAddress());
            viewHolder.deviceRssi.setText("rssi: " + device.getRssi() + "dbm");

            return view;
        }
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                	
                	if( device.getName() != null && device.getName().startsWith("BESA")){
                	    mLeDeviceListAdapter.addDevice(new LeDevice(device.getName(), device.getAddress(), rssi, scanRecord));
                        mLeDeviceListAdapter.notifyDataSetChanged();
                	}
                }
            });
        }
    };

    private static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
        TextView deviceRssi;
        TextView connect;
    }
}