package com.zzteck.cardect.ui;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.zzteck.cardect.R;
import com.zzteck.cardect.adapter.NoteAdapter;
import com.zzteck.cardect.bean.FileBean;
import com.zzteck.cardect.ui.NoteDeleteActivity;
import com.zzteck.cardect.util.FileUtils;

public class NoteDeleteActivity extends Activity implements View.OnClickListener{

private ImageView mIvBack ;
	
	private NoteAdapter mNoteAdapter ;
	
	private Context mContext ;
	
	private ListView mLvFileList ;

	private ImageView mIvConnect ;

	@Override
	protected void onResume() {
		super.onResume();
		setImageViewConnectStatus() ;
	}

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
	private TextView mTvMainInfo ;

	private void initView(){
		mTvMainInfo = (TextView) findViewById(R.id.tv_main) ;
		mTvMainInfo.setText("SEARCH REPORT");
		mLvFileList = (ListView)findViewById(R.id.lv_note) ;
		mIvBack = (ImageView)findViewById(R.id.iv_back) ;
		mIvBack.setOnClickListener(this) ;
	}
	
	private ArrayList<FileBean> mFileList = new ArrayList<>() ;
	
	private void initData(){
		FileUtils.getFiles(mFileList, Environment.getExternalStorageDirectory()+File.separator+"zzteck") ;
		mNoteAdapter = new NoteAdapter(mContext, mFileList) ;
		mLvFileList.setAdapter(mNoteAdapter) ;
		mLvFileList.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,long arg3) {
				
				FileBean bean = mFileList.get(position) ;
				
				File file = new File(bean.getFilePath()) ;
				
				String filePath = file.getName() ;
				
				String tempFilePath = filePath.substring(0,filePath.length() - 4) ;
				
				String fileParentPath = file.getParent() ;
				
				String relFilePath = fileParentPath+ "json/"+tempFilePath+"json"+".txt" ;
				
				ArrayList<String> message = FileUtils.ReadTxtFile(relFilePath) ;
				//String type = message.substring(0,1) ;
				
				Intent intent = new Intent(mContext,ResultActivity.class) ;
				intent.putExtra("message_list",message) ;
				intent.putExtra("filePath",file.getAbsolutePath());
				intent.putExtra("real_filePath",relFilePath);
				intent.putExtra("type",1) ;
				startActivity(intent) ;

			}
		}) ;
		
		mLvFileList.setOnItemLongClickListener(new OnItemLongClickListener() {
			
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,int position, long arg3) {
				
				FileBean bean = mFileList.get(position) ;
				
				File file = new File(bean.getFilePath()) ;
				
				String filePath = file.getName() ;
				
				String tempFilePath = filePath.substring(filePath.length() - 3, filePath.length()) ;
				
				String fileParentPath = file.getParent() ;
				
				String relFilePath = fileParentPath+ tempFilePath+"json"+".txt" ;
				
				Intent intent = new Intent(mContext,DeleteActivity.class) ;
				intent.putExtra("filePath", relFilePath) ;
				intent.putExtra("filePath1", file.getAbsolutePath()) ;
				startActivityForResult(intent,1100);
				return true ;
			}
		}) ;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 1100){
			mFileList.clear();
			FileUtils.getFiles(mFileList, Environment.getExternalStorageDirectory()+File.separator+"zzteck") ;
			mNoteAdapter.notifyNoteApdater(mFileList);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_note_delete) ;
		mContext = NoteDeleteActivity.this ;
		initView() ;
		initData() ;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.iv_back:
			finish() ;
			break;

		default:
			break;
		}
	}
}
