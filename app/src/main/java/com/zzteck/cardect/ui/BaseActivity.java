package com.zzteck.cardect.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.os.Bundle;

import com.zzteck.cardect.R;

public class BaseActivity extends FragmentActivity {

	private boolean isConnnect ;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
		 if (getRequestedOrientation () != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
	         setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );
	     }
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public void onResume() {
        super.onResume();
    }
    
}
