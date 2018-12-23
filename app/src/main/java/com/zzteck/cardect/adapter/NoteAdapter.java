package com.zzteck.cardect.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zzteck.cardect.R;
import com.zzteck.cardect.bean.FileBean;

public class NoteAdapter extends BaseAdapter {

	private List<FileBean> mFileList;
	private Context mContext;

	private LayoutInflater mLayoutInflater;

	public NoteAdapter(Context context, List<FileBean> fileList) {
		this.mContext = context;
		this.mFileList = fileList;
		mLayoutInflater = LayoutInflater.from(context);
	}

	public void notifyNoteApdater(List<FileBean> fileList){
		this.mFileList = fileList;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mFileList == null ? 0 : mFileList.size();
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

			convertView = mLayoutInflater.inflate(R.layout.item_note, null);
			viewHolder = new ViewHolder();
			viewHolder.mTvName = (TextView) convertView.findViewById(R.id.tv_name);
			convertView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		FileBean bean = mFileList.get(position) ;
		viewHolder.mTvName.setText(bean.getFileName()) ;
		return convertView;
	}

	class ViewHolder {
		TextView mTvName;
	}

}
