package com.zzteck.cardect.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.zzteck.cardect.R;
import com.zzteck.cardect.adapter.NoteAdapter;

public class NoteViewActivity extends Activity implements OnClickListener{

	private ListView mLvNote ;
	
	private NoteAdapter mNoteAdapter ;
	
	private ImageView mIvBack ; 
	
	private void initView(){
		mIvBack = (ImageView)findViewById(R.id.iv_back) ;
		mLvNote = (ListView)findViewById(R.id.lv_note) ;
		mIvBack.setOnClickListener(this) ;
	}
	
	private void initData(){
		mNoteAdapter = new NoteAdapter(mContext, null) ;
		mLvNote.setAdapter(mNoteAdapter) ;
	}
	
	private Context mContext ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_note_view) ;
		mContext = NoteViewActivity.this ;
		initView() ;
		initData() ;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			finish() ;
			break;

		default:
			break;
		}
	}
	
}
