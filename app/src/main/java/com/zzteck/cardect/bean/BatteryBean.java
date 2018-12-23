package com.zzteck.cardect.bean;

import java.io.Serializable;

public class BatteryBean implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int res ;
	
	private int resPress ;
	
	private boolean isSelect ;
	
	public boolean getSelect() {
		return isSelect;
	}

	public void setSelect(boolean isSelect) {
		this.isSelect = isSelect ;
	}
	
	public int getResPress() {
		return resPress;
	}

	public void setResPress(int res) {
		this.resPress = res;
	}

	public int getRes() {
		return res;
	}

	public void setRes(int res) {
		this.res = res;
	}
	
}
