package com.zzteck.cardect.btprinter.util;

import android.text.format.DateFormat;

import java.util.Date;

public class StringUtils {

	public static String DATE_FORMAT1 = "yyyyMMddkkmmss";

	/**
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		if (str != null && str.length() != 0) {
			return false;
		}
		return true;
	}

	public static String getCurrentDate(String format) {
		Date curDate = new Date(System.currentTimeMillis());
		String date = DateFormat.format(format, curDate).toString();
		return date;
	}

}
