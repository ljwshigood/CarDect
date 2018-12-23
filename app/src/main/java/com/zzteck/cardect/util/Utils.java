package com.zzteck.cardect.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

	public static String makeChecksum(String data) {
		if (data == null || data.equals("")) {
			return "";
		}
		int total = 0;
		int len = data.length();
		int num = 0;
		while (num < len) {
			String s = data.substring(num, num + 2);
			System.out.println(s);
			total += Integer.parseInt(s, 16);
			num = num + 2;
		}
		/**
		 * 用256求余最大是255，即16进制的FF
		 */
		int mod = total % 256;
		String hex = Integer.toHexString(mod);
		len = hex.length();
		// 如果不够校验位的长度，补0,这里用的是两位校验
		if (len < 2) {
			hex = "0" + hex;
		}
		return hex;
	}
	
	public static String int2Hex(Integer x){
		String hex = x.toHexString(x);
		return hex ;
	}

	public static Integer hex2Int(String hex){
		Integer x = Integer.parseInt(hex,16);
		return x ;
	}

	public static String getTime1(){
		long time=System.currentTimeMillis();//long now = android.os.SystemClock.uptimeMillis();
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		Date d1=new Date(time);
		String t1=format.format(d1);
		return t1 ;
		//  Log.e("msg", t1);
	}

	public static String getTime(){
        long time=System.currentTimeMillis();//long now = android.os.SystemClock.uptimeMillis();  
        SimpleDateFormat format=new SimpleDateFormat("dd-MM-yyyy HH:mm");
        Date d1=new Date(time);
        String t1=format.format(d1);
		return t1 ;
      //  Log.e("msg", t1);
    }  

	
}
