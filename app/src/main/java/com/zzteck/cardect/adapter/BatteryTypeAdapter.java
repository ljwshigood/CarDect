package com.zzteck.cardect.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.zzteck.cardect.R;
import com.zzteck.cardect.bean.BatteryBean;
import com.zzteck.cardect.ui.CCASetActivity;

public class BatteryTypeAdapter extends BaseAdapter/* implements OnItemClickListener */{

	private List<BatteryBean> mParamList;
	private Context mContext;

	private LayoutInflater mLayoutInflater;

	public BatteryTypeAdapter(Context context, List<BatteryBean> fileList) {
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

			convertView = mLayoutInflater.inflate(R.layout.item_battery, null);
			viewHolder = new ViewHolder();
			viewHolder.mIvName = (ImageView) convertView.findViewById(R.id.iv_name);
			convertView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		BatteryBean bean = mParamList.get(position);
		viewHolder.mIvName.setImageResource(bean.getRes()) ;
		return convertView;
	}

	class ViewHolder {
		ImageView mIvName;
	}

}
