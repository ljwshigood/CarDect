package com.zzteck.cardect.bean;

public class SettingBean {
	
	private int imageMaxWidth;
//	private int imageMaxHeight;
//	private int imagePrintWidth;
//	private int imagePrintHeight;
	

	private static SettingBean instance = new SettingBean();

	private SettingBean() {
	}

	public static SettingBean getInstance() {
		return instance;
	}

	public int getImageMaxWidth() {
		return imageMaxWidth;
	}

//	public int getImagePrintWidth() {
//		return imagePrintWidth;
//	}
//
//	public void setImagePrintWidth(int imagePrintWidth) {
//		this.imagePrintWidth = imagePrintWidth;
//	}
//
//	public int getImagePrintHeight() {
//		return imagePrintHeight;
//	}
//
//	public void setImagePrintHeight(int imagePrintHeight) {
//		this.imagePrintHeight = imagePrintHeight;
//	}

	public void setImageMaxWidth(int imageMaxWidth) {
		this.imageMaxWidth = imageMaxWidth;
	}

//	public int getImageMaxHeight() {
//		return imageMaxHeight;
//	}
//
//	public void setImageMaxHeight(int imageMaxHeight) {
//		this.imageMaxHeight = imageMaxHeight;
//	}

	public void clear() {
		imageMaxWidth = 0;
		//imageMaxHeight = 0;
	}

}
