package com.zzteck.cardect.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.zzteck.cardect.R;
import com.zzteck.cardect.bean.BatteryBean;
import com.zzteck.cardect.bean.FileBean;

public class CCASetAdapter extends BaseAdapter/* implements OnItemClickListener*/{

	private List<BatteryBean> mParamList;
	private Context mContext;

	private LayoutInflater mLayoutInflater;

	public CCASetAdapter(Context context, List<BatteryBean> fileList) {
		this.mContext = context;
		this.mParamList = fileList;
		mLayoutInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return mParamList == null ? 0 : mParamList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.item_battery_param, null);
			viewHolder = new ViewHolder();
			viewHolder.mIvParam = (ImageView) convertView.findViewById(R.id.iv_param);
			convertView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		BatteryBean bean = mParamList.get(position) ;
		if(bean.getSelect()){
			viewHolder.mIvParam.setImageResource(bean.getResPress());
		}else{
			viewHolder.mIvParam.setImageResource(bean.getRes());		
		}
	
		return convertView;
	}

	class ViewHolder {
		ImageView mIvParam;
	}

}
