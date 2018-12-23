package com.zzteck.cardect.btprinter.util;

import java.io.File;
import java.io.FilenameFilter;

/**
 *
 */
public class FileListFilter implements FilenameFilter {

	//public static String SUFFIX_TXT = "txt";
	public static String SUFFIX_PRN = "prn";
	public static String[] SUFFIX_IMG = { "jpg", "png", "jpeg" };

	protected String suffix;
	protected boolean isImage;

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public void isImage(boolean isImage) {
		this.isImage = isImage;
	}

	protected boolean isOnlyDir;

	public FileListFilter() {
	}

	/**
	 * 是否只显示文件夹
	 * 
	 * @param isOnlyDir
	 */
	public void setOnlyDir(boolean isOnlyDir) {
		this.isOnlyDir = isOnlyDir;
	}

	@Override
	public boolean accept(File dir, String filename) {
		String fullpath = dir.getPath() + File.separator + filename;
		if (isOnlyDir) {
			return onlyDir(fullpath);
		} else {
			return onlyFile(fullpath);
		}
	}

	/**
	 * 只显示文件夹
	 * 
	 * @param fullpath
	 *            当前路径
	 * @return
	 */
	protected boolean onlyDir(String fullpath) {
		File file = new File(fullpath);
		if (file.isDirectory()) {
			// 文件夹可读且不是空文件夹
			if (file.canRead() == false || file.list() == null
					|| file.list().length == 0) {
				return false;
			}
			return true;
		}
		return false;
	}

	private boolean isSupportFile(String filenm) {
		if (isImage == false) {
			if (StringUtils.isEmpty(suffix) == false
					&& filenm.toLowerCase().endsWith(suffix.toLowerCase()) == false) {
				return false;
			}
			return true;
		} else {
			for (String imgtype : SUFFIX_IMG) {
				if (filenm.toLowerCase().endsWith(imgtype)) {
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * 只显示文件
	 * 
	 * @param fullpath
	 *            当前路径
	 * @return
	 */
	protected boolean onlyFile(String fullpath) {
		File file = new File(fullpath);

		if (file.isFile() && file.canRead()) {
			return isSupportFile(file.getName());
		}
		
		return false;
		
	/*

		if (StringUtils.isEmpty(suffix) == false
				&& file.getName().toLowerCase().endsWith(suffix.toLowerCase()) == false) {
			return false;
		}
		if (file.isFile() && file.canRead()) {
			// 文件可读
			return true;
		}
		return false;*/
	}
}
