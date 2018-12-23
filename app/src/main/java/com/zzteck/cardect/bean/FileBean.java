package com.zzteck.cardect.bean;

import java.io.Serializable;

public class FileBean implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String filePath ;
	
	private String fileName ;

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
}
