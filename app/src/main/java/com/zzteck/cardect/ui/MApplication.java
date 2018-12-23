package com.zzteck.cardect.ui;

import android.app.Application;

import com.uuzuche.lib_zxing.activity.ZXingLibrary;

/**
 * Created by aaron on 16/9/7.
 */

public class MApplication extends Application{

	
	public static String mFileName ;
	
	public static String retMessage ;

	public static String mBarCode ;

	public static String mBatteryMode ;

	public static String mCarPlate ;

	public static String mName ;
	
	public static String mWorkShop ;
	
	public static boolean isConnect ;
	
    @Override
    public void onCreate() {
        super.onCreate();
        ZXingLibrary.initDisplayOpinion(this);
    }
}
