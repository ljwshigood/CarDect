package com.zzteck.cardect.bean;

import android.content.Context;

import com.zzteck.cardect.R;


public class DeviceBean {

	private String deviceNm;
	private String deviceAddress;
	private int connectState;
	private static DeviceBean instance = new DeviceBean();

	private DeviceBean() {
	}

	public static DeviceBean getInstance() {
		return instance;
	}

	public String getDeviceNm() {
		return deviceNm;
	}

	public void setDeviceNm(String deviceNm) {
		this.deviceNm = deviceNm;
	}

	public String getDeviceAddress() {
		return deviceAddress;
	}

	public void setDeviceAddress(String deviceAddress) {
		this.deviceAddress = deviceAddress;
	}

	public int getConnectState() {
		return connectState;
	}

	public void setConnectState(int connectState) {
		this.connectState = connectState;
	}

	public String getDeviceState(Context context) {
		String msgtitle = context.getString(R.string.msg_info_title);
		String msg = "";
		if (connectState != 0) {
			msg = context.getString(connectState);
		}
		return String.format(msgtitle, msg);
	}

	public void clear() {
		deviceNm = null;
		deviceAddress = null;
		connectState = 0;
	}
}
