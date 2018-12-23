package com.zzteck.cardect.ui;

import java.io.File;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.webkit.WebView.FindListener;
import android.widget.ImageView;
import android.content.Intent;
import android.os.Bundle;

import com.zzteck.cardect.R;

public class DeleteActivity extends FragmentActivity {

	private ImageView mIvDelete ;

	private String mFilePath ,mFilePath1 ;
	
	private void getIntentData(){
		Intent intent = getIntent() ;
		mFilePath = intent.getStringExtra("filePath") ;
		mFilePath1 = intent.getStringExtra("filePath1") ;
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
    	setFinishOnTouchOutside(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);
		getIntentData() ;
        mIvDelete = (ImageView)findViewById(R.id.iv_delete) ;
        mIvDelete.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				File file = new File(mFilePath) ;
				if(file.exists()){
					file.delete() ;
				}
				
				File file1 = new File(mFilePath1) ;
				if(file1.exists()){
					file1.delete() ;
				}
				setResult(1000);
                finish();
			}
		}) ;
        
    }

}
